package com.example.socialmeetingapp.domain.user.model

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
    val createdAt: Date = Date(),
    val lastLogin: Date? = null,
    val lastPasswordChange: Date? = null,
    val bio: String? = null
)
