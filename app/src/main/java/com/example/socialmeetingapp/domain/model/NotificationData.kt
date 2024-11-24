package com.example.socialmeetingapp.domain.model

sealed class NotificationData {
    data class EventNotificationData(val eventId: String, val eventName: String) : NotificationData()
    data class UserNotificationData(val userId: String) : NotificationData()
}
