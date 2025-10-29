package mapmates.feature.event.api

import kotlinx.datetime.LocalDateTime
import mapmates.feature.event.api.filters.Category

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val author: UserPreview,
    val category: Category,
    val locationCoordinates: Pair<Double, Double>?,
    var locationAddress: String?,
    val participants: List<UserPreview>,
    val maxParticipants: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isPrivate: Boolean,
    val isOnline: Boolean,
    val meetingLink: String? = null,
    val chatRoomId: String? = null
)


