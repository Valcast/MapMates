package com.example.socialmeetingapp.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.ChatRoom
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.ChatRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
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
    private var _chatRooms = MutableStateFlow<List<ChatRoomSummary>>(emptyList())
    val chatRooms = _chatRooms.asStateFlow()

    private val eventDataCache = mutableMapOf<String, Pair<String, String>>()

    init {
        listenToChatRooms()
    }

    private fun listenToChatRooms() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()

            if (userId == null) {
                return@launch
            }

            chatRepository.listenForChatRoomsByUserId(userId).collect { chatRooms ->
                val chatRoomSummaries = chatRooms.map { chatRoom ->
                    val eventData = eventDataCache.getOrPut(chatRoom.id) {
                        chatRepository.findEventIdAndTitleByChatRoomId(chatRoom.id).let {
                            if (it is Result.Success) {
                                it.data
                            } else {
                                Log.e(
                                    "ChatRoomListViewModel",
                                    "Failed to get event data for chat room ${chatRoom.id}"
                                )
                                return@collect
                            }
                        }
                    }
                    ChatRoomSummary(chatRoom, eventData.first, eventData.second)
                }
                _chatRooms.value = chatRoomSummaries
            }
        }
    }
}

data class ChatRoomSummary(
    val chatRoom: ChatRoom,
    val eventId: String,
    val eventTitle: String
)