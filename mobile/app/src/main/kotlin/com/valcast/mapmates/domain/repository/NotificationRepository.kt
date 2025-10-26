package com.valcast.mapmates.domain.repository

import com.valcast.mapmates.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    val notifications: Flow<List<Notification>>

    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(id: String)
}