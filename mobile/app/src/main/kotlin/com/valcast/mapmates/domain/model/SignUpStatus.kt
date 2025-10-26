package com.valcast.mapmates.domain.model

sealed class SignUpStatus {
    data object NewUser : SignUpStatus()
    data object ExistingUser : SignUpStatus()
}