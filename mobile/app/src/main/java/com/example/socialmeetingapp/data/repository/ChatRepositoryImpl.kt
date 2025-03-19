package com.example.socialmeetingapp.data.repository

import android.util.Log
import com.example.socialmeetingapp.data.source.MessagesPagingSource
import com.example.socialmeetingapp.data.utils.toChatRoom
import com.example.socialmeetingapp.data.utils.toEventPreview
import com.example.socialmeetingapp.data.utils.toMessage
import com.example.socialmeetingapp.domain.model.ChatRoom
import com.example.socialmeetingapp.domain.model.EventPreview
import com.example.socialmeetingapp.domain.model.Message
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.ChatRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl(
    private val db: FirebaseFirestore, private val userRepository: UserRepository
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
        } catch (_: Error) {
            Result.Failure("")
        }
    }

    override suspend fun listenForChatRoomsByUserId(userID: String): Flow<List<ChatRoom>> =
        callbackFlow {
            val chatRoomsCollection =
                db.collection("chatRooms").whereArrayContains("members", userID)

            val listenerRegistration = chatRoomsCollection.addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e(
                        "ChatRepositoryImpl",
                        error.message ?: "An error occurred while listening for chat rooms"
                    )
                    close(error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val chatRooms = value.documents.map {
                        it.toChatRoom()
                    }
                    trySend(chatRooms)
                }
            }

            awaitClose {
                listenerRegistration.remove()
            }
        }


    override suspend fun joinChatRoom(
        chatRoomID: String, userID: String
    ): Result<Unit> {
        return try {
            val chatRoom = db.collection("chatRooms").document(chatRoomID).get().await()

            val members = chatRoom["members"] as MutableList<String>
            members.add(userID)

            db.collection("chatRooms").document(chatRoomID).update("members", members).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred while joining chat room")
        }
    }

    override suspend fun getChatRoom(chatRoomID: String): Result<ChatRoom> {
        return try {
            val chatRoom = db.collection("chatRooms").document(chatRoomID).get().await()
            Result.Success(chatRoom.toChatRoom())
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred while getting chat room")
        }
    }

    override suspend fun findEventByChatRoomId(chatRoomId: String): Result<EventPreview> {
        return try {
            val event = db.collection("events").whereEqualTo("chatRoomId", chatRoomId).get()
                .await().documents.first()

            Result.Success(event.toEventPreview())
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred while getting event")
        }
    }

    override suspend fun sendMessage(chatRoomId: String, message: String) {
        val userId = userRepository.getCurrentUserId()
        if (userId == null) {
            return
        }

        try {
            val messageMap = hashMapOf(
                "senderId" to userId,
                "text" to message,
                "createdAt" to Timestamp.now(),
            )

            val messageResult =
                db.collection("chatRooms").document(chatRoomId).collection("messages")
                    .add(messageMap).await()

            Log.i("ChatRepositoryImpl", "Message sent with ID: ${messageResult.id}")
            db.collection("chatRooms").document(chatRoomId).update("lastMessage", messageMap)
                .await()
        } catch (e: Exception) {
            throw Exception(e.message ?: "An error occurred while sending message")
        }
    }

    override suspend fun listenForNewMessage(chatRoomID: String): Flow<Message> = callbackFlow {
        val messagesCollection =
            db.collection("chatRooms").document(chatRoomID).collection("messages")
                .whereGreaterThan("createdAt", Timestamp.now())
                .orderBy("createdAt", Query.Direction.DESCENDING).limit(1)

        Log.i(
            "ChatRepositoryImpl", "Listening for messages in chat room $chatRoomID"
        )
        val listenerRegistration = messagesCollection.addSnapshotListener { value, error ->

            if (error != null) {
                Log.e(
                    "ChatRepositoryImpl",
                    error.message ?: "An error occurred while listening for messages"
                )
                close(error)
                return@addSnapshotListener
            }

            if (value != null) {
                Log.i("ChatRepositoryImpl", "Received messages in chat room $chatRoomID")

                if (value.documents.isNotEmpty()) {
                    trySend(value.documents.first().toMessage())
                }
            }
        }

        awaitClose {
            Log.i("ChatRepositoryImpl", "Closing message listener for chat room $chatRoomID")
            listenerRegistration.remove()
        }
    }

    override fun getMessagesPagingSource(chatRoomID: String): MessagesPagingSource {
        return MessagesPagingSource(db, chatRoomID)
    }
}