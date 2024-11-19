package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.UserEvents
import com.google.firebase.firestore.DocumentReference

interface EventRepository {
    suspend fun getEvents(): Result<List<Event>>
    suspend fun getEvent(id: String): Result<Event>
    suspend fun createEvent(event: Event): Result<String>
    suspend fun updateEvent(event: Event): Result<Unit>
    suspend fun deleteEvent(id: String): Result<Unit>
    suspend fun joinEvent(id: String): Result<Unit>
    suspend fun leaveEvent(id: String): Result<Unit>

    suspend fun getUserEvents(userId: String): Result<UserEvents>

    fun getCategoriesReferences(categories: List<String>): List<DocumentReference>
}