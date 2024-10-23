package com.example.socialmeetingapp.presentation.common

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
