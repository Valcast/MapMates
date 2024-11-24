package com.example.socialmeetingapp.data.remote

import android.net.Uri
import android.util.Log
import com.example.socialmeetingapp.domain.model.Notification
import com.example.socialmeetingapp.domain.model.NotificationData
import com.example.socialmeetingapp.domain.model.NotificationType
import com.example.socialmeetingapp.domain.model.Result
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.LocalDateTime

class NotificationService(private val db: FirebaseFirestore) {
    suspend fun sendNotification(notification: Notification, userId: String): Result<Unit> {
        try {
            if (!shouldSendNotification(notification)) {
                Log.d("NotificationService", "Notification already exists")
                return Result.Success(Unit)
            }

            val notificationDocument = hashMapOf(
                "senderId" to notification.senderId,
                "type" to notification.type.toString(),
                "isRead" to false,
                "createdAt" to notification.createdAt.toString(),
                "expiresAt" to notification.expiresAt.toString(),
                "data" to when (val data = notification.data) {
                    is NotificationData.EventNotificationData -> mapOf(
                        "eventId" to data.eventId,
                        "eventName" to data.eventName
                    )
                    is NotificationData.UserNotificationData -> mapOf(
                        "userId" to data.userId
                    )
                    else -> emptyMap<String, Any>()
                }
            )


            val notificationRef = db.collection("notifications").add(notificationDocument).await()
            db.collection("users").document(userId).update(
                "notifications", FieldValue.arrayUnion(notificationRef.id)
            ).await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("NotificationService", "Error sending notification: ${e.message}")
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getNotifications(notificationIds: List<String>): List<Notification> {
        val notifications = notificationIds.mapNotNull { notificationId ->
            try {
                val notificationDocument = db.collection("notifications").document(notificationId).get().await()
                if (notificationDocument.exists()) {
                    val senderDocument = db.collection("users").document(
                        notificationDocument.getString("senderId") ?: return@mapNotNull null
                    ).get().await()

                    val notificationType = NotificationType.valueOf(
                        notificationDocument.getString("type") ?: return@mapNotNull null
                    )

                    val data = notificationDocument.get("data") as? Map<String, Any> ?: return@mapNotNull null

                    val notificationData: NotificationData = when (notificationType) {
                        NotificationType.JoinEvent, NotificationType.RemovedFromEvent, NotificationType.FriendCreatedNewEvent -> {
                            val eventId = data["eventId"] as? String ?: return@mapNotNull null
                            NotificationData.EventNotificationData(
                                eventId = eventId,
                                eventName = db.collection("events").document(eventId).get().await()
                                    .getString("title") ?: return@mapNotNull null
                            )
                        }
                        NotificationType.NewFollower -> {
                            val userId = data["userId"] as? String ?: return@mapNotNull null
                            NotificationData.UserNotificationData(
                                userId = userId
                            )
                        }
                    }

                    Notification(
                        id = notificationDocument.id,
                        senderId = notificationDocument.getString("senderId") ?: return@mapNotNull null,
                        senderName = senderDocument.getString("username") ?: return@mapNotNull null,
                        senderAvatar = senderDocument.getString("profilePictureUri")?.let { Uri.parse(it) }
                            ?: return@mapNotNull null,
                        type = notificationType,
                        isRead = notificationDocument.getBoolean("isRead") ?: false,
                        createdAt = notificationDocument.getString("createdAt")?.let { LocalDateTime.parse(it) }
                            ?: return@mapNotNull null,
                        expiresAt = notificationDocument.getString("expiresAt")?.let { LocalDateTime.parse(it) }
                            ?: return@mapNotNull null,
                        data = notificationData
                    )
                } else null
            } catch (e: Exception) {
                Log.e("Notification", "Error fetching notification $notificationId: ${e.message}")
                null
            }
        }
        return notifications
    }



    suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
        try {
            db.collection("notifications").document(notificationId).update(
                "isRead", true
            ).await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun shouldSendNotification(notification: Notification): Boolean {
        return when (notification.type) {
            is NotificationType.JoinEvent -> {
                val eventId = (notification.data as? NotificationData.EventNotificationData)?.eventId
                if (eventId != null) {
                    val notificationExists =
                        db.collection("notifications").whereEqualTo("senderId", notification.senderId)
                            .whereEqualTo("type", notification.type.toString())
                            .whereEqualTo("data.eventId", eventId).get()
                            .await().documents.isNotEmpty()

                    !notificationExists
                } else {
                    false
                }
            }

            is NotificationType.NewFollower -> {
                val notificationExists =
                    db.collection("notifications").whereEqualTo("senderId", notification.senderId)
                        .whereEqualTo("type", notification.type.toString()).get()
                        .await().documents.isNotEmpty()

                !notificationExists
            }

            is NotificationType.FriendCreatedNewEvent -> {
                val eventId = (notification.data as? NotificationData.EventNotificationData)?.eventId
                if (eventId != null) {
                    val notificationExists =
                        db.collection("notifications").whereEqualTo("senderId", notification.senderId)
                            .whereEqualTo("type", notification.type.toString())
                            .whereEqualTo("data.eventId", eventId).get()
                            .await().documents.isNotEmpty()
                    !notificationExists
                } else false
            }

            else -> true
        }
    }


}