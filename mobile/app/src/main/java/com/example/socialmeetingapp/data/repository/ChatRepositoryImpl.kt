package com.example.socialmeetingapp.data.repository

import android.util.Log
import com.example.socialmeetingapp.data.utils.getRequiredString
import com.example.socialmeetingapp.data.utils.toChatRoom
import com.example.socialmeetingapp.data.utils.toMessage
import com.example.socialmeetingapp.domain.model.ChatRoom
import com.example.socialmeetingapp.domain.model.Message
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.ChatRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class ChatRepositoryImpl(
    private val db: FirebaseFirestore,
) : ChatRepository {
    override suspend fun createChatRoom(chatRoom: ChatRoom): Result<String> {
        return try {
            val chat = hashMapOf(
                "name" to chatRoom.name,
                "members" to chatRoom.members,
                "lastMessage" to chatRoom.lastMessage,
                "authorOnlyWrite" to chatRoom.authorOnlyWrite,
                "authorId" to chatRoom.authorId
            )

            val createChatRef = db.collection("chatRooms").add(chat).await()
            Result.Success(createChatRef.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred while creating chat room")
        }
    }

    override suspend fun getChatRoom(id: String): Result<ChatRoom> {
        return try {
            val chatRoom = db.collection("chatRooms").document(id).get().await()

            val lastMessage = getMessages(chatRoom.id, 1)

            if (lastMessage is Result.Success && lastMessage.data.isNotEmpty()) {
                Result.Success(chatRoom.toChatRoom(lastMessage.data.first()))
            }

            Result.Success(chatRoom.toChatRoom())
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred while getting chat room")
        }
    }

    override suspend fun getChatRoomsByUser(userID: String): Result<List<ChatRoom>> {
        return try {
            val chatRooms =
                db.collection("chatRooms").whereArrayContains("members", userID).get().await()

            Log.i("ChatRepositoryImpl", "chatRooms: $chatRooms")

            val chatRoomList = chatRooms.map {
                val lastMessage = getMessages(it.id, 1)

                Log.i(
                    "ChatRepositoryImpl",
                    "lastMessage: $lastMessage"
                )

                if (lastMessage is Result.Success && lastMessage.data.isNotEmpty()) {
                    it.toChatRoom(lastMessage.data.first())
                }

                it.toChatRoom()
            }

            Log.i("ChatRepositoryImpl", "chatRoomList: $chatRoomList")

            Result.Success(chatRoomList)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred while getting chat rooms")
        }
    }

    override suspend fun joinChatRoom(
        chatRoomID: String,
        userID: String
    ): Result<Unit> {
        return try {
            val chatRoom = db.collection("chatRooms").document(chatRoomID).get().await()

            val members = chatRoom["members"] as MutableList<String>
            members.add(userID)

            db.collection("chatRooms").document(chatRoomID).update("members", members).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred while joining chat room")
        }
    }

    override suspend fun findEventIdAndTitleByChatRoomId(chatRoomId: String): Result<Pair<String, String>> {
        return try {
            val event = db.collection("events").whereEqualTo("chatRoomId", chatRoomId).get().await()
                .documents.first()

            Result.Success(Pair(event.id, event.getRequiredString("title")))
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred while getting event")
        }
    }

    override suspend fun sendMessage(chatRoomID: String, message: Message) {
        try {

            val messageMap = hashMapOf(
                "senderID" to message.senderId,
                "text" to message.text,
                "timestamp" to Timestamp(
                    message.timestamp.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
                    message.timestamp.toInstant(TimeZone.currentSystemDefault()).nanosecondsOfSecond
                ),
            )

            db.collection("chatRooms").document(chatRoomID).collection("messages").add(messageMap)
                .await()
        } catch (e: Exception) {
            throw Exception(e.message ?: "An error occurred while sending message")
        }
    }

    override suspend fun getMessages(chatRoomID: String, limit: Int): Result<List<Message>> {
        return try {
            val messages = db.collection("chatRooms").document(chatRoomID).collection("messages")
                .limit(limit.toLong()).get().await()

            if (messages.isEmpty) {
                return Result.Success(emptyList())
            }

            val messageList = messages.map {
                it.toMessage()
            }

            Result.Success(messageList)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred while getting messages")
        }
    }
}