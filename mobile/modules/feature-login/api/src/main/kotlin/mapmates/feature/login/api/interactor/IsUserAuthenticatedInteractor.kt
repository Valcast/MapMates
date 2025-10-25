package mapmates.feature.login.api.interactor

interface IsUserAuthenticatedInteractor {
    operator fun invoke(): Boolean
}