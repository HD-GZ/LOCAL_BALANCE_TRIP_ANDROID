package live.lb_trip.feature.recommendation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class CourseStop(
    val order: Int,
    val name: String,
    val hasAudioGuide: Boolean,
    val walkDuration: String?,
    val description: String,
    val audioUrl: String? = null,
)

data class Incentive(
    val title: String,
    val subtitle: String,
)

// No incentive/discount endpoint exists yet; kept hardcoded until the API supports it.
object RecommendationStubData {
    val incentives: ImmutableList<Incentive> = persistentListOf(
        Incentive(title = "KTX 인구감소지역 할인", subtitle = "코레일 공식 채널로 이동"),
        Incentive(title = "반값여행 환급", subtitle = "청년 추가 환급 대상"),
        Incentive(title = "디지털 관광주민증", subtitle = "가맹점 할인"),
    )
}
