package com.example.socialmeetingapp.presentation.notifications

import android.net.Uri

sealed class NotificationData {
    data class EventCreated(
        val eventId: String,
        val authorId: String,
        val authorName: String,
        val authorProfilePictureUrl: Uri,
        val eventTitle: String,
    ) : NotificationData()

    data class JoinRequest(
        val eventId: String,
        val eventTitle: String,
        val userId: String,
        val userName: String,
        val userPictureUrl: Uri,
    ) : NotificationData()
}
