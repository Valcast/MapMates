package mapmates.feature.login.impl.interactor

import mapmates.feature.login.impl.CredentialManager
import mapmates.feature.login.impl.CredentialManager.CredentialType
import mapmates.feature.login.impl.CredentialManager.RequestCredential
import javax.inject.Inject

internal class RequestCredentialInteractor @Inject constructor(
    private val credentialManager: CredentialManager
) {
    suspend operator fun invoke(credentialType: CredentialType): RequestCredential = credentialManager.requestCredential(credentialType)
}