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
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.model.onSuccess
import com.example.socialmeetingapp.domain.repository.ChatRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ChatRoomViewModel.Factory::class)
class ChatRoomViewModel @AssistedInject constructor(
    @Assisted val chatRoomId: String,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(chatRoomId: String): ChatRoomViewModel
    }

    private val pagingConfig = PagingConfig(
        pageSize = 20, enablePlaceholders = false, prefetchDistance = 5
    )

    private var _messages = Pager(pagingConfig) {
        chatRepository.getMessagesPagingSource(chatRoomId)
    }.flow.cachedIn(viewModelScope)

    val messages = _messages.map { pagingData ->
        pagingData.map { message ->
            val userResult = userRepository.getUserPreview(message.senderId)
            if (userResult is Result.Success) {
                UserMessage(
                    message = message,
                    isCurrentUser = message.senderId == userRepository.getCurrentUserId(),
                    senderName = userResult.data.username,
                    senderAvatarUri = userResult.data.profilePictureUri
                )
            } else {
                UserMessage(
                    message = message,
                    isCurrentUser = message.senderId == userRepository.getCurrentUserId(),
                    senderName = "Unknown",
                    senderAvatarUri = Uri.EMPTY
                )
            }
        }
    }


    private val _newMessages = MutableStateFlow<List<UserMessage>>(emptyList())
    val latestMessages = _newMessages.asStateFlow()

    private var _chatRoom = MutableStateFlow<ChatRoom?>(null)
    val chatRoom = _chatRoom.asStateFlow().onStart {
        chatRepository.getChatRoom(chatRoomId).onSuccess { chatRoomData ->
            _chatRoom.value = chatRoomData
        }.onFailure { _ ->
            Log.e("ChatRoomViewModel", "Failed to get chat room")
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            chatRepository.listenForNewMessage(chatRoomId).collect { message ->
                val newMessage = userRepository.getUserPreview(message.senderId).let { result ->
                    if (result is Result.Success) {
                        UserMessage(
                            message = message,
                            isCurrentUser = message.senderId == userRepository.getCurrentUserId(),
                            senderName = result.data.username,
                            senderAvatarUri = result.data.profilePictureUri
                        )
                    } else {
                        UserMessage(
                            message = message,
                            isCurrentUser = message.senderId == userRepository.getCurrentUserId(),
                            senderName = "Unknown",
                            senderAvatarUri = Uri.EMPTY
                        )
                    }
                }

                _newMessages.update { oldMessages -> listOf(newMessage) + oldMessages }
            }

        }
    }


    fun sendMessage(message: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(
                chatRoomId = chatRoomId, message = message
            )
        }
    }
}

data class UserMessage(
    val message: Message,
    val isCurrentUser: Boolean,
    val senderName: String,
    val senderAvatarUri: Uri
)