package com.example.socialmeetingapp.domain.model

import android.location.Location

sealed class LocationResult {
    data class Success(val location: Location) : LocationResult()
    data class Error(val message: String) : LocationResult()
}