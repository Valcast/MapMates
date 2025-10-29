@file:Suppress("UNCHECKED_CAST")

package mapmates.core.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun DocumentSnapshot.getRequiredString(field: String): String =
    this.getString(field) ?: throw MissingFieldException(field)

fun DocumentSnapshot.getRequiredBoolean(field: String): Boolean =
    this.getBoolean(field) ?: throw MissingFieldException(field)

fun DocumentSnapshot.getList(field: String): List<String> =
    this.get(field) as? List<String> ?: throw MissingFieldException(field)


@OptIn(ExperimentalTime::class)
fun DocumentSnapshot.getLocalDateTime(field: String): LocalDateTime {
    val timestamp = getTimestamp(field) ?: throw MissingFieldException(field)

    return Instant.fromEpochSeconds(timestamp.seconds, timestamp.nanoseconds)
        .toLocalDateTime(
            TimeZone.currentSystemDefault()
        )
}

fun DocumentSnapshot.getInt(field: String): Int {
    val number = getLong(field) ?: throw MissingFieldException(field)

    return number.toInt()
}

fun DocumentSnapshot.getMap(field: String): Map<String, Any> {
    return this.get(field) as? Map<String, Any> ?: throw MissingFieldException(field)
}

fun DocumentSnapshot.getMapOrNull(field: String): Map<String, Any>? {
    return this.get(field) as? Map<String, Any>
}

fun GeoPoint.toPair() = Pair(latitude, longitude)

fun DocumentSnapshot.getPairCoordinates(field: String) = getGeoPoint(field)?.toPair()

fun DocumentSnapshot.getStringOrNull(field: String): String? = this.getString(field)


class MissingFieldException(field: String) : Exception("Required field '$field' is missing.")

/*
fun DocumentSnapshot.toUser(followersCount: Int, followingCount: Int): User {
    return User(
        id = id,
        email = getRequiredString("email"),
        username = getRequiredString("username"),
        bio = getRequiredString("bio"),
        dateOfBirth = getLocalDateTime("dateOfBirth"),
        followingCount = followingCount,
        followersCount = followersCount,
        gender = getRequiredString("gender"),
        createdAt = getLocalDateTime("createdAt"),
        profilePictureUri = getUri("profilePictureUri")
    )
}

fun DocumentSnapshot.toUserPreview(): UserPreview {
    return UserPreview(
        id = id,
        username = getRequiredString("username"),
        profilePictureUri = getUri("profilePictureUri"),
        dateOfBirth = getLocalDateTime("dateOfBirth")
    )
}

fun DocumentSnapshot.toEvent(
    author: UserPreview,
    participants: List<UserPreview>,
    joinRequests: List<UserPreview> = emptyList()
): Event {

    return Event(
        id = id,
        title = getRequiredString("title"),
        description = getRequiredString("description"),
        locationCoordinates = getLatLngOrNull("locationCoordinates"),
        locationAddress = getStringOrNull("locationAddress"),
        author = author,
        category = getRequiredString("category").let { Category.valueOf(it.uppercase(Locale.getDefault())) },
        participants = participants,
        joinRequests = joinRequests,
        maxParticipants = getInt("maxParticipants"),
        startTime = getLocalDateTime("startTime"),
        endTime = getLocalDateTime("endTime"),
        isPrivate = getRequiredBoolean("isPrivate"),
        isOnline = getRequiredBoolean("isOnline"),
        chatRoomId = getStringOrNull("chatRoomId"),
        meetingLink = getStringOrNull("meetingLink")
    )
}

fun DocumentSnapshot.toEventPreview(): EventPreview {
    return EventPreview(
        id = id,
        title = getRequiredString("title"),
        locationAddress = getStringOrNull("locationAddress"),
        category = getRequiredString("category").let { Category.valueOf(it.uppercase(Locale.getDefault())) },
    )
}

fun DocumentSnapshot.toNotification(): Notification {
    return Notification(
        id = id,
        type = NotificationType.valueOf(getRequiredString("type")),
        timestamp = getLocalDateTime("timestamp"),
        isRead = getRequiredBoolean("read"),
        data = getMap("data")
    )
}

fun DocumentSnapshot.toChatRoom(): ChatRoom {
    return ChatRoom(
        id = id,
        authorId = getRequiredString("authorId"),
        name = getRequiredString("name"),
        members = getList("members"),
        lastMessage = getMessage(getMapOrNull("lastMessage"))
    )
}

fun DocumentSnapshot.getMessage(messageMap: Map<String, Any>?): Message? {
    if (messageMap == null) {
        return null;
    }

    return Message(
        senderId = messageMap["senderId"].toString(),
        text = messageMap["text"].toString(),
        createdAt = (messageMap["createdAt"] as? Timestamp).let { timestamp ->
            if (timestamp == null) {
                return null
            }
            Instant.fromEpochSeconds(timestamp.seconds, timestamp.nanoseconds)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        }
    )
}


fun DocumentSnapshot.toMessage(): Message {
    return Message(
        senderId = getRequiredString("senderId"),
        text = getRequiredString("text"),
        createdAt = getLocalDateTime("createdAt")
    )
}*/
