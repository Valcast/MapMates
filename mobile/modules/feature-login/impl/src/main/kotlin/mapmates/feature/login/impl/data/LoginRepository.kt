package mapmates.feature.login.impl.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mapmates.feature.login.impl.model.AuthenticationMethod
import mapmates.feature.login.impl.model.AuthenticationResult
import mapmates.feature.login.impl.model.ResetPasswordResult
import javax.inject.Inject
import mapmates.feature.login.impl.R as LoginR

internal class LoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDb: FirebaseFirestore,
) {
    fun isUserAuthenticated() = firebaseAuth.currentUser != null

    fun getCurrentUserId() = firebaseAuth.currentUser?.let { currentUser ->
        Result.success(currentUser.uid)
    } ?: Result.failure(Exception("No authenticated user found"))

    suspend fun authenticate(authenticationMethod: AuthenticationMethod) = withContext(Dispatchers.IO) {
        when (authenticationMethod) {
            is AuthenticationMethod.Google -> {
                Log.d("LoginRepository", "Authenticating with Google ID Token")
                val credential =
                    GoogleAuthProvider.getCredential(authenticationMethod.idToken, null)
                val result = firebaseAuth.signInWithCredential(credential).await()

                result.additionalUserInfo?.let { additionalUserInfo ->
                    Log.d("LoginRepository", "Authentication successful with Google")
                    createUserDocumentIfNotExists()
                    AuthenticationResult.Success(isNewUser = additionalUserInfo.isNewUser)
                } ?: AuthenticationResult.Failure(LoginR.string.auth_unknown_error)
            }

            is AuthenticationMethod.EmailAndPassword -> {
                if (authenticationMethod.isRegistration) {
                    firebaseAuth.createUserWithEmailAndPassword(
                        authenticationMethod.email,
                        authenticationMethod.password
                    ).await()

                    createUserDocumentIfNotExists()
                    AuthenticationResult.Success(isNewUser = true)
                } else {
                    try {
                        firebaseAuth.signInWithEmailAndPassword(
                            authenticationMethod.email,
                            authenticationMethod.password
                        ).await()

                        Log.d(
                            "LoginRepository",
                            "Authentication successful with Email and Password"
                        )
                        AuthenticationResult.Success(isNewUser = false)
                    } catch (error: FirebaseAuthException) {
                        Log.e("LoginRepository", "Authentication failed", error)
                        when (error) {
                            is FirebaseAuthInvalidUserException -> AuthenticationResult.Failure(
                                LoginR.string.login_no_account
                            )

                            is FirebaseAuthInvalidCredentialsException -> AuthenticationResult.Failure(
                                LoginR.string.login_invalid_credentials
                            )

                            else -> AuthenticationResult.Failure(
                                LoginR.string.auth_unknown_error
                            )
                        }
                    }
                }
            }
        }
    }

    suspend fun createUserDocumentIfNotExists() = withContext(Dispatchers.IO) {
        val userId = firebaseAuth.currentUser?.uid ?: return@withContext

        val userDocRef = firebaseDb.collection("users").document(userId)
        val userDocSnapshot = userDocRef.get().await()

        if (!userDocSnapshot.exists()) {
            val userData = mapOf(
                "createdAt" to System.currentTimeMillis()
            )
            userDocRef.set(userData).await()
            Log.d("LoginRepository", "User document created for userId: $userId")
        } else {
            Log.d("LoginRepository", "User document already exists for userId: $userId")
        }
    }

    suspend fun resetPassword(email: String): ResetPasswordResult {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            ResetPasswordResult.Success
        } catch (_: FirebaseAuthInvalidUserException) {
            ResetPasswordResult.UserNotFound
        } catch (_: Exception) {
            ResetPasswordResult.UnknownError
        }
    }
}
