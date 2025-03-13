package com.example.socialmeetingapp.domain.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Event(
    val id: String,
    val title: String,
    val description: String,
    var locationCoordinates: LatLng?,
    var locationAddress: String?,
    val author: UserPreview,
    val category: Category,
    val participants: List<UserPreview> = emptyList(),
    val joinRequests: List<UserPreview> = emptyList(),
    val maxParticipants: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isPrivate: Boolean,
    val isOnline: Boolean,
    val meetingLink: String? = null,
    val chatRoomId: String? = null
) {
    companion object {
        val EMPTY = Event(
            id = "",
            title = "",
            description = "",
            locationCoordinates = null,
            locationAddress = null,
            author = UserPreview.EMPTY,
            category = Category.entries.first(),
            maxParticipants = 2,
            startTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            endTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            isPrivate = false,
            isOnline = false
        )
    }
}


