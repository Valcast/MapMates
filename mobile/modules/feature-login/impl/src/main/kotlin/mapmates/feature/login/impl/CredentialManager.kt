package mapmates.feature.login.impl

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import mapmates.feature.login.impl.R as LoginR


internal class CredentialManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    private val googleIdOption =
        GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false).setServerClientId(
            context.getString(LoginR.string.default_web_client_id)
        ).build()

    suspend fun requestCredential(credentialType: CredentialType) = when (credentialType) {
        CredentialType.Google -> requestGoogleCredential()
        CredentialType.Password -> requestPasswordCredential()
    }

    private suspend fun requestPasswordCredential() = try {
        val credential = credentialManager.getCredential(
            request = GetCredentialRequest(listOf(GetPasswordOption())),
            context = context,
        ).credential as PasswordCredential

        RequestCredential.Success(credential)
    } catch (_: GetCredentialException) {
        RequestCredential.Canceled
    }


    private suspend fun requestGoogleCredential() = try {
        val credential = credentialManager.getCredential(
            request = GetCredentialRequest(listOf(googleIdOption)),
            context = context,
        ).credential as CustomCredential

        RequestCredential.Success(credential)
    } catch (e: GetCredentialException) {
        RequestCredential.Canceled
    }


    /*suspend fun saveCredential(username: String, password: String): Result<Unit> {
        return try {
            credentialManager.createCredential(
                request = CreatePasswordRequest(username, password),
                context = context
            )

            Result.success(Unit)
        } catch (e: CreateCredentialCancellationException) {
            return Result.failure("Registration cancelled")
        } catch (e: Exception) {
            return Result.failure(e.message ?: "Unknown error")
        }
    }*/

    sealed interface RequestCredential {
        data class Success(val credential: Credential) : RequestCredential
        object Canceled : RequestCredential
    }

    sealed interface CredentialType {
        object Google : CredentialType
        object Password : CredentialType
    }
}