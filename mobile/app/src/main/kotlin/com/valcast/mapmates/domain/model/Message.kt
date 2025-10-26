package com.valcast.mapmates.domain.model

import kotlinx.datetime.LocalDateTime

data class Message(
    val senderId: String,
    val text: String,
    val createdAt: LocalDateTime
)