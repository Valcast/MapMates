package com.example.socialmeetingapp.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.ChatRoom
import com.example.socialmeetingapp.domain.model.Message
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private var _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private var _chatRoom = MutableStateFlow<ChatRoom?>(null)
    val chatRoom = _chatRoom.asStateFlow()

    fun fetchMessages(chatRoomId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(chatRoomId, 15).let {
                if (it is Result.Success) {
                    _messages.value = it.data
                }
            }
        }
    }

    fun fetchChatRoom(chatRoomId: String) {
        viewModelScope.launch {
            val chatRoomsResult =
                chatRepository.getChatRoom(chatRoomId)

            if (chatRoomsResult is Result.Success) {
                _chatRoom.value = chatRoomsResult.data
            }
        }
    }
}