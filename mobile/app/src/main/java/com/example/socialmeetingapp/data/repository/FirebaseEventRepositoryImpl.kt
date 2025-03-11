package com.example.socialmeetingapp.data.repository

import com.example.socialmeetingapp.data.utils.getList
import com.example.socialmeetingapp.data.utils.getRequiredString
import com.example.socialmeetingapp.data.utils.toEvent
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.Result.Error
import com.example.socialmeetingapp.domain.model.Result.Success
import com.example.socialmeetingapp.domain.model.UserPreview
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
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
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime

class FirebaseEventRepositoryImpl(
    private val db: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
) : EventRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override val events: StateFlow<List<Event>> = callbackFlow {
        val listenerRegistration = db.collection("events").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                coroutineScope.launch {
                    val events = snapshot.documents.mapNotNull { eventDocument ->
                        val authorId = eventDocument.getRequiredString("author")
                        val authorResult = userRepository.getUserPreview(authorId)

                        val participantsIds = eventDocument.getList("participants")
                        val participantsResults = participantsIds.map { participantId ->
                            async {
                                userRepository.getUserPreview(participantId)
                            }
                        }.awaitAll()


                        val joinRequestsIds = eventDocument.getList("joinRequests")
                        val joinRequestsResults = joinRequestsIds.map { joinRequestId ->
                            async {
                                userRepository.getUserPreview(joinRequestId)
                            }
                        }.awaitAll()


                        if (authorResult is Success) {
                            eventDocument.toEvent(
                                author = authorResult.data,
                                participants = participantsResults.filterIsInstance<Success<UserPreview>>()
                                    .map { it.data },
                                joinRequests = joinRequestsResults.filterIsInstance<Success<UserPreview>>()
                                    .map { it.data }
                            )
                        } else {
                            null
                        }
                    }
                    send(events)
                }
            } else {
                trySend(emptyList())
            }
        }

        awaitClose { listenerRegistration.remove() }
    }.stateIn(
        coroutineScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    override suspend fun createEvent(event: Event): Result<String> {
        return try {
            val currentTime = Timestamp.now()

            val eventData = hashMapOf(
                "title" to event.title,
                "description" to event.description,
                "locationCoordinates" to event.locationCoordinates.toGeoPoint(),
                "locationAddress" to event.locationAddress,
                "author" to firebaseAuth.currentUser?.uid,
                "participants" to emptyList<String>(),
                "joinRequests" to emptyList<String>(),
                "maxParticipants" to event.maxParticipants,
                "startTime" to Timestamp(
                    event.startTime.toInstant(TimeZone.currentSystemDefault()).toJavaInstant()
                ),
                "endTime" to Timestamp(
                    event.endTime.toInstant(TimeZone.currentSystemDefault()).toJavaInstant()
                ),
                "createdAt" to currentTime,
                "isPrivate" to event.isPrivate,
                "isOnline" to event.isOnline,
                "category" to event.category
            )

            val createdEventRef = db.collection("events").add(eventData).await()

            Success(createdEventRef.id)
        } catch (e: FirebaseFirestoreException) {
            return Error(e.message ?: "Could not create event: ${e.message}")
        }
    }

    override suspend fun updateEvent(event: Event): Result<Unit> {
        return try {
            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            val eventDataToUpdate = hashMapOf(
                "title" to event.title,
                "description" to event.description,
                "participants" to emptyList<String>(),
                "joinRequests" to emptyList<String>(),
                "maxParticipants" to event.maxParticipants,
                "startTime" to event.startTime.toString(),
                "endTime" to event.endTime.toString(),
                "updatedAt" to currentTime.toString(),
                "isPrivate" to event.isPrivate,
                "isOnline" to event.isOnline,
                "category" to event.category
            )

            db.collection("events").document(event.id).update(eventDataToUpdate).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            return Error(e.message ?: "Failed to update event: ${e.message}")
        }
    }

    override suspend fun deleteEvent(id: String): Result<Unit> {
        return try {
            db.collection("events").document(id).delete().await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            return Error(e.message ?: "Failed to delete event: ${e.message}")
        }
    }

    override suspend fun removeParticipant(eventID: String, userID: String): Result<Unit> {
        return updateArrayField(eventID, "participants", userID, FieldValue.arrayRemove(userID))
    }

    override suspend fun sendJoinRequest(eventID: String): Result<Unit> {
        return updateArrayField(
            eventID, "joinRequests", firebaseAuth.currentUser?.uid, FieldValue.arrayUnion(
                firebaseAuth.currentUser?.uid ?: return Error("User not authenticated")
            )
        )
    }

    override suspend fun acceptJoinRequest(eventID: String, userID: String): Result<Unit> {
        return try {
            val eventDocumentRef = db.collection("events").document(eventID)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(eventDocumentRef)

                val currentParticipants =
                    snapshot.get("participants") as? List<String> ?: emptyList()
                val currentJoinRequests =
                    snapshot.get("joinRequests") as? List<String> ?: emptyList()

                if (!currentJoinRequests.contains(userID)) {
                    throw FirebaseFirestoreException(
                        "User is not in join requests", FirebaseFirestoreException.Code.ABORTED
                    )
                }
                val updatedParticipants = ArrayList(currentParticipants)
                updatedParticipants.add(userID)
                transaction.update(eventDocumentRef, "participants", updatedParticipants)
                transaction.update(
                    eventDocumentRef, "joinRequests", FieldValue.arrayRemove(userID)
                )
                null
            }.await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error("Failed to accept join request: ${e.message}")
        }
    }

    override suspend fun declineJoinRequest(eventID: String, userID: String): Result<Unit> {
        return updateArrayField(eventID, "joinRequests", userID, FieldValue.arrayRemove(userID))
    }

    override suspend fun joinEvent(id: String): Result<Unit> {
        val currentUserId = firebaseAuth.currentUser?.uid ?: return Error("User not authenticated")
        return try {
            val eventDocument = db.collection("events").document(id)
            eventDocument.update(
                "participants", FieldValue.arrayUnion(currentUserId)
            ).await()

            val authorId = try {
                eventDocument.get().await().getString("author")
            } catch (e: FirebaseFirestoreException) {
                null // Author might be deleted or document might not exist
            }

            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error("Failed to join event: ${e.message}")
        }
    }

    override suspend fun leaveEvent(id: String): Result<Unit> {
        val currentUserId = firebaseAuth.currentUser?.uid ?: return Error("User not authenticated")
        return try {
            val eventDocument = db.collection("events").document(id)
            eventDocument.update("participants", FieldValue.arrayRemove(currentUserId)).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error("Failed to leave event: ${e.message}")
        }
    }

    private suspend fun updateArrayField(
        eventID: String, fieldName: String, userId: String?, updateType: FieldValue
    ): Result<Unit> {
        if (userId == null) {
            return Error("User ID is null, cannot perform update")
        }
        return try {
            val eventDocument = db.collection("events").document(eventID)
            eventDocument.update(fieldName, updateType).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Error("Failed to update $fieldName: ${e.message}")
        }
    }

    private fun LatLng.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)
    private fun GeoPoint.toLatLng(): LatLng = LatLng(latitude, longitude)
}