package com.example.socialmeetingapp.presentation.common

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {

    @Serializable
    data class Map(val latitude: Double? = null, val longitude: Double? = null) : Routes()

    @Serializable
    data object Login : Routes()

    @Serializable
    data object Register : Routes()

    @Serializable
    data object Settings : Routes()

    @Serializable
    data object Activities : Routes()

    @Serializable
    data class Profile(val userID: String = "") : Routes()

    @Serializable
    data object CreateProfile : Routes()

    @Serializable
    data object EditProfile : Routes()

    @Serializable
    data object Notifications : Routes()

    @Serializable
    data object Introduction : Routes()

    @Serializable
    data object ForgotPassword : Routes()

    @Serializable
    data class CreateEvent(val latitude: Double, val longitude: Double) : Routes()

    @Serializable
    data class Event(val id: String) : Routes()
}