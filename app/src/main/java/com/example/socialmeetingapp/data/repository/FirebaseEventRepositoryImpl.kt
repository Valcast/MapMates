package com.example.socialmeetingapp.data.repository

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class FirebaseEventRepositoryImpl(
    private val db: FirebaseFirestore,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : EventRepository {
    override suspend fun getEvents(): Result<List<Event>> {
        return try {
            val events = db.collection("events").get().asDeferred().await().documents.map { eventDocument ->
                val event = Event(
                    id = eventDocument.id,
                    title = eventDocument.getString("title") ?: return Result.Error("Title is missing"),
                    description = eventDocument.getString("description") ?: return Result.Error("Description is missing"),
                    locationCoordinates = eventDocument.getGeoPoint("locationCoordinates")?.toLatLng() ?: return Result.Error("Location is missing"),
                    locationAddress = eventDocument.getString("locationAddress") ?: return Result.Error("Location address is missing"),
                    author = eventDocument.getDocumentReference("author")?.path ?: return Result.Error("Author is missing"),
                    maxParticipants = eventDocument.getLong("maxParticipants")?.toInt() ?: return Result.Error("Max participants is missing"),
                    startTime = eventDocument.getString("startTime")?.let { LocalDateTime.parse(it) } ?: return Result.Error("Date is missing"),
                    endTime = eventDocument.getString("endTime")?.let { LocalDateTime.parse(it) } ?: return Result.Error("Date is missing"),
                    createdAt = eventDocument.getString("createdAt")?.let { LocalDateTime.parse(it) } ?: return Result.Error("Created at is missing"),
                    updatedAt = eventDocument.getString("updatedAt")?.let { LocalDateTime.parse(it) } ?: return Result.Error("Updated at is missing"),
                    isPrivate = eventDocument.getBoolean("isPrivate") == true,
                    isOnline = eventDocument.getBoolean("isOnline") == true,
                )

                event
            }

            Result.Success(events)
        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getEvent(id: String): Result<Event> {

        return try {
            val eventDocument = db.collection("events").document(id).get().asDeferred().await()

            if (!eventDocument.exists()) {
                return Result.Error("Event not found")
            }

            val event = Event(
                id = eventDocument.id,
                title = eventDocument.getString("title") ?: return Result.Error("Title is missing"),
                description = eventDocument.getString("description") ?: return Result.Error("Description is missing"),
                locationCoordinates = eventDocument.getGeoPoint("locationCoordinates")?.toLatLng() ?: return Result.Error("Location is missing"),
                locationAddress = eventDocument.getString("locationAddress") ?: return Result.Error("Location address is missing"),
                author = eventDocument.getDocumentReference("author")?.path ?: return Result.Error("Author is missing"),
                maxParticipants = eventDocument.getLong("maxParticipants")?.toInt() ?: return Result.Error("Max participants is missing"),
                startTime = eventDocument.getString("startTime")?.let { LocalDateTime.parse(it) } ?: return Result.Error("Date is missing"),
                endTime = eventDocument.getString("endTime")?.let { LocalDateTime.parse(it) } ?: return Result.Error("Date is missing"),
                createdAt = eventDocument.getString("createdAt")?.let { LocalDateTime.parse(it) } ?: return Result.Error("Created at is missing"),
                updatedAt = eventDocument.getString("updatedAt")?.let { LocalDateTime.parse(it) } ?: return Result.Error("Updated at is missing"),
                isPrivate = eventDocument.getBoolean("isPrivate") == true,
                isOnline = eventDocument.getBoolean("isOnline") == true,
            )


            Result.Success(event)
        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun createEvent(event: Event): Result<Unit> {
        return try {
            val currentUser = getCurrentUserUseCase()

            if (currentUser !is Result.Success) {
                return Result.Error("User not found")
            }

            val userRef = db.collection("users").document(currentUser.data!!.id)
            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            val eventData = hashMapOf(
                "title" to event.title,
                "description" to event.description,
                "locationCoordinates" to event.locationCoordinates.toGeoPoint(),
                "locationAddress" to event.locationAddress,
                "author" to userRef,
                "participants" to emptyList<DocumentReference>(),
                "maxParticipants" to event.maxParticipants,
                "startTime" to event.startTime.toString(),
                "endTime" to event.endTime.toString(),
                "createdAt" to currentTime.toString(),
                "updatedAt" to currentTime.toString(),
                "isPrivate" to event.isPrivate,
                "isOnline" to event.isOnline,
            )

            db.collection("events").add(eventData).asDeferred().await()

            Result.Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    private fun LatLng.toGeoPoint(): GeoPoint {
        return GeoPoint(latitude, longitude)
    }

    private fun GeoPoint.toLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    override suspend fun updateEvent(event: Event): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(id: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun joinEvent(id: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(id)

            when (val currentUser = getCurrentUserUseCase()) {
                is Result.Error -> return Result.Error(currentUser.message)
                is Result.Success -> {
                    val userRef = db.collection("users").document(currentUser.data.id)

                    val eventAuthor = eventDocument.get().asDeferred().await().getDocumentReference("author")

                    if (eventAuthor == userRef) {
                        return Result.Error("You cannot join your own event")
                    }

                    eventDocument.update("participants", FieldValue.arrayUnion(userRef))

                    Result.Success(Unit)
                }

                else -> return Result.Error("Unknown error")
            }

        } catch (e: FirebaseFirestoreException) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun leaveEvent(id: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(id)

            when (val currentUser = getCurrentUserUseCase()) {
                is Result.Error -> return Result.Error(currentUser.message)
                is Result.Success -> {
                    val userRef = db.collection("users").document(currentUser.data.id)
                    eventDocument.update("participants", FieldValue.arrayRemove(userRef))

                    Result.Success(Unit)
                }
                else -> return Result.Error("Unknown error")
            }

        } catch (e: FirebaseFirestoreException) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override fun getCategoriesReferences(categories: List<String>): List<DocumentReference> {
        return categories.map { category ->
            db.collection("categories").document(category)
        }
    }

}