package com.example.socialmeetingapp.presentation.notifications

import com.example.socialmeetingapp.domain.model.NotificationType
import kotlinx.datetime.LocalDateTime

data class NotificationUI(
    val id: String,
    val type: NotificationType,
    val data: NotificationData,
    val isRead: Boolean,
    val timestamp: LocalDateTime,
)