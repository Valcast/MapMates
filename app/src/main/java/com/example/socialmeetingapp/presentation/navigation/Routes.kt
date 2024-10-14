package com.example.socialmeetingapp.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {
    @Serializable
    data object Map : Routes()

    @Serializable
    data object Login : Routes()

    @Serializable
    data object Register : Routes()

    @Serializable
    data object Settings : Routes()

    @Serializable
    data object Profile : Routes()

    @Serializable
    data object Introduction : Routes()

    @Serializable
    data object ForgotPassword : Routes()

    @Serializable
    data object RegisterProfileInfo : Routes()

    @Serializable
    data object RegisterLocation : Routes()

    @Serializable
    data class CreateEvent(val latitude: Double, val longitude: Double) : Routes()
    
    @Serializable
    data class Event(val id: String) : Routes()

}

