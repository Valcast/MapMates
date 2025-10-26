package mapmates.feature.account.impl.model

interface AccountCreationResult {

    object Success : AccountCreationResult
    data class Failure(val errorMeesageResId: Int) : AccountCreationResult
    object UserNotAuthenticated : AccountCreationResult
}