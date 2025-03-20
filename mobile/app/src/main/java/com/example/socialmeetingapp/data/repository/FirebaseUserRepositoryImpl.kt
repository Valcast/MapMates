package com.example.socialmeetingapp.data.repository

import android.net.Uri
import android.util.Log
import com.example.socialmeetingapp.data.source.FollowersPagingSource
import com.example.socialmeetingapp.data.source.FollowingPagingSource
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
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant

class FirebaseUserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : UserRepository {

    private val userPreviewCache = mutableMapOf<String, UserPreview>()

    override val authenticationStatus = callbackFlow {
        val initialUser = firebaseAuth.currentUser
        if (initialUser != null) {
            trySend(initialUser)
        }

        val authStateListener = AuthStateListener { authState ->
            trySend(authState.currentUser)
        }

        firebaseAuth.addAuthStateListener(authStateListener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun getCurrentUser(): Result<User> =
        getUser(firebaseAuth.currentUser!!.uid)

    override suspend fun getCurrentUserPreview(): Result<UserPreview> =
        getUserPreview(firebaseAuth.currentUser!!.uid)

    override suspend fun getUser(id: String): Result<User> {
        return try {
            val userDocumentRef = db.collection("users").document(id).get().await()

            val followersCount =
                db.collection("users").document(id).collection("followers").count().get(
                    AggregateSource.SERVER
                ).await().count.toInt()

            val followingCount =
                db.collection("users").document(id).collection("following").count().get(
                    AggregateSource.SERVER
                ).await().count.toInt()

            Result.Success(userDocumentRef.toUser(followersCount, followingCount))
        } catch (e: FirebaseFirestoreException) {
            return Result.Failure(e.message ?: "Failed to fetch user: ${e.message}")
        } catch (e: MissingFieldException) {
            return Result.Failure(e.message ?: "Failed to load user")
        }
    }

    override suspend fun getUserPreview(id: String): Result<UserPreview> {
        return try {
            val userDocumentRef = db.collection("users").document(id).get().await()

            Result.Success(userDocumentRef.toUserPreview())
        } catch (e: FirebaseFirestoreException) {
            return Result.Failure(e.message ?: "Failed to fetch user: ${e.message}")
        } catch (e: MissingFieldException) {
            return Result.Failure(e.message ?: "Failed to load user")
        }
    }

    override suspend fun getUsersPreviews(ids: List<String>): Result<List<UserPreview>> {
        try {
            if (ids.isEmpty()) return Result.Success(emptyList())

            val cachedUsers = mutableListOf<UserPreview>()
            val missingIds = mutableListOf<String>()

            ids.forEach { id ->
                userPreviewCache[id]?.let { cachedUsers.add(it) } ?: missingIds.add(id)
            }

            if (missingIds.isEmpty()) {
                return Result.Success(cachedUsers)
            }

            val userDocumentRefs = db.collection("users")
                .whereIn(FieldPath.documentId(), missingIds)
                .get().await()

            val fetchedUsers = userDocumentRefs.documents.mapNotNull { userDocument ->
                try {
                    val userPreview = userDocument.toUserPreview()
                    userPreviewCache[userDocument.id] = userPreview
                    userPreview
                } catch (e: MissingFieldException) {
                    Log.e("FirebaseUserRepositoryImpl", "${e.message} for ${userDocument.id}")
                    null
                }
            }

            val combinedUsers = (cachedUsers + fetchedUsers).sortedBy { ids.indexOf(it.id) }
            return Result.Success(combinedUsers)
        } catch (e: FirebaseFirestoreException) {
            return Result.Failure(e.message ?: "Failed to fetch users in batch: ${e.message}")
        }
    }

    override fun getFollowersPagingSource(userId: String): FollowersPagingSource {
        return FollowersPagingSource(db, userId)
    }

    override fun getFollowingPagingSource(userId: String): FollowingPagingSource {
        return FollowingPagingSource(db, userId)
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
                    "gender" to "",
                    "dateOfBirth" to currentMoment,
                    "bio" to "",
                    "username" to "",
                    "profilePictureUri" to ""
                )
            )
            Result.Success(Unit)
        } catch (_: FirebaseAuthWeakPasswordException) {
            Result.Failure("Password is too weak")
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            Result.Failure("Invalid email address")
        } catch (_: FirebaseAuthUserCollisionException) {
            Result.Failure("Email address already in use")
        } catch (e: Exception) {
            Result.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.Success(Unit)
        } catch (_: FirebaseAuthException) {
            Result.Failure("Email address or password is incorrect")
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
                    "bio" to "",
                    "username" to "",
                    "profilePictureUri" to ""
                )
            )

            Result.Success(SignUpStatus.NewUser)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        try {
            val userDocument = db.collection("users").document(firebaseAuth.currentUser!!.uid)

            userDocument.set(
                hashMapOf(
                    "gender" to user.gender,
                    "dateOfBirth" to Timestamp(
                        user.dateOfBirth.toInstant(TimeZone.UTC).toJavaInstant()
                    ),
                    "bio" to user.bio,
                    "username" to user.username,
                    "profilePictureUri" to user.profilePictureUri.toString(),
                ), SetOptions.merge()
            ).await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun followUser(friendID: String): Result<Unit> {
        try {
            val currentUserId = firebaseAuth.currentUser!!.uid

            db.collection("users").document(currentUserId).collection("following")
                .document(friendID).set(
                    hashMapOf(
                        "followedAt" to Timestamp.now()
                    )
                ).await()

            db.collection("users").document(friendID).collection("followers")
                .document(currentUserId).set(
                    hashMapOf(
                        "followedAt" to Timestamp.now()
                    )
                ).await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Unknown error")
        }

    }

    override suspend fun unfollowUser(friendId: String): Result<Unit> {
        try {
            val currentUserId = firebaseAuth.currentUser!!.uid

            db.collection("users").document(currentUserId).collection("following")
                .document(friendId).delete().await()
            db.collection("users").document(friendId).collection("followers")
                .document(currentUserId).delete().await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun deleteFollower(friendId: String): Result<Unit> {
        try {
            val currentUserId = firebaseAuth.currentUser!!.uid

            db.collection("users").document(currentUserId).collection("followers")
                .document(friendId).delete().await()

            db.collection("users").document(friendId).collection("following")
                .document(currentUserId).delete().await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateProfilePicture(imageUri: Uri): Result<Uri> {
        try {
            val storageRef =
                storage.reference.child("profile_pictures/${firebaseAuth.currentUser!!.uid}")

            storageRef.putFile(imageUri).await()

            val downloadUrl = storageRef.downloadUrl.await()

            db.collection("users").document(firebaseAuth.currentUser!!.uid).update(
                "profilePictureUri", downloadUrl.toString()
            )

            return Result.Success(downloadUrl)
        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Unknown error")
        }
    }


    override suspend fun sendEmailVerification(): Result<Unit> {
        try {
            firebaseAuth.currentUser!!.sendEmailVerification().await()
            return Result.Success(Unit)


        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateUsernameAndDateOfBirth(
        username: String,
        dateOfBirth: LocalDateTime
    ): Result<Unit> {
        try {
            db.collection("users").document(firebaseAuth.currentUser!!.uid).set(
                hashMapOf(
                    "username" to username,
                    "dateOfBirth" to Timestamp(dateOfBirth.toInstant(TimeZone.UTC).toJavaInstant())
                ), SetOptions.merge()
            )


            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateBio(bio: String): Result<Unit> {
        try {
            db.collection("users").document(firebaseAuth.currentUser!!.uid).set(
                hashMapOf(
                    "bio" to bio
                ), SetOptions.merge()
            )

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Unknown error")
        }
    }

    override fun signOut() = firebaseAuth.signOut()
}