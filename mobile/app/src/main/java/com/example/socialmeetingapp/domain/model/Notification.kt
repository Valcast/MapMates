package com.example.socialmeetingapp.domain.model

import kotlinx.datetime.Instant

data class Notification(
    val id: String,
    val type: NotificationType,
    val timestamp: Instant,
    val isRead: Boolean,
    val params: Map<String, String>,
)

enum class NotificationType {
    EVENT_INVITATION,
    EVENT_CANCELLED,
    EVENT_CREATED,
    EVENT_UPDATED,
}
