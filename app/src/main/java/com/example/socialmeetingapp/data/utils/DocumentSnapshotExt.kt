package com.example.socialmeetingapp.data.utils

import android.net.Uri
import androidx.core.net.toUri
import com.example.socialmeetingapp.domain.model.Category
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.model.UserPreview
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun DocumentSnapshot.getRequiredString(field: String): String = this.getString(field) ?: throw MissingFieldException(field)
fun DocumentSnapshot.getRequiredBoolean(field: String): Boolean = this.getBoolean(field) ?: throw MissingFieldException(field)
fun DocumentSnapshot.getList(field: String): List<String> = this.get(field) as? List<String> ?: throw MissingFieldException(field)


fun DocumentSnapshot.getLocalDateTime(field: String): LocalDateTime {
    val timestamp = getTimestamp(field) ?: throw MissingFieldException(field)

    return Instant.fromEpochSeconds(timestamp.seconds, timestamp.nanoseconds).toLocalDateTime(
        TimeZone.currentSystemDefault()
    )
}

fun DocumentSnapshot.getUri(field: String): Uri {
    val uri = getString(field) ?: throw MissingFieldException(field)

    return uri.toUri()
}

fun DocumentSnapshot.getInt(field: String): Int {
    val number = getLong(field) ?: throw MissingFieldException(field)

    return number.toInt()
}

fun LatLng.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)
fun GeoPoint.toLatLng(): LatLng = LatLng(latitude, longitude)

fun DocumentSnapshot.getLatLng(field: String): LatLng = getGeoPoint(field)?.toLatLng() ?: throw MissingFieldException(field)

class MissingFieldException(field: String) : Exception("Required field '$field' is missing.")

/**
 *  Converts a [DocumentSnapshot] object from Firestore into a [User] object
 *  @return A [User] object
 *  @throws [MissingFieldException] if one of the fields are missing in the [DocumentSnapshot]
 */
fun DocumentSnapshot.toUser(): User {
    return User(
        id = id,
        email = getRequiredString("email"),
        username = getRequiredString("username"),
        bio = getRequiredString("bio"),
        dateOfBirth = getLocalDateTime("dateOfBirth"),
        following = getList("following"),
        followers = getList("followers"),
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
    category: Category,
    participants: List<UserPreview>,
    joinRequests: List<UserPreview>
): Event? {

    return Event(
        id = id,
        title = getRequiredString("title"),
        description = getRequiredString("description"),
        locationCoordinates = getLatLng("locationCoordinates"),
        locationAddress = getRequiredString("locationAddress"),
        author = author,
        category = category,
        participants = participants,
        joinRequests = joinRequests,
        maxParticipants = getInt("maxParticipants"),
        startTime = getLocalDateTime("startTime"),
        endTime = getLocalDateTime("endTime"),
        isPrivate = getRequiredBoolean("isPrivate"),
        isOnline = getRequiredBoolean("isOnline")
    )

}