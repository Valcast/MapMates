package com.valcast.mapmates.domain.usecase

import android.util.Log
import com.valcast.mapmates.domain.model.ChatRoom
import com.valcast.mapmates.domain.model.Result
import com.valcast.mapmates.domain.model.flatMap
import com.valcast.mapmates.domain.model.map
import com.valcast.mapmates.domain.model.onFailure
import com.valcast.mapmates.domain.repository.ChatRepository
import com.valcast.mapmates.domain.repository.EventRepository
import javax.inject.Inject


class CreateChatRoomUseCase @Inject constructor(
    private val chatRepository: ChatRepository, private val eventRepository: EventRepository
) {
    suspend operator fun invoke(eventId: String, chatRoom: ChatRoom): Result<String> {
        return chatRepository.createChatRoom(chatRoom).flatMap { chatRoomId ->
            eventRepository.updateEventChatRoomId(eventId, chatRoomId).map {
                chatRoomId
            }
        }.onFailure {
            Log.e("CreateChatRoomUseCase", "Failed to create chat room")
            Result.Failure("Failed to create chat room")
        }

    }
}