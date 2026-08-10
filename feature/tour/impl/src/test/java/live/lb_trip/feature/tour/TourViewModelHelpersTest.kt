package live.lb_trip.feature.tour

import live.lb_trip.domain.model.TourPlaceVisit
import org.junit.Assert.assertEquals
import org.junit.Test

class TourViewModelHelpersTest {

    @Test
    fun `방문 기록이 없으면 첫 번째 정류장부터 시작한다`() {
        val index = restoredStopIndex(visitsByOrder = emptyMap(), lastIndex = 3)

        assertEquals(0, index)
    }

    @Test
    fun `미방문 정류장이 있으면 그 정류장을 현재 위치로 복원한다`() {
        val visits = listOf(
            TourPlaceVisit(placeId = 1, order = 1, visited = true),
            TourPlaceVisit(placeId = 2, order = 2, visited = true),
            TourPlaceVisit(placeId = 3, order = 3, visited = false),
            TourPlaceVisit(placeId = 4, order = 4, visited = false),
        ).associateBy { it.order }

        val index = restoredStopIndex(visitsByOrder = visits, lastIndex = 3)

        // 첫 미방문 정류장인 order 3은 배열 인덱스 2(order - 1)
        assertEquals(2, index)
    }

    @Test
    fun `모든 정류장을 방문했으면 마지막 정류장으로 복원한다`() {
        val visits = listOf(
            TourPlaceVisit(placeId = 1, order = 1, visited = true),
            TourPlaceVisit(placeId = 2, order = 2, visited = true),
        ).associateBy { it.order }

        val index = restoredStopIndex(visitsByOrder = visits, lastIndex = 1)

        assertEquals(1, index)
    }

    @Test
    fun `복원된 인덱스는 유효 범위를 벗어나지 않는다`() {
        val visits = listOf(
            TourPlaceVisit(placeId = 1, order = 1, visited = false),
        ).associateBy { it.order }

        val index = restoredStopIndex(visitsByOrder = visits, lastIndex = 3)

        assertEquals(0, index)
    }
}
