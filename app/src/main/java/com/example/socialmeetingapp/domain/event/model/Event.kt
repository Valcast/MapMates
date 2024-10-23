package com.example.socialmeetingapp.domain.event.model

import com.example.socialmeetingapp.domain.user.model.User
import com.google.android.gms.maps.model.LatLng
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class Event(
    val id: String,
    val title: String,
    val description: String,
    var locationCoordinates: LatLng,
    var locationAddress: String? = null,
    val author: User,
    val participants: List<User> = emptyList(),
    val maxParticipants: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val isPrivate: Boolean,
    val isOnline: Boolean
) {
    companion object {
        val EMPTY = Event(
            id = "",
            title = "",
            description = "",
            locationCoordinates = LatLng(0.0, 0.0),
            locationAddress = null,
            author = User.EMPTY,
            participants = emptyList(),
            maxParticipants = 3,
            startTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            endTime = Clock.System.now().plus(2, DateTimeUnit.HOUR).toLocalDateTime(TimeZone.currentSystemDefault()),
            createdAt = null,
            updatedAt = null,
            isPrivate = false,
            isOnline = false
        )
    }
}


