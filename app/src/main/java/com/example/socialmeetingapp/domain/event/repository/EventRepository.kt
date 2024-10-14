package com.example.socialmeetingapp.domain.event.repository

import com.example.socialmeetingapp.domain.common.model.Resource
import com.example.socialmeetingapp.domain.event.model.Event
import com.google.firebase.firestore.DocumentReference

interface EventRepository {
    suspend fun getEvents(): Resource<List<Event>>
    suspend fun getEvent(id: String): Resource<Event>
    suspend fun createEvent(event: Event): Resource<Event>
    suspend fun updateEvent(event: Event): Resource<Event>
    suspend fun deleteEvent(id: String): Resource<Event>
    suspend fun joinEvent(id: String): Resource<Event>
    suspend fun leaveEvent(id: String): Resource<Event>

    fun getCategoriesReferences(categories: List<String>): List<DocumentReference>
}