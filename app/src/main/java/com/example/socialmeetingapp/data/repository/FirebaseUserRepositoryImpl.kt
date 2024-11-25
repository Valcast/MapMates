package com.example.socialmeetingapp.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.socialmeetingapp.data.remote.NotificationService
import com.example.socialmeetingapp.data.utils.NetworkManager
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.SignUpStatus
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.repository.UserRepository
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
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class FirebaseUserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val networkManager: NetworkManager,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val dataStore: DataStore<Preferences>,
    private val notificationService: NotificationService
) : UserRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var userDataListenerRegistration: ListenerRegistration

    override val currentUser: StateFlow<Result<User?>> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser == null) {
                trySend(Result.Error("User not authenticated"))

                if (::userDataListenerRegistration.isInitialized) {
                    userDataListenerRegistration.remove()
                }
            } else {
                db.collection("users").document(auth.currentUser!!.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(Result.Error(error.message ?: "Unknown error"))
                        }

                        if (snapshot != null) {
                            if (firebaseAuth.currentUser == null) {
                                trySend(Result.Error("User not authenticated"))
                                return@addSnapshotListener
                            }

                            coroutineScope.launch {
                                val notifications = async {
                                    notificationService.getNotifications(
                                        snapshot.get("notifications") as? List<String>
                                            ?: emptyList()
                                    )
                                }

                                val user = User(
                                    id = firebaseAuth.currentUser!!.uid,
                                    email = firebaseAuth.currentUser!!.email!!,
                                    username = snapshot.getString("username")
                                        ?: return@launch,
                                    bio = snapshot.getString("bio") ?: "",
                                    dateOfBirth = snapshot.getString("dateOfBirth")?.let {
                                        if (it.isEmpty()) {
                                            Clock.System.now().toLocalDateTime(TimeZone.UTC)
                                        } else {
                                            LocalDateTime.parse(it)
                                        }
                                    } ?: return@launch,
                                    notifications = notifications.await(),
                                    following = snapshot.get("following") as List<String>?
                                        ?: return@launch,
                                    followers = snapshot.get("followers") as List<String>?
                                        ?: return@launch,
                                    gender = snapshot.getString("gender") ?: return@launch,
                                    role = snapshot.getString("role") ?: return@launch,
                                    createdAt = snapshot.getString("createdAt")
                                        ?.let { LocalDateTime.parse(it) }
                                        ?: return@launch,
                                    updatedAt = snapshot.getString("updatedAt")
                                        ?.let { try { LocalDateTime.parse(it) } catch(_: Exception) { null } },
                                    lastPasswordChange = snapshot.getString("lastPasswordChange")
                                        ?.let { try { LocalDateTime.parse(it) } catch(_: Exception) { null } },
                                    lastLogin = snapshot.getString("lastLogin")
                                        ?.let { LocalDateTime.parse(it) }
                                        ?: return@launch,
                                    profilePictureUri = snapshot.getString("profilePictureUri")
                                        ?.let { Uri.parse(it) } ?: Uri.EMPTY
                                )

                                trySend(Result.Success(user))
                            }
                        }
                    }
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)

        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }


    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), Result.Loading)

    override suspend fun getUser(id: String): Result<User> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        try {
            val userDocument = db.collection("users").document(id).get().await()

            val user = User(
                id = userDocument.id,
                email = userDocument.getString("email") ?: return Result.Error("User not found"),
                username = userDocument.getString("username")
                    ?: return Result.Error("User not found"),
                bio = userDocument.getString("bio") ?: "",
                dateOfBirth = userDocument.getString("dateOfBirth")?.let {
                    if (it.isEmpty()) {
                        Clock.System.now().toLocalDateTime(TimeZone.UTC)
                    } else {
                        LocalDateTime.parse(it)
                    }
                } ?: return Result.Error("User not found"),
                following = userDocument.get("following") as List<String>?
                    ?: return Result.Error("User not found"),
                followers = userDocument.get("followers") as List<String>?
                    ?: return Result.Error("User not found"),
                gender = userDocument.getString("gender") ?: return Result.Error("User not found"),
                role = userDocument.getString("role") ?: return Result.Error("User not found"),
                createdAt = userDocument.getString("createdAt")
                    ?.let { LocalDateTime.parse(it) }
                    ?: return Result.Error("User not found"),
                updatedAt = userDocument.getString("updatedAt")
                    ?.let { try { LocalDateTime.parse(it) } catch(_: Exception) { null } },
                lastPasswordChange = userDocument.getString("lastPasswordChange")
                    ?.let { try { LocalDateTime.parse(it) } catch(_: Exception) { null } },
                lastLogin = userDocument.getString("lastLogin")
                    ?.let { LocalDateTime.parse(it) }
                    ?: return Result.Error("User not found"),
                profilePictureUri = userDocument.getString("profilePictureUri")
                    ?.let { Uri.parse(it) } ?: Uri.EMPTY
            )



            return Result.Success(user)
        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }


    override suspend fun signUp(
        email: String,
        password: String
    ): Result<Unit> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        return try {
            val authResult: AuthResult =
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            val currentMoment = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            db.collection("users").document(authResult.user!!.uid).set(
                hashMapOf(
                    "email" to email,
                    "createdAt" to currentMoment.toString(),
                    "updatedAt" to "",
                    "lastLogin" to currentMoment.toString(),
                    "lastPasswordChange" to "",
                    "following" to emptyList<String>(),
                    "followers" to emptyList<String>(),
                    "role" to "User",
                    "gender" to "",
                    "dateOfBirth" to "",
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
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            db.collection("users").document(authResult.user!!.uid).update(
                "lastLogin", Clock.System.now().toLocalDateTime(
                    TimeZone.UTC
                ).toString()
            )

            Result.Success(Unit)
        } catch (_: FirebaseAuthException) {
            Result.Error("Email address or password is incorrect")
        }

    }

    override suspend fun signUpWithGoogle(idToken: String): Result<SignUpStatus> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()

            val currentMoment = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            if (db.collection("users").document(authResult.user!!.uid).get().await().exists()) {
                db.collection("users").document(authResult.user!!.uid).update(
                    "lastLogin", currentMoment.toString()
                )
                return Result.Success(SignUpStatus.ExistingUser)
            }

            db.collection("users").document(authResult.user!!.uid).set(
                hashMapOf(
                    "email" to authResult.user!!.email,
                    "createdAt" to currentMoment.toString(),
                    "updatedAt" to "",
                    "lastLogin" to currentMoment.toString(),
                    "lastPasswordChange" to "",
                    "role" to "User",
                    "gender" to "",
                    "dateOfBirth" to "",
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
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        try {
            val userDocument = db.collection("users").document(firebaseAuth.currentUser!!.uid)

            val currentMoment = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            userDocument.set(
                hashMapOf(
                    "updatedAt" to currentMoment.toString(),
                    "gender" to user.gender,
                    "dateOfBirth" to user.dateOfBirth.toString(),
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
        friendID: String,
        action: FieldValue
    ): Result<Unit> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

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
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

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
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        try {
            firebaseAuth.currentUser!!.sendEmailVerification().await()
            return Result.Success(Unit)


        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun markNotificationAsRead(notificationId: String) =
        notificationService.markNotificationAsRead(notificationId)

    override suspend fun getUserPreferences(): Result<Map<String, Any>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserPreferences(preferences: Map<String, Any>): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun signOut() = firebaseAuth.signOut()
}