package com.example.socialmeetingapp.domain.model

import kotlinx.datetime.LocalDateTime

data class Notification(
    val id: String,
    val type: NotificationType,
    val timestamp: LocalDateTime,
    val isRead: Boolean,
    val data: Map<String, Any>,
)

enum class NotificationType {
    EVENT_CREATED,
    JOIN_REQUEST,
}

