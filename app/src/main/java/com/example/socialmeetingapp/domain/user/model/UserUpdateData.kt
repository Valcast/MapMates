package com.example.socialmeetingapp.domain.user.model

import java.util.Date

data class UserUpdateData(
    val bio: String? = null,
    val username: String? = null,
    val lastLogin: Date? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)