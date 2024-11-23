package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.UserEvents
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.StateFlow

interface EventRepository {

    val eventsStateFlow: StateFlow<List<Event>>

    suspend fun getEvent(id: String): Result<Event>
    suspend fun createEvent(event: Event): Result<String>
    suspend fun updateEvent(event: Event): Result<Unit>
    suspend fun deleteEvent(id: String): Result<Unit>
    suspend fun joinEvent(id: String): Result<Unit>
    suspend fun leaveEvent(id: String): Result<Unit>

    fun getCategoriesReferences(categories: List<String>): List<DocumentReference>
    suspend fun removeParticipant(eventID: String, userID: String): Result<Unit>

    suspend fun sendJoinRequest(eventID: String): Result<Unit>
    suspend fun acceptJoinRequest(eventID: String, userID: String): Result<Unit>
    suspend fun declineJoinRequest(eventID: String, userID: String): Result<Unit>
}