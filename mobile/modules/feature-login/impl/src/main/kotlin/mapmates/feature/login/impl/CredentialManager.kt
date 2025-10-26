package mapmates.feature.login.impl

import android.content.Context
import android.util.Log
import androidx.credentials.CreatePasswordRequest
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
    } catch (e: GetCredentialException) {
        Log.e("CredentialManager", "Password credential request failed", e)
        RequestCredential.Canceled
    }


    private suspend fun requestGoogleCredential() = try {
        val credential = credentialManager.getCredential(
            request = GetCredentialRequest(listOf(googleIdOption)),
            context = context,
        ).credential as CustomCredential

        RequestCredential.Success(credential)
    } catch (e: GetCredentialException) {
        Log.e("CredentialManager", "Google credential request failed", e)
        RequestCredential.Canceled
    }


    suspend fun savePasswordCredential(username: String, password: String) {
        try {
            credentialManager.createCredential(
                request = CreatePasswordRequest(username, password), context = context
            )
        } catch (e: Exception) {
            Log.e("CredentialManager", "Failed to save credential", e)
        }
    }

    sealed interface RequestCredential {
        data class Success(val credential: Credential) : RequestCredential
        object Canceled : RequestCredential
    }

    sealed interface CredentialType {
        object Google : CredentialType
        object Password : CredentialType
    }
}