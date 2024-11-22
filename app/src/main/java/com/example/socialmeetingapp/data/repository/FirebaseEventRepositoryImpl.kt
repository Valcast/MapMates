package com.example.socialmeetingapp.data.repository

import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.Result.Error
import com.example.socialmeetingapp.domain.model.Result.Success
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.UserEvents
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class FirebaseEventRepositoryImpl(
    private val db: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
) : EventRepository {
    override suspend fun getEvents(): Result<List<Event>> {
        return try {
            val events = db.collection("events").get().asDeferred()
                .await().documents.mapNotNull { eventDocument ->
                    mapToEvent(eventDocument)
                }

            Success(events)
        } catch (e: FirebaseFirestoreException) {
            return Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getEvent(id: String): Result<Event> {
        return try {
            val eventDocument = db.collection("events").document(id).get().asDeferred().await()

            Success(mapToEvent(eventDocument) ?: return Error("Event not found"))
        } catch (e: FirebaseFirestoreException) {
            return Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun createEvent(event: Event): Result<String> {
        return try {
            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            val eventData = hashMapOf(
                "title" to event.title,
                "description" to event.description,
                "locationCoordinates" to event.locationCoordinates.toGeoPoint(),
                "locationAddress" to event.locationAddress,
                "author" to db.collection("users").document(firebaseAuth.currentUser!!.uid),
                "participants" to emptyList<DocumentReference>(),
                "joinRequests" to emptyList<DocumentReference>(),
                "maxParticipants" to event.maxParticipants,
                "startTime" to event.startTime.toString(),
                "endTime" to event.endTime.toString(),
                "createdAt" to currentTime.toString(),
                "updatedAt" to currentTime.toString(),
                "isPrivate" to event.isPrivate,
                "isOnline" to event.isOnline,

            )

            val createdEvent = db.collection("events").add(eventData).await()

            Success(createdEvent.id)
        } catch (e: FirebaseFirestoreException) {
            return Error(e.message ?: "Unknown error")
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
        return try {
            db.collection("events").document(id).delete().await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            return Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun updateEventParticipants(
        id: String,
        updateAction: (DocumentReference) -> FieldValue
    ): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(id)

            val userRef = db.collection("users").document(firebaseAuth.currentUser!!.uid)
            eventDocument.update("participants", updateAction(userRef)).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun removeParticipant(eventID: String, userID: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(eventID)

            val userRef = db.collection("users").document(userID)
            eventDocument.update("participants", FieldValue.arrayRemove(userRef)).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun sendJoinRequest(eventID: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(eventID)

            val userRef = db.collection("users").document(firebaseAuth.currentUser!!.uid)
            eventDocument.update("joinRequests", FieldValue.arrayUnion(userRef)).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun acceptJoinRequest(eventID: String, userID: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(eventID)

            val userRef = db.collection("users").document(userID)
            eventDocument.update("participants", FieldValue.arrayUnion(userRef)).await()
            eventDocument.update("joinRequests", FieldValue.arrayRemove(userRef)).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun declineJoinRequest(eventID: String, userID: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(eventID)

            val userRef = db.collection("users").document(userID)
            eventDocument.update("joinRequests", FieldValue.arrayRemove(userRef)).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun joinEvent(id: String): Result<Unit> =
        updateEventParticipants(id, FieldValue::arrayUnion)

    override suspend fun leaveEvent(id: String): Result<Unit> =
        updateEventParticipants(id, FieldValue::arrayRemove)

    override suspend fun getUserEvents(userId: String): Result<UserEvents> {
        return try {
            val userRef = db.collection("users").document(userId)

            val authorEvents = db.collection("events").whereEqualTo("author", userRef).get()
                .await().documents.mapNotNull { eventDocument -> mapToEvent(eventDocument) }

            val participantEvents = db.collection("events").whereArrayContains("participants", userRef).get()
                .await().documents.mapNotNull { eventDocument -> mapToEvent(eventDocument) }

            Success(UserEvents(authorEvents, participantEvents))
        } catch (e: FirebaseFirestoreException) {
            return Error(e.message ?: "Unknown error")
        }
    }

    override fun getCategoriesReferences(categories: List<String>): List<DocumentReference> {
        return categories.map { category ->
            db.collection("categories").document(category)
        }
    }

    private suspend fun mapToEvent(eventDocument: DocumentSnapshot): Event? {
        val participantsDocument =
            eventDocument.get("participants") as? List<DocumentReference> ?: return null

        val participants = participantsDocument.mapNotNull { participant ->
            val userResult = userRepository.getUser(participant.path.split("/").last())
            if (userResult is Success) {
                userResult.data
            } else {
                null
            }
        }

        val joinRequestsDocument =
            eventDocument.get("joinRequests") as? List<DocumentReference> ?: return null

        val joinRequests = joinRequestsDocument.mapNotNull { joinRequest ->
            val userResult = userRepository.getUser(joinRequest.path.split("/").last())
            if (userResult is Success) {
                userResult.data
            } else {
                null
            }
        }

        val authorDocument = eventDocument.getDocumentReference("author") ?: return null
        val authorResult = userRepository.getUser(authorDocument.path.split("/").last())

        val author = if (authorResult is Success) {
            authorResult.data
        } else {
            return null
        }

        return Event(
            id = eventDocument.id,
            title = eventDocument.getString("title") ?: return null,
            description = eventDocument.getString("description") ?: return null,
            locationCoordinates = eventDocument.getGeoPoint("locationCoordinates")?.toLatLng()
                ?: return null,
            locationAddress = eventDocument.getString("locationAddress") ?: return null,
            author = author,
            maxParticipants = eventDocument.getLong("maxParticipants")?.toInt() ?: return null,
            participants = participants,
            joinRequests = joinRequests,
            startTime = eventDocument.getString("startTime")?.let { LocalDateTime.parse(it) }
                ?: return null,
            endTime = eventDocument.getString("endTime")?.let { LocalDateTime.parse(it) }
                ?: return null,
            createdAt = eventDocument.getString("createdAt")?.let { LocalDateTime.parse(it) }
                ?: return null,
            updatedAt = eventDocument.getString("updatedAt")?.let { LocalDateTime.parse(it) }
                ?: return null,
            isPrivate = eventDocument.getBoolean("isPrivate") == true,
            isOnline = eventDocument.getBoolean("isOnline") == true,
        )

    }


}