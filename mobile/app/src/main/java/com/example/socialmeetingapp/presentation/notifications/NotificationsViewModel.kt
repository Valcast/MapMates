package com.example.socialmeetingapp.presentation.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Notification
import com.example.socialmeetingapp.domain.model.NotificationType
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.UserPreview
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.NotificationRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    val notifications: Flow<List<NotificationUI>> =
        notificationRepository.notifications.map { notifications ->
            val eventIds = mutableSetOf<String>()
            val authorIds = mutableSetOf<String>()

            notifications.forEach { notification ->
                val authorId = notification.data["authorId"]
                val eventId = notification.data["eventId"]

                if (authorId != null) {
                    authorIds.add(authorId)
                }

                if (eventId != null) {
                    eventIds.add(eventId)
                }
            }

            val events = eventRepository.events.value.filter {
                eventIds.contains(it.id)
            }

            val authors = userRepository.getUsersPreviews(authorIds.toList())

            if (authors is Result.Success) {
                notifications.map { notification ->
                    Log.d("NotificationsViewModel", "notifications: ${authors.data}")
                    notification.toNotificationUI(events, authors.data)
                }
            } else emptyList()

        }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
        }
    }

    private fun Notification.toNotificationUI(
        events: List<Event>,
        authors: List<UserPreview>
    ): NotificationUI {
        return NotificationUI(
            id = id,
            type = type,
            data = when (type) {
                NotificationType.EVENT_CREATED -> {
                    NotificationData.EventCreated(
                        eventId = data["eventId"].toString(),
                        authorId = data["authorId"].toString(),
                        authorName = authors.first { it.id == data["authorId"] }.username,
                        authorProfilePictureUrl = authors.first { it.id == data["authorId"] }.profilePictureUri,
                        eventTitle = events.first { it.id == data["eventId"] }.title,
                    )
                }

                else -> throw IllegalArgumentException("Unsupported notification type: $type")
            },
            isRead = isRead,
            timestamp = timestamp,
        )
    }
}



