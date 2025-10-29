package mapmates.core.location.api

sealed interface LocationResult {

    data class Success(val latitude: Double, val longitude: Double) : LocationResult
    data class Failure(val error: LocationError) : LocationResult
}