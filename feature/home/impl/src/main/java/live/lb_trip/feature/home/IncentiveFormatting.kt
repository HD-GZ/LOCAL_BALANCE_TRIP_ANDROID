package live.lb_trip.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
internal fun HomeIncentiveCard.ddayLabel(): String {
    val dday = dday
    return when {
        dday == null -> stringResource(R.string.home_incentive_dday_ongoing)
        dday < 0 -> stringResource(R.string.home_incentive_dday_closed)
        dday == 0L -> stringResource(R.string.home_incentive_dday_today)
        else -> stringResource(R.string.home_incentive_dday_template, dday)
    }
}

internal fun HomeIncentiveCard.formattedEndDate(): String? = endDate?.replace('-', '.')
