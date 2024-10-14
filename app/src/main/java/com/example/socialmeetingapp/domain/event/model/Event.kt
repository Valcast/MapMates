package com.example.socialmeetingapp.domain.event.model

import com.example.socialmeetingapp.domain.user.model.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import java.util.Date

data class Event(
    val title: String,
    val description: String,
    var location: LatLng,
    val author: User? = null,
    val maxParticipants: Int,
    val date: Date,
    val time: Date,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val isPrivate: Boolean,
    val isOnline: Boolean,
    val duration: Int
)


