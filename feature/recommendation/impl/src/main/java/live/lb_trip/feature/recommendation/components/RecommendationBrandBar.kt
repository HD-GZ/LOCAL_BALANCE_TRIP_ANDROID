package live.lb_trip.feature.recommendation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.feature.recommendation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecommendationBrandBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Text(text = title, color = Ink, fontSize = 15.5.sp, fontWeight = FontWeight.SemiBold)
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_back),
                        contentDescription = stringResource(R.string.recommendation_back_content_description),
                        tint = Color.Unspecified,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Paper),
        )
        HorizontalDivider(color = LineSoft, thickness = 1.dp)
    }
}
