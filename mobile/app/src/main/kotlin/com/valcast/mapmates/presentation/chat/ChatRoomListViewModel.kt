package com.valcast.mapmates.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcast.mapmates.domain.model.ChatRoom
import com.valcast.mapmates.domain.model.EventPreview
import com.valcast.mapmates.domain.model.Result
import com.valcast.mapmates.domain.model.UserPreview
import com.valcast.mapmates.domain.repository.ChatRepository
import com.valcast.mapmates.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private var _roomPreviews = MutableStateFlow<List<RoomPreview>>(emptyList())
    val roomPreviews = _roomPreviews.asStateFlow()

    init {
        listenToChatRooms()
    }

    private fun listenToChatRooms() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()

            if (userId == null) {
                Log.e("ChatRoomListViewModel", "Failed to get current user id")
                return@launch
            }

            chatRepository.listenForChatRoomsByUserId(userId).collect { chatRooms ->
                val roomPreviews = chatRooms.mapNotNull { chatRoom ->
                    val event = chatRepository.findEventByChatRoomId(chatRoom.id)
                        .takeIf { it is Result.Success }
                        ?.let { (it as Result.Success).data }
                        ?: run {
                            Log.e(
                                "ChatRoomListViewModel",
                                "Failed to find event for chat room ${chatRoom.id}"
                            )
                            return@mapNotNull null
                        }

                    val lastMessageUserPreview = chatRoom.lastMessage?.let {
                        userRepository.getUserPreview(it.senderId)
                            .takeIf { it is Result.Success }
                            ?.let { (it as Result.Success).data }
                    }

                    RoomPreview(chatRoom, event, lastMessageUserPreview)
                }

                _roomPreviews.value = roomPreviews
            }
        }
    }
}

data class RoomPreview(
    val chatRoom: ChatRoom,
    val event: EventPreview,
    val lastMessageUserPreview: UserPreview? = null
)