package com.example.socialmeetingapp.domain.user.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime

data class UserUpdateData(
    val bio: String? = null,
    val username: String? = null,
    val lastLogin: Instant? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)