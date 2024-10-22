package com.example.socialmeetingapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
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
