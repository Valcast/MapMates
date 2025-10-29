package mapmates.feature.event.api

sealed interface EventError {
    object NetworkError : EventError
    object Unauthorized : EventError
    object Unknown : EventError
}