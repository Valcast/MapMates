package com.valcast.mapmates.domain.model

data class EventPreview(
    val id: String,
    val title: String,
    val locationAddress: String?,
    val category: Category
)