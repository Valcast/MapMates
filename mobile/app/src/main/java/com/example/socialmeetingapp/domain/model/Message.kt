package com.example.socialmeetingapp.domain.model

import kotlinx.datetime.LocalDateTime

data class Message(
    val senderId: String,
    val text: String,
    val createdAt: LocalDateTime
)