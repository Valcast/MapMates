package mapmates.feature.login.impl.interactor

import mapmates.feature.login.impl.CredentialManager
import javax.inject.Inject

internal class SavePasswordCredentialInteractor @Inject constructor(
    private val credentialManager: CredentialManager
) {
    suspend operator fun invoke(username: String, password: String) = credentialManager.savePasswordCredential(username, password)
}