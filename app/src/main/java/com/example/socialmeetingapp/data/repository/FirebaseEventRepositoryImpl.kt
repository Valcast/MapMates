package com.example.socialmeetingapp.data.repository

import android.util.Log
import com.example.socialmeetingapp.data.remote.NotificationService
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Notification
import com.example.socialmeetingapp.domain.model.NotificationData
import com.example.socialmeetingapp.domain.model.NotificationType
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.Result.Error
import com.example.socialmeetingapp.domain.model.Result.Success
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    private val notificationService: NotificationService
) : EventRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override val eventsStateFlow: StateFlow<List<Event>> = callbackFlow {
        val listenerRegistration = db.collection("events").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
            } else if (snapshot != null) {
                coroutineScope.launch {
                    val events = snapshot.documents.mapNotNull { eventDocument ->
                        async { mapToEvent(eventDocument) }
                    }

                    send(events.awaitAll().filterNotNull())
                }
            }
        }

        awaitClose { listenerRegistration.remove() }
    }.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )


    override suspend fun getEvent(id: String): Result<Event> {
        return try {
            val eventDocument = db.collection("events").document(id).get().asDeferred().await()
            val event = mapToEvent(eventDocument)

            Success(event ?: return Error("Event not found"))
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
                "author" to firebaseAuth.currentUser!!.uid,
                "participants" to emptyList<String>(),
                "joinRequests" to emptyList<String>(),
                "maxParticipants" to event.maxParticipants,
                "startTime" to event.startTime.toString(),
                "endTime" to event.endTime.toString(),
                "createdAt" to currentTime.toString(),
                "updatedAt" to currentTime.toString(),
                "isPrivate" to event.isPrivate,
                "isOnline" to event.isOnline,

                )

            val createdEvent = db.collection("events").add(eventData).await()

            (userRepository.currentUser.value as Success).data!!.followers.forEach { follower ->
                notificationService.sendNotification(
                    userId = follower,
                    notification = Notification(
                        senderId = firebaseAuth.currentUser!!.uid,
                        type = NotificationType.FriendCreatedNewEvent,
                        data = NotificationData.EventNotificationData(
                            eventId = createdEvent.id,
                            eventName = event.title
                        )
                    )

                )
            }

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

    override suspend fun removeParticipant(eventID: String, userID: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(eventID)

            eventDocument.update("participants", FieldValue.arrayRemove(userID)).await()

            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun sendJoinRequest(eventID: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(eventID)

            eventDocument.update("joinRequests", FieldValue.arrayUnion(firebaseAuth.currentUser!!.uid)).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun acceptJoinRequest(eventID: String, userID: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(eventID)

            eventDocument.update("participants", FieldValue.arrayUnion(userID)).await()
            eventDocument.update("joinRequests", FieldValue.arrayRemove(userID)).await()

            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun declineJoinRequest(eventID: String, userID: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(eventID)

            eventDocument.update("joinRequests", FieldValue.arrayRemove(userID)).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun joinEvent(id: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(id)
            eventDocument.update("participants", FieldValue.arrayUnion(firebaseAuth.currentUser!!.uid)).await()

            notificationService.sendNotification(
                userId = eventDocument.get().await().getString("author")!!,
                notification = Notification(
                    senderId = firebaseAuth.currentUser!!.uid,
                    type = NotificationType.JoinEvent,
                    data = NotificationData.EventNotificationData(
                        eventId = id,
                        eventName = db.collection("events").document(id).get().await()
                            .getString("title")!!
                    )
                )
            )

            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error(e.message ?: "Unknown error")
        }

    }

    override suspend fun leaveEvent(id: String): Result<Unit> {
        return try {
            val eventDocument = db.collection("events").document(id)
            eventDocument.update("participants", FieldValue.arrayRemove(firebaseAuth.currentUser!!.uid)).await()

            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error(e.message ?: "Unknown error")
        }
    }

    override fun getCategoriesReferences(categories: List<String>): List<DocumentReference> {
        return categories.map { category ->
            db.collection("categories").document(category)
        }
    }

    private suspend fun mapToEvent(eventDocument: DocumentSnapshot): Event? {
        val participantsDocument = eventDocument.get("participants") as? List<String> ?: return null
        val participants = participantsDocument.map { participant ->
            coroutineScope.async {
                val userResult = userRepository.getUser(participant)
                if (userResult is Success) {
                    userResult.data
                } else {
                    null
                }
            }
        }

        val joinRequestsDocument = eventDocument.get("joinRequests") as? List<String> ?: return null
        val joinRequests = joinRequestsDocument.map { joinRequest ->
            coroutineScope.async {
                val userResult = userRepository.getUser(joinRequest)
                if (userResult is Success) {
                    userResult.data
                } else {
                    null
                }
            }
        }

        val authorDocument = eventDocument.getString("author") ?: return null
        val author = coroutineScope.async {
            val userResult = userRepository.getUser(authorDocument)
            if (userResult is Success) {
                userResult.data
            } else {
                null
            }
        }

        return Event(
            id = eventDocument.id,
            title = eventDocument.getString("title") ?: return null,
            description = eventDocument.getString("description") ?: return null,
            locationCoordinates = eventDocument.getGeoPoint("locationCoordinates")?.toLatLng()
                ?: return null,
            locationAddress = eventDocument.getString("locationAddress") ?: return null,
            author = author.await() ?: return null,
            maxParticipants = eventDocument.getLong("maxParticipants")?.toInt() ?: return null,
            participants = participants.awaitAll().filterNotNull(),
            joinRequests = joinRequests.awaitAll().filterNotNull(),
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