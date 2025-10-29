package mapmates.core.location.api.interactor

import mapmates.core.location.api.LocationResult

interface GetCurrentLocationInteractor {
    suspend operator fun invoke(): LocationResult
}