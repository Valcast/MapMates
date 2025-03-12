package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.domain.model.ChatRoom
import com.example.socialmeetingapp.domain.model.Message
import com.example.socialmeetingapp.domain.model.Result

interface ChatRepository {

    suspend fun createChatRoom(chatRoom: ChatRoom): Result<String>
    suspend fun getChatRoom(id: String): Result<ChatRoom>
    suspend fun getChatRoomsByUser(userID: String): Result<List<ChatRoom>>
    suspend fun joinChatRoom(chatRoomID: String, userID: String): Result<Unit>

    suspend fun findEventIdAndTitleByChatRoomId(chatRoomID: String): Result<Pair<String, String>>

    suspend fun sendMessage(chatRoomID: String, message: Message)
    suspend fun getMessages(chatRoomID: String, limit: Int = 50): Result<List<Message>>
}