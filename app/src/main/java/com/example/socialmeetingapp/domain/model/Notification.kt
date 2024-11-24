package com.example.socialmeetingapp.domain.model

import android.net.Uri
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

sealed class Notification(
    open val id: String = "",
    open val senderId: String ,
    open val senderName: String = "",
    open val senderAvatar: Uri = Uri.EMPTY,
    open val type: NotificationType,
    open val isRead: Boolean = false,
    open val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    open val expiresAt: LocalDateTime = (Clock.System.now() + 31.days).toLocalDateTime(TimeZone.currentSystemDefault())
) {
    data class JoinEventNotification(
        override val id: String = "",
        override val senderId: String,
        override val senderName: String = "",
        override val senderAvatar: Uri = Uri.EMPTY,
        override val type: NotificationType = NotificationType.JoinEvent,
        override val isRead: Boolean = false,
        override val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        override val expiresAt: LocalDateTime = (Clock.System.now() + 31.days).toLocalDateTime(TimeZone.currentSystemDefault()),
        val eventId: String,
        val eventName: String
    ) : Notification(id, senderId, senderName, senderAvatar, type)

    data class NewFollowerNotification(
        override val id: String = "",
        override val senderId: String,
        override val senderName: String = "",
        override val senderAvatar: Uri = Uri.EMPTY,
        override val isRead: Boolean = false,
        override val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        override val expiresAt: LocalDateTime = (Clock.System.now() + 31.days).toLocalDateTime(TimeZone.currentSystemDefault()),
        override val type: NotificationType = NotificationType.NewFollower,
    ) : Notification(id, senderId, senderName, senderAvatar, type)

}