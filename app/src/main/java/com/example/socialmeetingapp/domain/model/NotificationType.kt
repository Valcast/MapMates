package com.example.socialmeetingapp.domain.model

sealed class NotificationType{

    data object JoinEvent : NotificationType()
    data object FriendCreatedNewEvent : NotificationType()
    data object NewFollower : NotificationType()
    data object RemovedFromEvent : NotificationType()



    companion object {

        fun valueOf(s: String): NotificationType {
            return when (s) {
                "NewFollower" -> NewFollower
                "JoinEvent" -> JoinEvent
                "RemovedFromEvent" -> RemovedFromEvent
                "FriendCreatedNewEvent" -> FriendCreatedNewEvent
                else -> throw IllegalArgumentException("Unknown notification type")
            }
        }
    }
}