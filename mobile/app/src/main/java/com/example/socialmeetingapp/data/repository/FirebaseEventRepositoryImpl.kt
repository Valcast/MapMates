package com.example.socialmeetingapp.data.repository

import android.util.Log
import com.example.socialmeetingapp.data.utils.getList
import com.example.socialmeetingapp.data.utils.getRequiredString
import com.example.socialmeetingapp.data.utils.toEvent
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.Result.Failure
import com.example.socialmeetingapp.domain.model.Result.Success
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

class FirebaseEventRepositoryImpl(
    private val db: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
) : EventRepository {

    override suspend fun getEvents(ids: List<String>?): com.example.socialmeetingapp.domain.model.Result<List<Event>> {
        return try {
            val events = if (ids != null) {
                db.collection("events").whereIn("id", ids).get()
                    .await().documents.mapNotNull { document ->
                        val author =
                            userRepository.getUserPreview(document.getRequiredString("author"))
                        val participants =
                            userRepository.getUsersPreviews(document.getList("participants"))

                        if (author is Success && participants is Success) {
                            document.toEvent(author.data, participants.data)
                        } else {
                            null
                        }
                    }
            } else {
                db.collection("events").get().await().documents.mapNotNull { document ->
                    Log.i("FirebaseEventRepositoryImpl", "getEvents: document = $document")
                    val author = userRepository.getUserPreview(document.getRequiredString("author"))
                    val participants =
                        userRepository.getUsersPreviews(document.getList("participants"))

                    if (author is Success && participants is Success) {
                        document.toEvent(author.data, participants.data)
                    } else {
                        null
                    }
                }
            }

            Log.i("FirebaseEventRepositoryImpl", "getEvents: events = $events")

            Success(events)
        } catch (e: FirebaseFirestoreException) {
            Log.e("FirebaseEventRepositoryImpl", "getEvents: error = ${e.message}")
            Failure("Failed to get events: ${e.message}")
        }
    }

    override suspend fun getEventsByAuthor(authorID: String): com.example.socialmeetingapp.domain.model.Result<List<Event>> {
        return try {
            val events = db.collection("events").whereEqualTo("author", authorID).get()
                .await().documents.mapNotNull { document ->
                    val author = userRepository.getUserPreview(document.getRequiredString("author"))
                    val participants =
                        userRepository.getUsersPreviews(document.getList("participants"))

                    if (author is Success && participants is Success) {
                        document.toEvent(author.data, participants.data)
                    } else {
                        null
                    }
                }

            Success(events)
        } catch (e: FirebaseFirestoreException) {
            Failure("Failed to get events: ${e.message}")
        }
    }

    override suspend fun getEventsByParticipant(participantID: String): com.example.socialmeetingapp.domain.model.Result<List<Event>> {
        return try {
            val events =
                db.collection("events").whereArrayContains("participants", participantID).get()
                    .await().documents.mapNotNull { document ->
                        val author =
                            userRepository.getUserPreview(document.getRequiredString("author"))
                        val participants =
                            userRepository.getUsersPreviews(document.getList("participants"))

                        if (author is Success && participants is Success) {
                            document.toEvent(author.data, participants.data)
                        } else {
                            null
                        }
                    }

            Success(events)
        } catch (e: FirebaseFirestoreException) {
            Failure("Failed to get events: ${e.message}")
        }
    }

    override suspend fun getEvent(id: String): com.example.socialmeetingapp.domain.model.Result<Event> {
        return try {
            val eventDocument = db.collection("events").document(id).get().await()

            val author = userRepository.getUserPreview(eventDocument.getRequiredString("author"))
            val participants =
                userRepository.getUsersPreviews(eventDocument.getList("participants"))

            if (author is Success && participants is Success) {

                if (author.data.id == firebaseAuth.currentUser!!.uid) {
                    val joinRequests =
                        userRepository.getUsersPreviews(eventDocument.getList("joinRequests"))
                    if (joinRequests is Success) {
                        return Success(
                            eventDocument.toEvent(
                                author.data,
                                participants.data,
                                joinRequests.data
                            )
                        )
                    }
                }

                val event = eventDocument.toEvent(author.data, participants.data)
                Success(event)
            } else {
                Failure("Failed to get event data")
            }
        } catch (e: FirebaseFirestoreException) {
            Failure("Failed to get event: ${e.message}")
        }
    }


    @OptIn(ExperimentalTime::class)
    override suspend fun createEvent(event: Event): com.example.socialmeetingapp.domain.model.Result<String> {
        return try {
            val currentTime = Timestamp.now()

            val eventData = hashMapOf(
                "title" to event.title,
                "description" to event.description,
                "locationCoordinates" to event.locationCoordinates?.toGeoPoint(),
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
                "category" to event.category,
                "chatRoomId" to event.chatRoomId,
                "meetingLink" to event.meetingLink
            )

            val createdEventRef = db.collection("events").add(eventData).await()

            Success(createdEventRef.id)
        } catch (e: FirebaseFirestoreException) {
            return Failure(e.message ?: "Could not create event: ${e.message}")
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateEvent(event: Event): com.example.socialmeetingapp.domain.model.Result<Unit> {
        return try {

            val eventData = hashMapOf(
                "title" to event.title,
                "description" to event.description,
                "locationCoordinates" to event.locationCoordinates?.toGeoPoint(),
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
                "isPrivate" to event.isPrivate,
                "isOnline" to event.isOnline,
                "category" to event.category,
                "chatRoomId" to event.chatRoomId,
                "meetingLink" to event.meetingLink
            )

            db.collection("events").document(event.id).update(eventData).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            return Failure(e.message ?: "Failed to update event: ${e.message}")
        }
    }

    override suspend fun deleteEvent(id: String): com.example.socialmeetingapp.domain.model.Result<Unit> {
        return try {
            db.collection("events").document(id).delete().await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            return Failure(e.message ?: "Failed to delete event: ${e.message}")
        }
    }

    override suspend fun removeParticipant(
        eventID: String,
        userID: String
    ): com.example.socialmeetingapp.domain.model.Result<Unit> {
        return updateArrayField(eventID, "participants", userID, FieldValue.arrayRemove(userID))
    }

    override suspend fun inviteUsersToEvent(
        eventID: String,
        userIDs: List<String>
    ): com.example.socialmeetingapp.domain.model.Result<Unit> {
        return try {
            userIDs.forEach {
                val inviteData = hashMapOf(
                    "eventID" to eventID,
                    "senderID" to firebaseAuth.currentUser?.uid,
                    "createdAt" to Timestamp.now()
                )

                db.collection("users").document(it).collection("invites").add(inviteData).await()
            }

            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Failure("Failed to invite users to event: ${e.message}")
        }
    }

    override suspend fun sendJoinRequest(eventID: String): com.example.socialmeetingapp.domain.model.Result<Unit> {
        return updateArrayField(
            eventID, "joinRequests", firebaseAuth.currentUser?.uid, FieldValue.arrayUnion(
                firebaseAuth.currentUser?.uid ?: return Failure("User not authenticated")
            )
        )
    }

    override suspend fun acceptJoinRequest(
        eventID: String,
        userID: String
    ): com.example.socialmeetingapp.domain.model.Result<Unit> {
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
            Failure("Failed to accept join request: ${e.message}")
        }
    }

    override suspend fun declineJoinRequest(
        eventID: String,
        userID: String
    ): com.example.socialmeetingapp.domain.model.Result<Unit> {
        return updateArrayField(eventID, "joinRequests", userID, FieldValue.arrayRemove(userID))
    }

    override suspend fun updateEventDescription(
        eventId: String,
        description: String
    ): com.example.socialmeetingapp.domain.model.Result<Unit> {
        return try {
            db.collection("events").document(eventId).update("description", description).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Failure("Failed to update description: ${e.message}")
        }
    }

    override suspend fun updateEventChatRoomId(
        eventId: String,
        chatRoomId: String
    ): com.example.socialmeetingapp.domain.model.Result<Unit> {
        return try {
            db.collection("events").document(eventId).update("chatRoomId", chatRoomId).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Failure("Failed to update chat room ID: ${e.message}")
        }
    }

    override suspend fun updateEventMeetingLink(
        eventId: String,
        meetingLink: String
    ): Result<Unit> {
        return try {
            db.collection("events").document(eventId).update("meetingLink", meetingLink).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Failure("Failed to update meeting link: ${e.message}")
        }
    }

    override suspend fun joinEvent(id: String): com.example.socialmeetingapp.domain.model.Result<Unit> {
        val currentUserId =
            firebaseAuth.currentUser?.uid ?: return Failure("User not authenticated")
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
            Failure("Failed to join event: ${e.message}")
        }
    }

    override suspend fun leaveEvent(id: String): com.example.socialmeetingapp.domain.model.Result<Unit> {
        val currentUserId =
            firebaseAuth.currentUser?.uid ?: return Failure("User not authenticated")
        return try {
            val eventDocument = db.collection("events").document(id)
            eventDocument.update("participants", FieldValue.arrayRemove(currentUserId)).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Failure("Failed to leave event: ${e.message}")
        }
    }

    private suspend fun updateArrayField(
        eventID: String, fieldName: String, userId: String?, updateType: FieldValue
    ): Result<Unit> {
        if (userId == null) {
            return Failure("User ID is null, cannot perform update")
        }
        return try {
            val eventDocument = db.collection("events").document(eventID)
            eventDocument.update(fieldName, updateType).await()
            Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Failure("Failed to update $fieldName: ${e.message}")
        }
    }

    private fun LatLng.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)
    private fun GeoPoint.toLatLng(): LatLng = LatLng(latitude, longitude)
}