package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.data.source.MessagesPagingSource
import com.example.socialmeetingapp.domain.model.ChatRoom
import com.example.socialmeetingapp.domain.model.EventPreview
import com.example.socialmeetingapp.domain.model.Message
import com.example.socialmeetingapp.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun createChatRoom(chatRoom: ChatRoom): Result<String>
    suspend fun listenForChatRoomsByUserId(userID: String): Flow<List<ChatRoom>>
    suspend fun joinChatRoom(chatRoomID: String, userID: String): Result<Unit>
    suspend fun getChatRoom(chatRoomID: String): Result<ChatRoom>

    suspend fun findEventByChatRoomId(chatRoomID: String): Result<EventPreview>

    suspend fun sendMessage(chatRoomId: String, message: String)
    suspend fun listenForNewMessage(chatRoomID: String): Flow<Message>

    fun getMessagesPagingSource(chatRoomID: String): MessagesPagingSource
}