package com.example.socialmeetingapp.presentation.chat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.socialmeetingapp.domain.model.ChatRoom
import com.example.socialmeetingapp.domain.model.Message
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.ChatRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ChatRoomViewModel.Factory::class)
class ChatRoomViewModel @AssistedInject constructor(
    @Assisted val chatRoomID: String,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(chatRoomID: String): ChatRoomViewModel
    }

    private val pagingConfig = PagingConfig(
        pageSize = 20,
        enablePlaceholders = false,
        prefetchDistance = 5
    )

    private var _messages = Pager(pagingConfig) {
        chatRepository.getMessagesPagingSource(chatRoomID)
    }.flow.cachedIn(viewModelScope)

    val messages = _messages.map { pagingData ->
        pagingData.map { message ->
            val user = userRepository.getUserPreview(message.senderId)

            if (user is Result.Success) {
                MessageWithUserData(
                    message = message,
                    isCurrentUser = message.senderId == userRepository.getCurrentUserId(),
                    senderNick = user.data.username,
                    senderProfileImageUrl = user.data.profilePictureUri
                )
            } else {
                MessageWithUserData(
                    message = message,
                    isCurrentUser = message.senderId == userRepository.getCurrentUserId(),
                    senderNick = "Unknown",
                    senderProfileImageUrl = Uri.EMPTY
                )
            }
        }
    }

    private val _newMessage = MutableSharedFlow<Message>()
    val newMessage = _newMessage.asSharedFlow().onStart {
        chatRepository.listenForNewMessage(chatRoomID).collect { message ->
            _newMessage.emit(message)
        }
    }.shareIn(viewModelScope, SharingStarted.Eagerly)

    private var _chatRoom = MutableStateFlow<ChatRoom?>(null)
    val chatRoom = _chatRoom.asStateFlow().onStart {
        val chatRoomResult = chatRepository.getChatRoom(chatRoomID)

        if (chatRoomResult is Result.Success) {
            _chatRoom.value = chatRoomResult.data
        } else {
            Log.e("ChatRoomViewModel", "Failed to get chat room")
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)


    fun sendMessage(message: String) {
        Log.i("ChatRoomViewModel", "Sending message: $message")
        viewModelScope.launch {
            chatRepository.sendMessage(
                chatRoomID = chatRoomID,
                message = message
            )
        }
    }
}

data class MessageWithUserData(
    val message: Message,
    val isCurrentUser: Boolean,
    val senderNick: String,
    val senderProfileImageUrl: Uri
)