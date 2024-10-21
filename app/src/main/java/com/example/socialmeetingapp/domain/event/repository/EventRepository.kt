package com.example.socialmeetingapp.domain.event.repository

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.model.Event
import com.google.firebase.firestore.DocumentReference

interface EventRepository {
    suspend fun getEvents(): Result<List<Event>>
    suspend fun getEvent(id: String): Result<Event>
    suspend fun createEvent(event: Event): Result<Unit>
    suspend fun updateEvent(event: Event): Result<Unit>
    suspend fun deleteEvent(id: String): Result<Unit>
    suspend fun joinEvent(id: String): Result<Unit>
    suspend fun leaveEvent(id: String): Result<Unit>

    fun getCategoriesReferences(categories: List<String>): List<DocumentReference>
}