package com.example.socialmeetingapp.data.remote

import android.net.Uri
import android.util.Log
import com.example.socialmeetingapp.domain.model.Notification
import com.example.socialmeetingapp.domain.model.NotificationType
import com.example.socialmeetingapp.domain.model.Result
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.LocalDateTime

class NotificationService(private val db: FirebaseFirestore) {
    suspend fun sendNotification(notification: Notification, userId: String): Result<Unit> {
        try {
            val notificationExists = db.collection("notifications")
                .whereEqualTo("senderId", notification.senderId)
                .whereEqualTo("type", notification.type.toString())
                .get().await().documents.isNotEmpty()

            if (notificationExists) {
                return Result.Success(Unit)
            }

            val notificationDocument = hashMapOf(
                    "senderId" to notification.senderId,
                    "type" to notification.type.toString(),
                    "isRead" to false,
                    "createdAt" to notification.createdAt.toString(),
                    "expiresAt" to notification.expiresAt.toString(),
                    "data" to hashMapOf<String, Any>()
                )


            when (notification) {
                is Notification.JoinEventNotification -> {
                    notificationDocument["data"] = hashMapOf(
                        "eventId" to notification.eventId
                    )
                }

                else -> {}
            }

            val notificationRef = db.collection("notifications").add(notificationDocument).await()

            db.collection("users").document(userId).update(
                "notifications", FieldValue.arrayUnion(notificationRef.id)
            ).await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getNotifications(notificationIds: List<String>): List<Notification> {

        val notifications = notificationIds.mapNotNull { notificationId ->
            try {
                val notificationDocument =
                    db.collection("notifications").document(notificationId).get().await()

                if (notificationDocument.exists()) {
                    val senderDocument = db.collection("users").document(
                        notificationDocument.getString("senderId") ?: return@mapNotNull null
                    ).get().await()
                    val notificationType = NotificationType.valueOf(
                        notificationDocument.getString("type") ?: return@mapNotNull null
                    )

                    val data = notificationDocument.get("data") as? Map<String, Any> ?: emptyMap()

                    when (notificationType) {
                        is NotificationType.JoinEvent -> {
                            val eventDocument = db.collection("events").document(
                                data["eventId"] as? String ?: return@mapNotNull null
                            ).get().await()


                            Notification.JoinEventNotification(
                                id = notificationDocument.id,
                                senderId = notificationDocument.getString("senderId")
                                    ?: return@mapNotNull null,
                                senderName = senderDocument.getString("username")
                                    ?: return@mapNotNull null,
                                senderAvatar = senderDocument.getString("profilePictureUri")
                                    ?.let { Uri.parse(it) } ?: return@mapNotNull null,
                                type = notificationType,
                                isRead = notificationDocument.getBoolean("isRead")
                                    ?: return@mapNotNull null,
                                createdAt = notificationDocument.getString("createdAt")
                                    ?.let { LocalDateTime.parse(it) } ?: return@mapNotNull null,
                                expiresAt = notificationDocument.getString("expiresAt")
                                    ?.let { LocalDateTime.parse(it) } ?: return@mapNotNull null,
                                eventId = eventDocument.id,
                                eventName = eventDocument.getString("title")
                                    ?: return@mapNotNull null
                            )
                        }

                        is NotificationType.NewFollower -> {
                            Notification.NewFollowerNotification(
                                id = notificationDocument.id,
                                senderId = notificationDocument.getString("senderId")
                                    ?: return@mapNotNull null,
                                senderName = senderDocument.getString("username")
                                    ?: return@mapNotNull null,
                                senderAvatar = senderDocument.getString("profilePictureUri")
                                    ?.let { Uri.parse(it) } ?: return@mapNotNull null,
                                type = notificationType,
                                isRead = notificationDocument.getBoolean("isRead")
                                    ?: return@mapNotNull null,
                                createdAt = notificationDocument.getString("createdAt")
                                    ?.let { LocalDateTime.parse(it) } ?: return@mapNotNull null,
                                expiresAt = notificationDocument.getString("expiresAt")
                                    ?.let { LocalDateTime.parse(it) } ?: return@mapNotNull null
                            )
                        }

                        else -> null
                    }

                } else null

            } catch (e: Exception) {
                Log.e("Notification", "Error fetching notification $notificationId: ${e.message}")
                null
            }
        }

        return notifications
    }
}