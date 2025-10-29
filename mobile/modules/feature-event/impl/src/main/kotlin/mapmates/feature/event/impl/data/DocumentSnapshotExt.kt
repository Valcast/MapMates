package mapmates.feature.event.impl.data

import com.google.firebase.firestore.DocumentSnapshot
import mapmates.core.firebase.getInt
import mapmates.core.firebase.getLocalDateTime
import mapmates.core.firebase.getPairCoordinates
import mapmates.core.firebase.getRequiredBoolean
import mapmates.core.firebase.getRequiredString
import mapmates.core.firebase.getStringOrNull
import mapmates.feature.event.api.Event
import mapmates.feature.event.api.UserPreview
import mapmates.feature.event.api.filters.Category
import java.util.Locale

fun DocumentSnapshot.toEvent(
    author: UserPreview,
    participants: List<UserPreview>,
) = Event(
    id = id,
    title = getRequiredString("title"),
    description = getRequiredString("description"),
    locationCoordinates = getPairCoordinates("locationCoordinates"),
    locationAddress = getStringOrNull("locationAddress"),
    author = author,
    category = getRequiredString("category").let { Category.valueOf(it.uppercase(Locale.getDefault())) },
    participants = participants,
    maxParticipants = getInt("maxParticipants"),
    startTime = getLocalDateTime("startTime"),
    endTime = getLocalDateTime("endTime"),
    isPrivate = getRequiredBoolean("isPrivate"),
    isOnline = getRequiredBoolean("isOnline"),
    chatRoomId = getStringOrNull("chatRoomId"),
    meetingLink = getStringOrNull("meetingLink")
)


fun DocumentSnapshot.toUserPreview() = UserPreview(
    id = id,
    username = getRequiredString("username"),
    profilePictureUri = getRequiredString("profilePictureUri"),
    dateOfBirth = getLocalDateTime("dateOfBirth")
)
