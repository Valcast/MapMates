package com.example.socialmeetingapp.domain.model

import android.net.Uri
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class UserPreview(
    val id: String,
    val username: String,
    val profilePictureUri: Uri,
    val dateOfBirth: LocalDateTime
) {
    companion object {
        val EMPTY = UserPreview(
            id = "",
            username = "",
            profilePictureUri = Uri.EMPTY,
            dateOfBirth = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        )
    }
}