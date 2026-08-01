package live.lb_trip.feature.recommendation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.feature.recommendation.R

@Composable
internal fun RecommendationBrandBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LbTopBar(
        title = title,
        onBackClick = onBackClick,
        backContentDescription = stringResource(R.string.recommendation_back_content_description),
        modifier = modifier,
    )
}
