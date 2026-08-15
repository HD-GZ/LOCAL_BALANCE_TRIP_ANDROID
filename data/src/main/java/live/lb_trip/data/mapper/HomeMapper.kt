package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.HeroItemResponseDto
import live.lb_trip.data.dto.response.HomeHeroResponseDto
import live.lb_trip.data.dto.response.HomeIncentivesResponseDto
import live.lb_trip.data.dto.response.HomeProfileSummaryResponseDto
import live.lb_trip.data.dto.response.HomeProfileTypesResponseDto
import live.lb_trip.data.dto.response.IncentiveResponseDto
import live.lb_trip.data.dto.response.ProfileSliderResponseDto
import live.lb_trip.data.dto.response.ProfileTypeResponseDto
import live.lb_trip.data.dto.response.RegionTabResponseDto
import live.lb_trip.domain.model.HeroItem
import live.lb_trip.domain.model.Incentive
import live.lb_trip.domain.model.ProfileSlider
import live.lb_trip.domain.model.ProfileSummary
import live.lb_trip.domain.model.ProfileType
import live.lb_trip.domain.model.RegionIncentives

fun HomeHeroResponseDto.toDomain(): List<HeroItem> = items.map { it.toDomain() }

fun HeroItemResponseDto.toDomain(): HeroItem = HeroItem(imageUrl = imageUrl, title = title)

fun HomeProfileTypesResponseDto.toDomain(): List<ProfileType> = types.map { it.toDomain() }

fun ProfileTypeResponseDto.toDomain(): ProfileType =
    ProfileType(code = code, nickname = nickname, description = description, imageUrl = imageUrl)

fun HomeProfileSummaryResponseDto.toDomain(): ProfileSummary =
    ProfileSummary(
        type = type,
        description = description,
        imageUrl = imageUrl,
        diagnosedAt = diagnosedAt,
        sliders = sliders.map { it.toDomain() },
    )

fun ProfileSliderResponseDto.toDomain(): ProfileSlider =
    ProfileSlider(key = key, minLabel = minLabel, maxLabel = maxLabel, score = score)

fun HomeIncentivesResponseDto.toDomain(): List<RegionIncentives> = regions.map { it.toDomain() }

fun RegionTabResponseDto.toDomain(): RegionIncentives =
    RegionIncentives(
        regionName = regionName,
        incentives = incentives.map { it.toDomain() },
    )

fun IncentiveResponseDto.toDomain(): Incentive =
    Incentive(title = title, description = description, url = url, endDate = endDate, dday = dday)
