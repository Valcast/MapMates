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

    init {
        getChatRooms()
    }

    private fun getChatRooms() {
        viewModelScope.launch {
            val currentUser = userRepository.getCurrentUser()

            val chatRoomsResult =
                chatRepository.getChatRoomsByUser((currentUser as Result.Success).data.id)

            if (chatRoomsResult is Result.Success) {
                val eventData = chatRoomsResult.data.map {
                    val eventIdAndTitleResult =
                        chatRepository.findEventIdAndTitleByChatRoomId(it.id)

                    if (eventIdAndTitleResult is Result.Error) {
                        Log.e("ChatRoomListViewModel", eventIdAndTitleResult.message)
                        return@map ChatRoomSummary(it, "", "")
                    }

                    eventIdAndTitleResult as Result.Success

                    ChatRoomSummary(
                        it,
                        eventIdAndTitleResult.data.first,
                        eventIdAndTitleResult.data.second
                    )
                }

                _chatRooms.value = eventData
            }
        }
    }
}

data class ChatRoomSummary(
    val chatRoom: ChatRoom,
    val eventId: String,
    val eventTitle: String
)