package com.example.socialmeetingapp.domain.user.model

import kotlinx.datetime.LocalDateTime
import java.util.Date

data class User(
    val id: String,
    val email: String,
    val username: String? = null,
    val dateOfBirth: Date? = null,
    val gender: String? = null,
    val status: String? = null,
    val role: String? = null,
    val isVerified: Boolean? = null,
    val createdAt: LocalDateTime? = null,
    val lastLogin: LocalDateTime? = null,
    val lastPasswordChange: LocalDateTime? = null,
    val bio: String? = null
)
