package com.example.socialmeetingapp.domain.model

sealed class NotificationType {
    data object NewFollower : NotificationType()
    data object JoinEvent : NotificationType()

    companion object {
        fun valueOf(s: String): Any {
            return when (s) {
                "NewFollower" -> NewFollower
                "JoinEvent" -> JoinEvent
                else -> throw IllegalArgumentException("Unknown notification type")
            }
        }
    }
}