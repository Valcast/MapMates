package com.example.socialmeetingapp.data.repository

import android.net.Uri
import android.util.Log
import com.example.socialmeetingapp.data.utils.MissingFieldException
import com.example.socialmeetingapp.data.utils.toUser
import com.example.socialmeetingapp.data.utils.toUserPreview
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.SignUpStatus
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.model.UserPreview
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant

class FirebaseUserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : UserRepository {

    override fun isUserAuthenticated(): Boolean = firebaseAuth.currentUser != null

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val userDocumentRef = db.collection("users").document(firebaseAuth.currentUser!!.uid).get().await()

            Result.Success(userDocumentRef.toUser())
        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Failed to fetch user: ${e.message}")
        } catch (e: MissingFieldException) {
            return Result.Error(e.message ?: "Failed to load user")
        }
    }

    override suspend fun getCurrentUserPreview(): Result<UserPreview> {
        return try {
            val userDocumentRef = db.collection("users").document(firebaseAuth.currentUser!!.uid).get().await()

            Result.Success(userDocumentRef.toUserPreview())
        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Failed to fetch user: ${e.message}")
        } catch (e: MissingFieldException) {
            return Result.Error(e.message ?: "Failed to load user")
        }
    }

    override suspend fun getUser(id: String): Result<User> {
        return try {
            val userDocumentRef = db.collection("users").document(id).get().await()

            Result.Success(userDocumentRef.toUser())
        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Failed to fetch user: ${e.message}")
        } catch (e: MissingFieldException) {
            return Result.Error(e.message ?: "Failed to load user")
        }
    }

    override suspend fun getUserPreview(id: String): Result<UserPreview> {
        return try {
            val userDocumentRef = db.collection("users").document(id).get().await()

            Result.Success(userDocumentRef.toUserPreview())
        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Failed to fetch user: ${e.message}")
        } catch (e: MissingFieldException) {
            return Result.Error(e.message ?: "Failed to load user")
        }
    }

    override suspend fun getUsers(ids: List<String>): Result<List<User>> {
        try {
            if (ids.isEmpty()) return Result.Success(emptyList())
            val userDocumentRefs = db.collection("users").whereIn("id", ids).get().await()
            val users = userDocumentRefs.documents.mapNotNull { userDocument ->
                try {
                    userDocument.toUser()
                } catch (e: MissingFieldException) {
                    Log.e("FirebaseUserRepositoryImpl", "${e.message} for ${userDocument.id}")
                    null
                }
            }
            return Result.Success(users)

        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Failed to fetch users in batch: ${e.message}")
        }
    }

    override suspend fun getUsersPreviews(ids: List<String>): Result<List<UserPreview>> {
        try {
            if (ids.isEmpty()) return Result.Success(emptyList())
            val userDocumentRefs = db.collection("users").whereIn("id", ids).get().await()
            val users = userDocumentRefs.documents.mapNotNull { userDocument ->
                try {
                    userDocument.toUserPreview()
                } catch (e: MissingFieldException) {
                    Log.e("FirebaseUserRepositoryImpl", "${e.message} for ${userDocument.id}")
                    null
                }
            }
            return Result.Success(users)

        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Failed to fetch users in batch: ${e.message}")
        }
    }

    override suspend fun signUp(
        email: String, password: String
    ): Result<Unit> {
        return try {
            val authResult: AuthResult =
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            val currentMoment = Timestamp(Clock.System.now().toJavaInstant())

            db.collection("users").document(authResult.user!!.uid).set(
                hashMapOf(
                    "email" to email,
                    "createdAt" to currentMoment,
                    "following" to emptyList<String>(),
                    "followers" to emptyList<String>(),
                    "gender" to "",
                    "dateOfBirth" to currentMoment,
                    "bio" to "",
                    "username" to "",
                    "profilePictureUri" to ""
                )
            )
            Result.Success(Unit)
        } catch (_: FirebaseAuthWeakPasswordException) {
            Result.Error("Password is too weak")
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            Result.Error("Invalid email address")
        } catch (_: FirebaseAuthUserCollisionException) {
            Result.Error("Email address already in use")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.Success(Unit)
        } catch (_: FirebaseAuthException) {
            Result.Error("Email address or password is incorrect")
        }

    }

    override suspend fun signUpWithGoogle(idToken: String): Result<SignUpStatus> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()

            val currentMoment = Timestamp(Clock.System.now().toJavaInstant())

            if (db.collection("users").document(authResult.user!!.uid).get().await().exists()) {
                return Result.Success(SignUpStatus.ExistingUser)
            }

            db.collection("users").document(authResult.user!!.uid).set(
                hashMapOf(
                    "email" to authResult.user!!.email,
                    "createdAt" to currentMoment,
                    "gender" to "",
                    "dateOfBirth" to currentMoment,
                    "following" to emptyList<String>(),
                    "followers" to emptyList<String>(),
                    "bio" to "",
                    "username" to "",
                    "profilePictureUri" to ""
                )
            )

            Result.Success(SignUpStatus.NewUser)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        try {
            val userDocument = db.collection("users").document(firebaseAuth.currentUser!!.uid)

            userDocument.set(
                hashMapOf(
                    "gender" to user.gender,
                    "dateOfBirth" to Timestamp(user.dateOfBirth.toInstant(TimeZone.UTC).toJavaInstant()),
                    "bio" to user.bio,
                    "username" to user.username,
                    "profilePictureUri" to user.profilePictureUri.toString(),
                ), SetOptions.merge()
            ).await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun updateFriendship(
        friendID: String, action: FieldValue
    ): Result<Unit> {
        try {
            val currentUserId = firebaseAuth.currentUser!!.uid
            val currentUserDocument = db.collection("users").document(currentUserId)
            val friendDocument = db.collection("users").document(friendID)

            currentUserDocument.update("following", action).await()
            friendDocument.update(
                "followers", if (action.javaClass == FieldValue.arrayUnion().javaClass) {
                    FieldValue.arrayUnion(currentUserId)
                } else {
                    FieldValue.arrayRemove(currentUserId)
                }
            ).await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun followUser(friendID: String): Result<Unit> {

        return updateFriendship(friendID, FieldValue.arrayUnion(friendID))
    }

    override suspend fun unfollowUser(friendID: String): Result<Unit> {
        return updateFriendship(friendID, FieldValue.arrayRemove(friendID))
    }

    override suspend fun deleteFollower(friendID: String): Result<Unit> {
        try {
            val currentUserId = firebaseAuth.currentUser!!.uid
            val currentUserDocument = db.collection("users").document(currentUserId)
            val friendDocument = db.collection("users").document(friendID)

            currentUserDocument.update("followers", FieldValue.arrayRemove(friendID)).await()
            friendDocument.update("following", FieldValue.arrayRemove(currentUserId)).await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun uploadProfilePicture(imageUri: Uri): Result<Uri> {
        try {
            val storageRef =
                storage.reference.child("profile_pictures/${firebaseAuth.currentUser!!.uid}")

            storageRef.putFile(imageUri).await()

            val downloadUrl = storageRef.downloadUrl.await()

            return Result.Success(downloadUrl)
        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }


    override suspend fun sendEmailVerification(): Result<Unit> {
        try {
            firebaseAuth.currentUser!!.sendEmailVerification().await()
            return Result.Success(Unit)


        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    override fun signOut() = firebaseAuth.signOut()
}