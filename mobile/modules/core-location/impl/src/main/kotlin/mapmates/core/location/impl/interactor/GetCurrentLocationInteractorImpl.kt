package mapmates.core.location.impl.interactor

import android.Manifest
import androidx.annotation.RequiresPermission
import mapmates.core.location.api.interactor.GetCurrentLocationInteractor
import mapmates.core.location.impl.data.LocationRepository

internal class GetCurrentLocationInteractorImpl(
    private val locationRepository: LocationRepository
) : GetCurrentLocationInteractor {

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun invoke() = locationRepository.getCurrentLocation()
}