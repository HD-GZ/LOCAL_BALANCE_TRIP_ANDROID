package live.lb_trip.feature.tour.location

import live.lb_trip.domain.model.LocationFix
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TourArrivalDetectorTest {

    private val targetLatitude = 37.5665
    private val targetLongitude = 126.9780

    @Test
    fun `반경 안에 처음 진입하면 dwell 시간이 지나지 않아 미도착으로 판정한다`() {
        val detector = TourArrivalDetector()

        val arrived = detector.onLocationChanged(
            location = fixAt(targetLatitude, targetLongitude),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = 0L,
        )

        assertFalse(arrived)
    }

    @Test
    fun `반경 안에서 dwell 시간이 지나면 도착으로 판정한다`() {
        val detector = TourArrivalDetector()
        detector.onLocationChanged(
            location = fixAt(targetLatitude, targetLongitude),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = 0L,
        )

        val arrived = detector.onLocationChanged(
            location = fixAt(targetLatitude, targetLongitude),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = ARRIVAL_DWELL_MILLIS,
        )

        assertTrue(arrived)
    }

    @Test
    fun `반경을 벗어나면 dwell 시계가 초기화된다`() {
        val detector = TourArrivalDetector()
        detector.onLocationChanged(
            location = fixAt(targetLatitude, targetLongitude),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = 0L,
        )
        detector.onLocationChanged(
            location = fixAt(targetLatitude + FAR_AWAY_LATITUDE_OFFSET, targetLongitude),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = ARRIVAL_DWELL_MILLIS / 2,
        )

        val arrived = detector.onLocationChanged(
            location = fixAt(targetLatitude, targetLongitude),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = ARRIVAL_DWELL_MILLIS,
        )

        assertFalse(arrived)
    }

    @Test
    fun `정확도가 낮은 위치는 무시하고 dwell 시계에 영향을 주지 않는다`() {
        val detector = TourArrivalDetector()
        detector.onLocationChanged(
            location = fixAt(targetLatitude, targetLongitude, accuracyMeters = MAX_ACCURACY_METERS + 1f),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = 0L,
        )

        val arrived = detector.onLocationChanged(
            location = fixAt(targetLatitude, targetLongitude),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = ARRIVAL_DWELL_MILLIS,
        )

        assertFalse(arrived)
    }

    @Test
    fun `오래된 위치는 무시하고 dwell 시계에 영향을 주지 않는다`() {
        val detector = TourArrivalDetector()
        detector.onLocationChanged(
            location = fixAt(targetLatitude, targetLongitude, ageMillis = MAX_LOCATION_AGE_MILLIS + 1),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = 0L,
        )

        val arrived = detector.onLocationChanged(
            location = fixAt(targetLatitude, targetLongitude),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = ARRIVAL_DWELL_MILLIS,
        )

        assertFalse(arrived)
    }

    @Test
    fun `reset 호출 시 dwell 시계가 초기화된다`() {
        val detector = TourArrivalDetector()
        detector.onLocationChanged(
            location = fixAt(targetLatitude, targetLongitude),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = 0L,
        )

        detector.reset()

        val arrived = detector.onLocationChanged(
            location = fixAt(targetLatitude, targetLongitude),
            targetLatitude = targetLatitude,
            targetLongitude = targetLongitude,
            nowElapsedRealtime = ARRIVAL_DWELL_MILLIS,
        )

        assertFalse(arrived)
    }

    private fun fixAt(
        latitude: Double,
        longitude: Double,
        accuracyMeters: Float? = 10f,
        ageMillis: Long = 0L,
    ) = LocationFix(latitude = latitude, longitude = longitude, accuracyMeters = accuracyMeters, ageMillis = ageMillis)
}

private const val FAR_AWAY_LATITUDE_OFFSET = 0.01
