package mapmates.feature.login.api.interactor

interface GetUserAuthenticatedId {
    operator fun invoke(): Result<String>
}