package com.example.socialmeetingapp.presentation.event.createevent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Category
import com.example.socialmeetingapp.domain.model.ChatRoom
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.UserPreview
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.model.onSuccess
import com.example.socialmeetingapp.domain.repository.ChatRepository
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.LocationRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val locationRepository: LocationRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private var _createEventUiState = MutableStateFlow<CreateEventUiState>(CreateEventUiState())
    val createEventUiState = _createEventUiState.asStateFlow()

    fun nextStep() {

        if (createEventUiState.value.createEventFlowState == CreateEventFlow.RULES) {
            viewModelScope.launch {
                eventRepository.createEvent(createEventUiState.value.event).onSuccess { eventId ->
                    if (createEventUiState.value.chatRoom != null) {
                        chatRepository.createChatRoom(createEventUiState.value.chatRoom!!)
                            .onSuccess { chatRoomId ->
                                eventRepository.updateEvent(
                                    createEventUiState.value.event.copy(
                                        id = eventId, chatRoomId = chatRoomId
                                    )
                                ).onFailure { assignChatRoomError ->
                                    Log.e(
                                        "CreateEventViewModel",
                                        "Assign chat room id failed: $assignChatRoomError"
                                    )
                                }

                            }.onFailure { createChatError ->
                                Log.e(
                                    "CreateEventViewModel",
                                    "Create chat room failed: $createChatError"
                                )
                            }
                    } else {
                    }
                }.onFailure { createEventError ->
                    Log.e(
                        "CreateEventViewModel", "Create event failed: $createEventError"
                    )
                }
            }
        }

        _createEventUiState.update { it.copy(createEventFlowState = it.createEventFlowState.next()) }

        if (createEventUiState.value.createEventFlowState == CreateEventFlow.INVITE) {

        }

        validateNextButton()
    }

    fun previousStep() {
        if (createEventUiState.value.createEventFlowState == CreateEventFlow.CATEGORY) return

        Log.i("CreateEventViewModel", "Previous step")

        _createEventUiState.update { it.copy(createEventFlowState = it.createEventFlowState.previous()) }
        validateNextButton()
    }

    private fun validateNextButton() {
        _createEventUiState.update {
            val uiState = _createEventUiState.value

            it.copy(
                isNextButtonEnabled = when (uiState.createEventFlowState) {
                    CreateEventFlow.CATEGORY -> true
                    CreateEventFlow.INFO -> isInfoValid(uiState.event)
                    CreateEventFlow.TIME -> isTimeValid(uiState.event)
                    CreateEventFlow.LOCATION -> if (createEventUiState.value.event.isOnline) {
                        createEventUiState.value.event.meetingLink != null && isValidLink(
                            createEventUiState.value.event.meetingLink!!
                        )
                    } else {
                        true
                    }

                    CreateEventFlow.CHAT -> if (createEventUiState.value.chatRoom != null) {
                        createEventUiState.value.chatRoom!!.name.length > 5
                    } else {
                        true
                    }

                    CreateEventFlow.INVITE -> true
                    CreateEventFlow.RULES -> _createEventUiState.value.isRulesAccepted
                }
            )
        }
    }

    private fun isInfoValid(event: Event): Boolean {
        return event.title.length > 5
    }

    private fun isValidLink(link: String): Boolean {
        val googleMeetRegex = "^(http(s)://)?meet\\.google\\.com/\\S*$"
        val teamsRegex = "^(http(s)://)?teams\\.microsoft\\.com/\\S*$"
        val zoomRegex = "^(http(s)://)?(us0[2-9]web\\.zoom\\.us|zoom\\.us|\\w+\\.zoom\\.us)/j/\\S*$"
        val skypeRegex = "^(http(s)://)?join\\.skype\\.com/\\S*$"
        val discordRegex = "^(http(s)://)?discord\\.com/\\S*$"

        return link.matches(googleMeetRegex.toRegex()) || link.matches(teamsRegex.toRegex()) || link.matches(
            zoomRegex.toRegex()
        ) || link.matches(discordRegex.toRegex()) || link.matches(skypeRegex.toRegex())
    }

    private fun isTimeValid(event: Event): Boolean {
        return (event.startTime.date == event.endTime.date && event.startTime.time < event.endTime.time) || event.startTime.date < event.endTime.date
    }

    fun updateCategory(category: Category) {
        _createEventUiState.update {
            it.copy(event = it.event.copy(category = category))
        }
        validateNextButton()
    }

    fun updateTitle(title: String) {
        _createEventUiState.update {
            it.copy(event = it.event.copy(title = title))
        }
        validateNextButton()
    }

    fun updateDescription(description: String) {
        _createEventUiState.update {
            it.copy(event = it.event.copy(description = description))
        }
        validateNextButton()
    }

    fun updateIsPrivate(isPrivate: Boolean) {
        _createEventUiState.update {
            it.copy(event = it.event.copy(isPrivate = isPrivate))
        }
        validateNextButton()
    }

    fun updateIsOnline(isOnline: Boolean) {
        _createEventUiState.update {
            it.copy(event = it.event.copy(isOnline = isOnline))
        }
        validateNextButton()
    }

    fun updateMaxParticipants(maxParticipants: Int) {
        _createEventUiState.update {
            it.copy(event = it.event.copy(maxParticipants = maxParticipants))
        }
        validateNextButton()
    }

    fun updateLocation(location: LatLng) {
        viewModelScope.launch {
            locationRepository.getAddressFromLatLng(location).onSuccess { addressData ->
                _createEventUiState.update {
                    it.copy(
                        event = it.event.copy(
                            locationCoordinates = location, locationAddress = addressData
                        )
                    )
                }
            }
            validateNextButton()
        }
    }

    fun updateMeetingLink(meetingLink: String) {
        _createEventUiState.update {
            it.copy(event = it.event.copy(meetingLink = meetingLink))
        }
        validateNextButton()
    }

    fun setStartTime(startTime: LocalDateTime) {
        _createEventUiState.update {
            it.copy(event = it.event.copy(startTime = startTime))
        }
        validateNextButton()
    }

    fun setEndTime(endTime: LocalDateTime) {
        _createEventUiState.update {
            it.copy(event = it.event.copy(endTime = endTime))
        }
        validateNextButton()
    }

    fun updateChatRoomShouldCreate(boolean: Boolean) {
        viewModelScope.launch {
            userRepository.getCurrentUserPreview()
                .onSuccess { userData ->
                    _createEventUiState.update {
                        it.copy(
                            chatRoom = if (boolean) ChatRoom.EMPTY.copy(
                                members = listOf(userData.id), authorId = userData.id
                            ) else null
                        )
                    }

                    validateNextButton()
                }
        }
    }

    fun updateChatRoomName(name: String) {
        _createEventUiState.update {
            it.copy(chatRoom = it.chatRoom?.copy(name = name))
        }
        validateNextButton()
    }

    fun updateChatRoomAuthorOnlyWrite(boolean: Boolean) {
        _createEventUiState.update {
            it.copy(chatRoom = it.chatRoom?.copy(authorOnlyWrite = boolean))
        }
        validateNextButton()
    }

    fun updateRulesAccepted() {
        _createEventUiState.update {
            it.copy(isRulesAccepted = !it.isRulesAccepted)
        }
        validateNextButton()
    }
}

data class CreateEventUiState(
    val event: Event = Event.EMPTY,
    val chatRoom: ChatRoom? = null,
    val followersAndFollowing: Pair<List<UserPreview>, List<UserPreview>> = Pair(
        emptyList(), emptyList()
    ),
    val createEventFlowState: CreateEventFlow = CreateEventFlow.CATEGORY,
    val isNextButtonEnabled: Boolean = true,
    val isRulesAccepted: Boolean = false
)

enum class CreateEventFlow {
    CATEGORY, INFO, TIME, LOCATION, CHAT, INVITE, RULES;

    fun next(): CreateEventFlow {
        return when (this) {
            CATEGORY -> INFO
            INFO -> TIME
            TIME -> LOCATION
            LOCATION -> CHAT
            CHAT -> INVITE
            INVITE -> RULES
            RULES -> RULES
        }
    }

    fun previous(): CreateEventFlow {
        return when (this) {
            CATEGORY -> CATEGORY
            INFO -> CATEGORY
            TIME -> INFO
            LOCATION -> TIME
            CHAT -> LOCATION
            INVITE -> CHAT
            RULES -> INVITE
        }
    }
}