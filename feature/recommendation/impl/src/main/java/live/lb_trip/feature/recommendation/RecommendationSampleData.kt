package live.lb_trip.feature.recommendation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class RecommendedRegion(
    val provinceShortName: String,
    val provinceFullName: String,
    val name: String,
    val best: Boolean = false,
    val reason: String,
)

data class RecommendedCourse(
    val name: String,
    val best: Boolean = false,
    val reason: String,
)

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

object RecommendationSampleData {

    val regions: ImmutableList<RecommendedRegion> = persistentListOf(
        RecommendedRegion(
            provinceShortName = "전남",
            provinceFullName = "전라남도",
            name = "담양군",
            best = true,
            reason = "방문객이 적어 한적하고, 대숲과 골목 상권이 풍부해 로컬 미식·산책에 잘 맞아요.",
        ),
        RecommendedRegion(
            provinceShortName = "경북",
            provinceFullName = "경상북도",
            name = "영양군",
            reason = "관광객 발길이 드문 청정 오지 — 느긋한 쉼과 자연 몰입을 선호하는 성향과 어울려요.",
        ),
        RecommendedRegion(
            provinceShortName = "충남",
            provinceFullName = "충청남도",
            name = "서천군",
            reason = "갯벌 생태와 전통시장 노포가 많아 실속형 로컬 미식·생활 체험에 잘 맞아요.",
        ),
        RecommendedRegion(
            provinceShortName = "강원",
            provinceFullName = "강원도",
            name = "정선군",
            reason = "폐광·산촌 정취와 걷기 좋은 둘레길이 많아, 활동보다 쉼을 우선하는 코스에 어울려요.",
        ),
        RecommendedRegion(
            provinceShortName = "전북",
            provinceFullName = "전라북도",
            name = "임실군",
            reason = "치즈마을·소도시 감성과 직접 해보는 생활 체험형 코스가 풍부해요.",
        ),
    )

    val courses: ImmutableList<RecommendedCourse> = persistentListOf(
        RecommendedCourse(
            name = "남도 골목 미식 슬로우 트립",
            best = true,
            reason = "실속 소비 + 로컬 미식 성향을 반영해 노포·골목 상권 위주로 짰어요.",
        ),
        RecommendedCourse(
            name = "대숲 사이 힐링 산책 코스",
            reason = "느긋한 쉼·자연 몰입을 우선한 저강도 도보 동선이에요.",
        ),
        RecommendedCourse(
            name = "담양 전통시장 생활 체험",
            reason = "직접 해보기·생활 체험 선호를 담아 시장·공방 중심으로 구성했어요.",
        ),
    )

    val stops: ImmutableList<CourseStop> = persistentListOf(
        CourseStop(
            order = 1,
            name = "죽녹원",
            hasAudioGuide = true,
            walkDuration = "6분",
            description = "담양을 대표하는 대숲 정원. 8만여 그루 대나무가 만드는 그늘길을 20분 코스로 여유롭게 걸어요.",
        ),
        CourseStop(
            order = 2,
            name = "백련동 발효빵집",
            hasAudioGuide = true,
            walkDuration = "8분",
            description = "대숲 발효종으로 굽는 로컬 베이커리. 담양 쌀누룩 발효빵이 시그니처예요.",
        ),
        CourseStop(
            order = 3,
            name = "담양 관방제림",
            hasAudioGuide = false,
            walkDuration = "5분",
            description = "300여 년 된 방죽 숲길. 느티나무 그늘 아래 500m 산책로를 여유롭게 걸어요.",
        ),
        CourseStop(
            order = 4,
            name = "국수거리 노포 점심",
            hasAudioGuide = true,
            walkDuration = "4분",
            description = "영산강 둑방 옆 멸치국수 노포 거리. 실속 한 끼로 담양 로컬의 맛을 봐요.",
        ),
        CourseStop(
            order = 5,
            name = "죽세공예 체험공방",
            hasAudioGuide = false,
            walkDuration = null,
            description = "대나무로 컵받침·바구니를 직접 엮어보는 30분 생활 체험이에요.",
        ),
    )

    val incentives: ImmutableList<Incentive> = persistentListOf(
        Incentive(title = "KTX 인구감소지역 할인", subtitle = "코레일 공식 채널로 이동"),
        Incentive(title = "반값여행 환급", subtitle = "청년 추가 환급 대상"),
        Incentive(title = "디지털 관광주민증", subtitle = "담양 가맹점 12곳 할인"),
    )
}
