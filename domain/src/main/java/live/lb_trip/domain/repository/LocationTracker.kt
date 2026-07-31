package live.lb_trip.domain.repository

import kotlinx.coroutines.flow.Flow
import live.lb_trip.domain.model.LocationFix

interface LocationTracker {
    fun locationUpdates(): Flow<LocationFix>
}
