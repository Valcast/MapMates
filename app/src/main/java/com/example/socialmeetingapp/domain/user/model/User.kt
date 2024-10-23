package com.example.socialmeetingapp.domain.user.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Date

data class User(
    val id: String,
    val email: String,
    val username: String,
    val dateOfBirth: Date = Date(),
    val gender: String = "Not specified",
    val role: String = "User",
    val isVerified: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lastLogin: LocalDateTime,
    val lastPasswordChange: LocalDateTime,
    val bio: String = "",

    ) {

    companion object {
        val EMPTY = User(
            id = "",
            email = "",
            username = "",
            dateOfBirth = Date(),
            gender = "Not specified",
            role = "User",
            isVerified = false,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            lastLogin = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            lastPasswordChange = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            bio = ""
        )
    }
}
