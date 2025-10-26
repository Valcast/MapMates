package com.valcast.mapmates.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcast.mapmates.domain.model.Event
import com.valcast.mapmates.domain.model.Notification
import com.valcast.mapmates.domain.model.NotificationType
import com.valcast.mapmates.domain.model.Result
import com.valcast.mapmates.domain.model.UserPreview
import com.valcast.mapmates.domain.repository.EventRepository
import com.valcast.mapmates.domain.repository.NotificationRepository
import com.valcast.mapmates.domain.repository.UserRepository
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
            val userIds = mutableSetOf<String>()

            notifications.forEach { notification ->
                notification.data["authorId"]?.let { userIds.add(it.toString()) }
                notification.data["joinRequestId"]?.let { userIds.add(it.toString()) }
                notification.data["eventId"]?.let { eventIds.add(it.toString()) }
            }

            val eventsResult = eventRepository.getEvents(eventIds.toList())

            val usersPreviews = userRepository.getUsersPreviews(userIds.toList())

            if (usersPreviews is Result.Success && eventsResult is Result.Success) {
                notifications.map { notification ->
                    notification.toNotificationUI(eventsResult.data, usersPreviews.data)
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
        usersPreviews: List<UserPreview>
    ): NotificationUI {
        return NotificationUI(
            id = id,
            type = type,
            data = when (type) {
                NotificationType.EVENT_CREATED -> {
                    NotificationData.EventCreated(
                        eventId = data["eventId"].toString(),
                        authorId = data["authorId"].toString(),
                        authorName = usersPreviews.first { it.id == data["authorId"] }.username,
                        authorProfilePictureUrl = usersPreviews.first { it.id == data["authorId"] }.profilePictureUri,
                        eventTitle = events.first { it.id == data["eventId"] }.title,
                    )
                }

                NotificationType.JOIN_REQUEST -> {
                    NotificationData.JoinRequest(
                        eventId = data["eventId"].toString(),
                        eventTitle = events.first { it.id == data["eventId"] }.title,
                        userId = data["joinRequestId"].toString(),
                        userName = usersPreviews.first { it.id == data["joinRequestId"] }.username,
                        userPictureUrl = usersPreviews.first { it.id == data["joinRequestId"] }.profilePictureUri,
                    )
                }
            },
            isRead = isRead,
            timestamp = timestamp,
        )
    }
}



