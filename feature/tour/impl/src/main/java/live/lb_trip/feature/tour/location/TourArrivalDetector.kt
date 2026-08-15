package live.lb_trip.feature.tour.location

import android.location.Location
import live.lb_trip.domain.model.LocationFix

/**
 * 목적지 도착 판정 상태머신. 정확도/오래된 위치를 걸러내고, 반경 안에 [ARRIVAL_DWELL_MILLIS]만큼
 * 머무르면 도착으로 판정한다. 반경을 벗어나면 체류 시각을 초기화한다.
 */
internal class TourArrivalDetector {

    private var inRadiusSinceElapsedRealtime: Long? = null

    fun reset() {
        inRadiusSinceElapsedRealtime = null
    }

    fun onLocationChanged(
        location: LocationFix,
        targetLatitude: Double,
        targetLongitude: Double,
        nowElapsedRealtime: Long,
    ): Boolean {
        val accuracy = location.accuracyMeters
        if (accuracy != null && accuracy > MAX_ACCURACY_METERS) return false
        if (location.ageMillis > MAX_LOCATION_AGE_MILLIS) return false

        val distance = distanceMeters(location.latitude, location.longitude, targetLatitude, targetLongitude)
        if (distance > ARRIVAL_RADIUS_METERS) {
            reset()
            return false
        }

        val since = inRadiusSinceElapsedRealtime ?: nowElapsedRealtime.also { inRadiusSinceElapsedRealtime = it }
        return nowElapsedRealtime - since >= ARRIVAL_DWELL_MILLIS
    }
}

private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    return results[0]
}

internal const val ARRIVAL_RADIUS_METERS = 200f
internal const val ARRIVAL_DWELL_MILLIS = 5_000L
internal const val MAX_ACCURACY_METERS = 50f
internal const val MAX_LOCATION_AGE_MILLIS = 60_000L
