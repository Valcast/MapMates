package com.example.socialmeetingapp.domain.model

import android.net.Uri
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class User(
    val id: String,
    val email: String,
    val username: String,
    val dateOfBirth: LocalDateTime,
    val gender: String = "Not specified",
    val followingCount: Int,
    val followersCount: Int,
    val createdAt: LocalDateTime,
    val profilePictureUri: Uri,
    val bio: String = "",

    ) {

    @OptIn(ExperimentalTime::class)
    companion object {
        val EMPTY = User(
            id = "",
            email = "",
            username = "",
            dateOfBirth = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            gender = "Not specified",
            followingCount = 0,
            followersCount = 0,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            bio = "",
            profilePictureUri = Uri.EMPTY
        )
    }
}
