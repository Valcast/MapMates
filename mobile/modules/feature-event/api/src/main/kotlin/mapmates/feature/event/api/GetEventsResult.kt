package mapmates.feature.event.api

sealed interface GetEventsResult {
    data class Success(val events: List<Event>) : GetEventsResult
    data class Failure(val error: EventError) : GetEventsResult
}

