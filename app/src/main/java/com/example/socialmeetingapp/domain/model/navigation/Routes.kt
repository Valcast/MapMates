package com.example.socialmeetingapp.domain.model.navigation

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

}