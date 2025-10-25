package com.example.socialmeetingapp.presentation.event

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.ChatRoom
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.model.onSuccess
import com.example.socialmeetingapp.domain.repository.ChatRepository
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.usecase.CreateChatRoomUseCase
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditEventViewModel.Factory::class)
class EditEventViewModel @AssistedInject constructor(
    private val chatRepository: ChatRepository,
    private val eventRepository: EventRepository,
    private val createChatRoomUseCase: CreateChatRoomUseCase,
    @Assisted private val eventId: String
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(eventId: String): EditEventViewModel
    }

    private val _event = MutableStateFlow<Event?>(null)
    val event = _event.asStateFlow()

    private val _newDescription = MutableStateFlow<String>("")
    val newDescription = _newDescription.asStateFlow()

    private val _newMeetingLink = MutableStateFlow<String>("")
    val newMeetingLink = _newMeetingLink.asStateFlow()

    private val _chatRoom = MutableStateFlow<ChatRoom?>(null)
    val chatRoom = _chatRoom.asStateFlow()

    private val _newChatRoom = MutableStateFlow<ChatRoom>(ChatRoom.EMPTY)
    val newChatRoom = _newChatRoom.asStateFlow()

    init {
        viewModelScope.launch {
            fetchEvent().run {
                event.value?.chatRoomId?.let { fetchChatRoom(it) }
            }
        }
    }

    private suspend fun fetchEvent() {
        eventRepository.getEvent(eventId)
            .onSuccess { eventData ->
                _event.value = eventData
                _newDescription.value = eventData.description
                _newMeetingLink.value = eventData.meetingLink ?: ""
            }
            .onFailure { errorMessage ->
                Log.e("EditEventViewModel", "Failed to get event with id $eventId: $errorMessage")
                SnackbarManager.showMessage("Failed to get event")
            }
    }

    private suspend fun fetchChatRoom(chatRoomId: String) {
        chatRepository.getChatRoom(chatRoomId)
            .onSuccess { chatRoomData ->
                _chatRoom.value = chatRoomData
            }
            .onFailure { errorMessage ->
                Log.e(
                    "EditEventViewModel",
                    "Failed to get chat room with id $chatRoomId: $errorMessage"
                )
                SnackbarManager.showMessage("Failed to get chat room")
            }

    }

    fun updateEventDescription(description: String) {
        _newDescription.value = description
    }

    fun saveEventDescription() {
        viewModelScope.launch {
            eventRepository.updateEventDescription(eventId, newDescription.value)
                .onSuccess {
                    SnackbarManager.showMessage("Description updated")
                }
                .onFailure { errorMessage ->
                    Log.e(
                        "EditEventViewModel",
                        "Failed to update description for event with id $eventId: $errorMessage"
                    )
                    SnackbarManager.showMessage("Failed to update description")
                }
        }
    }

    fun updateEventMeetingLink(meetingLink: String) {
        _newMeetingLink.value = meetingLink
    }

    fun saveEventMeetingLink() {
        viewModelScope.launch {
            eventRepository.updateEventMeetingLink(eventId, newMeetingLink.value)
                .onSuccess {
                    SnackbarManager.showMessage("Meeting link updated")
                }
                .onFailure { errorMessage ->
                    Log.e(
                        "EditEventViewModel",
                        "Failed to update meeting link for event with id $eventId: $errorMessage"
                    )
                    SnackbarManager.showMessage("Failed to update meeting link")
                }
        }
    }

    fun updateNewChatRoomName(name: String) {
        _newChatRoom.update { it.copy(name = name) }
    }

    fun updateNewChatRoomAuthorOnlyWrite(authorOnlyWrite: Boolean) {
        _newChatRoom.update { it.copy(authorOnlyWrite = authorOnlyWrite) }
    }

    fun createChatRoom() {
        viewModelScope.launch {
            createChatRoomUseCase(eventId, newChatRoom.value)
                .onSuccess { chatRoomId ->
                    fetchChatRoom(chatRoomId)
                    SnackbarManager.showMessage("Chat room created")
                }
                .onFailure { errorMessage ->
                    Log.e(
                        "EditEventViewModel",
                        "Failed to create chat room for event with id $eventId: $errorMessage"
                    )
                    SnackbarManager.showMessage("Failed to create chat room")
                }
        }
    }

    fun removeParticipant(participantId: String) {
        viewModelScope.launch {
            eventRepository.removeParticipant(eventId, participantId)
                .onSuccess {
                    fetchEvent()
                    SnackbarManager.showMessage("Participant removed")
                }
                .onFailure { errorMessage ->
                    Log.e(
                        "EditEventViewModel",
                        "Failed to remove participant with id $participantId: $errorMessage"
                    )
                    SnackbarManager.showMessage("Failed to remove participant")
                }
        }
    }

    fun deleteEvent() {
        viewModelScope.launch {
            eventRepository.deleteEvent(eventId)
                .onFailure { errorMessage ->
                    Log.e(
                        "EditEventViewModel",
                        "Failed to delete event with id $eventId: $errorMessage"
                    )
                    SnackbarManager.showMessage("Failed to delete event")
                }
        }
    }


}