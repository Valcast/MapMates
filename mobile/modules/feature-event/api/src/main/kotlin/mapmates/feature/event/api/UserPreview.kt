package mapmates.feature.event.api

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class UserPreview(
    val id: String,
    val username: String,
    val profilePictureUri: String,
    val dateOfBirth: LocalDateTime
) {
    @OptIn(ExperimentalTime::class)
    companion object {
        val EMPTY = UserPreview(
            id = "",
            username = "",
            profilePictureUri = "",
            dateOfBirth = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        )
    }
}