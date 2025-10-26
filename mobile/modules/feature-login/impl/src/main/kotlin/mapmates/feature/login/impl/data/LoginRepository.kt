package mapmates.feature.login.impl.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import mapmates.feature.login.impl.model.AuthenticationMethod
import mapmates.feature.login.impl.model.AuthenticationResult
import mapmates.feature.login.impl.model.ResetPasswordResult
import javax.inject.Inject
import mapmates.feature.login.impl.R as LoginR

internal class LoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun isUserAuthenticated(): Boolean = firebaseAuth.currentUser != null

    suspend fun authenticate(authenticationMethod: AuthenticationMethod) =
        when (authenticationMethod) {
            is AuthenticationMethod.Google -> {
                Log.d("LoginRepository", "Authenticating with Google ID Token")
                val credential =
                    GoogleAuthProvider.getCredential(authenticationMethod.idToken, null)
                val result = firebaseAuth.signInWithCredential(credential).await()

                result.additionalUserInfo?.let { additionalUserInfo ->
                    Log.d("LoginRepository", "Authentication successful with Google")
                    AuthenticationResult.Success(isNewUser = additionalUserInfo.isNewUser)
                } ?: AuthenticationResult.Failure(LoginR.string.auth_unknown_error)
            }

            is AuthenticationMethod.EmailAndPassword -> {
                if (authenticationMethod.isRegistration) {
                    firebaseAuth.createUserWithEmailAndPassword(
                        authenticationMethod.email,
                        authenticationMethod.password
                    ).await()

                    AuthenticationResult.Success(isNewUser = true)
                } else {
                    try {
                        firebaseAuth.signInWithEmailAndPassword(
                            authenticationMethod.email,
                            authenticationMethod.password
                        ).await()

                        Log.d("LoginRepository", "Authentication successful with Email and Password")
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
