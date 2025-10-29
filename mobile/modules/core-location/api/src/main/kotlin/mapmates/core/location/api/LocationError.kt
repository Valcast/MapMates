package mapmates.core.location.api

sealed interface LocationError {
    object GpsDisabled : LocationError
    object LocationPermissionDisabled : LocationError
    object Unknown : LocationError
}