package com.example.socialmeetingapp.presentation.common

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.Serializable

object NavigationManager {
    private var _route = MutableSharedFlow<Routes>(replay = 1)
    val route = _route.asSharedFlow()

    fun navigateTo(event: Routes) {
        _route.tryEmit(event)
    }




}


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
    data class Profile(val userID: String) : Routes()

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

    companion object {
        fun fromString(route: String): Routes? {
            // Rozdzielamy nazwę pakietu od samej trasy
            val path = route.substringAfterLast(".")
            val segments = path.split("/")

            return when (segments[0]) {
                "Map" -> Routes.Map
                "Login" -> Routes.Login
                "Register" -> Routes.Register
                "Settings" -> Routes.Settings
                "Introduction" -> Routes.Introduction
                "ForgotPassword" -> Routes.ForgotPassword
                "RegisterProfileInfo" -> Routes.RegisterProfileInfo
                "RegisterLocation" -> Routes.RegisterLocation
                "Profile" -> {
                    // Zakładamy, że profile jest w formacie Profile/{userID}
                    if (segments.size == 2) {
                        val userId = segments[1]
                        Routes.Profile(userId)
                    } else {
                        null // Zwróć null, jeśli format jest nieprawidłowy
                    }
                }
                "Event" -> {
                    // Zakładamy, że event jest w formacie Event/{id}
                    if (segments.size == 2) {
                        val id = segments[1]
                        Routes.Event(id)
                    } else {
                        null // Zwróć null, jeśli format jest nieprawidłowy
                    }
                }
                else -> null // Zwróć null dla nieznanych tras
            }
        }

    }



}
