package com.example.socialmeetingapp.domain.model

import android.net.Uri
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class User(
    val id: String,
    val email: String,
    val username: String,
    val dateOfBirth: LocalDateTime,
    val gender: String = "Not specified",
    val role: String = "User",
    val following: List<String> = emptyList(),
    val followers: List<String> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val lastLogin: LocalDateTime,
    val lastPasswordChange: LocalDateTime?,
    val profilePictureUri: Uri,
    val bio: String = "",

    ) {

    companion object {
        val EMPTY = User(
            id = "",
            email = "",
            username = "",
            dateOfBirth = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            gender = "Not specified",
            role = "User",
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            lastLogin = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            lastPasswordChange = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            bio = "",
            profilePictureUri = Uri.EMPTY
        )
    }
}
