package mapmates.feature.login.impl.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import mapmates.feature.login.impl.model.AuthenticationMethod
import mapmates.feature.login.impl.model.AuthenticationResult
import javax.inject.Inject

internal class LoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun isUserAuthenticated(): Boolean = firebaseAuth.currentUser != null

    suspend fun authenticate(authenticationMethod: AuthenticationMethod) = when (authenticationMethod) {
        is AuthenticationMethod.Google -> {
            val credential = GoogleAuthProvider.getCredential(authenticationMethod.idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()

            result.additionalUserInfo?.let { additionalUserInfo ->
                if (additionalUserInfo.isNewUser) {
                    AuthenticationResult.NewUser
                } else {
                    AuthenticationResult.ExistingUser
                }
            }
        }

        is AuthenticationMethod.EmailAndPassword -> {
            if (authenticationMethod.isRegistration) {
                firebaseAuth.createUserWithEmailAndPassword(
                    authenticationMethod.email,
                    authenticationMethod.password
                ).await()

                AuthenticationResult.NewUser
            } else {
                try {
                    firebaseAuth.signInWithEmailAndPassword(
                        authenticationMethod.email,
                        authenticationMethod.password
                    ).await()

                    AuthenticationResult.ExistingUser
                } catch (error: FirebaseAuthException) {
                    when (error) {
                        is FirebaseAuthInvalidUserException -> AuthenticationResult.UserNotFound
                        else -> AuthenticationResult.UnknownError
                    }
                }
            }
        }
    }
}
