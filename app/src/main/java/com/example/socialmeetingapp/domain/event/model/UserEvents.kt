package com.example.socialmeetingapp.domain.event.model

data class UserEvents(
    val createdEvents: List<Event>,
    val joinedEvents: List<Event>
)