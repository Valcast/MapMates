package com.example.socialmeetingapp.domain.user.model

data class UserUpdateData(
    val bio: String? = null,
    val username: String? = null,
    val profileUri: String? = null
)