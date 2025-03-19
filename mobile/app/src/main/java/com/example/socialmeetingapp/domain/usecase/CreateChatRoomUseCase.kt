package com.example.socialmeetingapp.domain.usecase

import android.util.Log
import com.example.socialmeetingapp.domain.model.ChatRoom
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.flatMap
import com.example.socialmeetingapp.domain.model.map
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.repository.ChatRepository
import com.example.socialmeetingapp.domain.repository.EventRepository
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