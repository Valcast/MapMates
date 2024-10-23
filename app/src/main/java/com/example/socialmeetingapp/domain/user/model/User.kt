package com.example.socialmeetingapp.domain.user.model

import kotlinx.datetime.LocalDateTime
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
)
