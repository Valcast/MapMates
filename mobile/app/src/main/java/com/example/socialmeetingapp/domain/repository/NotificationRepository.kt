package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    val notifications: Flow<List<Notification>>

    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(id: String)
}