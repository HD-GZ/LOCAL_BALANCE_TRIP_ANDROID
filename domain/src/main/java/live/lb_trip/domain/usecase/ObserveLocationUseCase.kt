package live.lb_trip.domain.usecase

import kotlinx.coroutines.flow.Flow
import live.lb_trip.domain.model.LocationFix
import live.lb_trip.domain.repository.LocationTracker
import javax.inject.Inject

class ObserveLocationUseCase @Inject constructor(
    private val locationTracker: LocationTracker,
) {
    operator fun invoke(): Flow<LocationFix> = locationTracker.locationUpdates()
}
