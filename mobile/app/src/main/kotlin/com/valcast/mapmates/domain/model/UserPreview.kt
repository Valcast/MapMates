package com.valcast.mapmates.domain.model

import android.net.Uri
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class UserPreview(
    val id: String,
    val username: String,
    val profilePictureUri: Uri,
    val dateOfBirth: LocalDateTime
) {
    @OptIn(ExperimentalTime::class)
    companion object {
        val EMPTY = UserPreview(
            id = "",
            username = "",
            profilePictureUri = Uri.EMPTY,
            dateOfBirth = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        )
    }
}