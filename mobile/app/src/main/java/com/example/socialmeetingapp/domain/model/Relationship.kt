package com.example.socialmeetingapp.domain.model

import kotlinx.datetime.LocalDateTime

data class Relationship(
    val userPreview: UserPreview,
    val followedAt: LocalDateTime
)