package com.example.socialmeetingapp.domain.model

data class UserEvents(
    val createdEvents: List<Event>,
    val joinedEvents: List<Event>
)