package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result

interface EventRepository {

    suspend fun getEvents(ids: List<String>? = null): Result<List<Event>>
    suspend fun getEvent(id: String): Result<Event>

    suspend fun getEventsByAuthor(authorID: String): Result<List<Event>>
    suspend fun getEventsByParticipant(participantID: String): Result<List<Event>>

    suspend fun createEvent(event: Event): Result<String>
    suspend fun updateEvent(event: Event): Result<Unit>
    suspend fun deleteEvent(id: String): Result<Unit>
    suspend fun joinEvent(id: String): Result<Unit>
    suspend fun leaveEvent(id: String): Result<Unit>

    suspend fun removeParticipant(eventID: String, userID: String): Result<Unit>

    suspend fun inviteUsersToEvent(eventID: String, userIDs: List<String>): Result<Unit>

    suspend fun sendJoinRequest(eventID: String): Result<Unit>
    suspend fun acceptJoinRequest(eventID: String, userID: String): Result<Unit>
    suspend fun declineJoinRequest(eventID: String, userID: String): Result<Unit>

    suspend fun updateEventDescription(eventId: String, description: String): Result<Unit>
    suspend fun updateEventChatRoomId(eventId: String, chatRoomId: String): Result<Unit>
    suspend fun updateEventMeetingLink(eventId: String, meetingLink: String): Result<Unit>
}