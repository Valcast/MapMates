package com.example.socialmeetingapp.domain.model

import android.net.Uri
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

data class Notification(
    val id: String = "",
    val senderId: String,
    val senderName: String = "",
    val senderAvatar: Uri = Uri.EMPTY,
    val type: NotificationType,
    val isRead: Boolean = false,
    val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val expiresAt: LocalDateTime = (Clock.System.now() + 31.days).toLocalDateTime(TimeZone.currentSystemDefault()),
    val data: NotificationData? = null
)