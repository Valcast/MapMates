package com.example.socialmeetingapp.presentation.common

import android.util.Log
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
    data object Activities : Routes()

    @Serializable
    data class Profile(val userID: String) : Routes()

    @Serializable
    data object CreateProfile : Routes()

    @Serializable
    data object Introduction : Routes()

    @Serializable
    data object ForgotPassword : Routes()

    @Serializable
    data class CreateEvent(val latitude: Double, val longitude: Double) : Routes()

    @Serializable
    data class Event(val id: String) : Routes()

    @Serializable
    data object MyProfile : Routes()

    companion object {
        fun fromString(route: String): Routes? {
            val path = route.substringAfterLast(".")
            val segments = path.split("/")

            return when (segments[0]) {
                "Map" -> Map
                "Activities" -> Activities
                "Login" -> Login
                "MyProfile" -> MyProfile
                "Register" -> Register
                "Settings" -> Settings
                "Introduction" -> Introduction
                "CreateProfile" -> CreateProfile
                "ForgotPassword" -> ForgotPassword
                "Profile" -> {
                    if (segments.size == 2) {
                        val userId = segments[1]
                        Profile(userId)
                    } else {
                        null
                    }
                }
                "Event" -> {
                    if (segments.size == 2) {
                        val id = segments[1]
                        Event(id)
                    } else {
                        null
                    }
                }
                else -> null
            }
        }

    }



}