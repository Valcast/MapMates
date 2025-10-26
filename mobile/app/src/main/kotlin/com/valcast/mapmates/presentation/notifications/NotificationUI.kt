package com.valcast.mapmates.presentation.notifications

import com.valcast.mapmates.domain.model.NotificationType
import kotlinx.datetime.LocalDateTime

data class NotificationUI(
    val id: String,
    val type: NotificationType,
    val data: NotificationData,
    val isRead: Boolean,
    val timestamp: LocalDateTime,
)