package com.example.socialmeetingapp.data.repository

import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.asDeferred

class FirebaseEventRepositoryImpl(
    private val db: FirebaseFirestore
) : EventRepository {
    override suspend fun getEvents(): EventResult {
        return try {
            val events = db.collection("events").get().asDeferred().await().documents.map { document ->

                val authorRef = document.getDocumentReference("author")
                val authorDocument = authorRef!!.get().asDeferred().await()


                val author = User(
                    id = authorDocument.id,
                    email = authorDocument.getString("email")!!,
                    username = authorDocument.getString("username"),
                    bio = authorDocument.getString("bio"),
                    dateOfBirth = authorDocument.getDate("dateOfBirth"),
                    gender = authorDocument.getString("gender"),
                    status = authorDocument.getString("status"),
                    role = authorDocument.getString("role"),
                    isVerified = authorDocument.getBoolean("isVerified"),
                    createdAt = authorDocument.getDate("createdAt")!!,
                    lastLogin = authorDocument.getDate("lastLogin"),
                    lastPasswordChange = authorDocument.getDate("lastPasswordChange")
                )


                val event = Event(
                    title = document.getString("title")!!,
                    description = document.getString("description")!!,
                    location = document.getGeoPoint("location")?.toLatLng()!!,
                    author = author,
                    maxParticipants = document.getLong("maxParticipants")!!.toInt(),
                    time = document.getTimestamp("time")!!.toDate(),
                    date = document.getTimestamp("date")!!.toDate(),
                    createdAt = document.getTimestamp("createdAt")!!.toDate(),
                    updatedAt = document.getTimestamp("updatedAt")!!.toDate(),
                    isPrivate = document.getBoolean("isPrivate") == true,
                    isOnline = document.getBoolean("isOnline") == true,
                    duration = document.getLong("duration")?.toInt() ?: 0,
                )

                event
            }

            EventResult.SuccessMultiple(events)
        } catch (e: FirebaseFirestoreException) {
            return EventResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getEvent(id: String): EventResult {
        return try {
            val event = db.collection("events").document(id).get().asDeferred().await()

            if (!event.exists()) {
                return EventResult.Error("Event not found")
            }

            val eventObject = event.toObject(Event::class.java)

            EventResult.SuccessSingle(eventObject!!)
        } catch (e: FirebaseFirestoreException) {
            return EventResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun createEvent(event: Event): EventResult {
        return try {
            val userRef = db.collection("users").document(event.author!!.id)

            val eventData = hashMapOf(
                "title" to event.title,
                "description" to event.description,
                "location" to event.location.toGeoPoint(),
                "author" to userRef,
                "maxParticipants" to event.maxParticipants,
                "date" to Timestamp(event.date),
                "time" to Timestamp(event.time),
                "createdAt" to Timestamp(event.createdAt!!),
                "updatedAt" to Timestamp(event.updatedAt!!),
                "isPrivate" to event.isPrivate,
                "isOnline" to event.isOnline,
                "duration" to event.duration
            )

            db.collection("events").add(eventData).asDeferred().await()

            EventResult.Success
        } catch (e: FirebaseFirestoreException) {
            return EventResult.Error(e.message ?: "Unknown error")
        }
    }

    fun LatLng.toGeoPoint(): GeoPoint {
        return GeoPoint(latitude, longitude)
    }

    fun GeoPoint.toLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    override suspend fun updateEvent(event: Event): EventResult {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(id: String): EventResult {
        TODO("Not yet implemented")
    }

    override suspend fun joinEvent(id: String): EventResult {
        TODO("Not yet implemented")
    }

    override suspend fun leaveEvent(id: String): EventResult {
        TODO("Not yet implemented")
    }

    override fun getCategoriesReferences(categories: List<String>): List<DocumentReference> {
        return categories.map { category ->
            db.collection("categories").document(category)
        }
    }

}