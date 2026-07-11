package live.lb_trip.feature.recommendation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.feature.recommendation.R

private val SavedButtonColors: ButtonColors
    @Composable get() = ButtonDefaults.buttonColors(containerColor = GreenTint, contentColor = Green)

@Composable
internal fun RecommendationCtaBar(
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onTourStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = LineSoft, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Paper)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 14.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            LbButton(
                onClick = onSaveClick,
                colors = if (isSaved) SavedButtonColors else LbButtonDefaults.whiteColors(),
                border = BorderStroke(width = 1.dp, color = if (isSaved) GreenLine else Line2),
                modifier = Modifier.weight(1f).height(46.dp),
            ) {
                if (isSaved) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Green,
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = stringResource(if (isSaved) R.string.recommendation_cta_saved else R.string.recommendation_cta_save),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            LbButton(
                onClick = onTourStartClick,
                colors = LbButtonDefaults.greenColors(),
                modifier = Modifier.weight(1f).height(46.dp),
            ) {
                Text(
                    text = stringResource(R.string.recommendation_cta_tour_start),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
