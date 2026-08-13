package live.lb_trip.feature.recommendation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import live.lb_trip.core.designsystem.component.LbBottomActionBar
import live.lb_trip.core.designsystem.component.LbBottomActionButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.feature.recommendation.R

private val SavedButtonColors: ButtonColors
    @Composable get() = ButtonDefaults.buttonColors(containerColor = GreenTint, contentColor = Green)

@Composable
internal fun RecommendationCtaBar(isSaved: Boolean, onSaveClick: () -> Unit, modifier: Modifier = Modifier) {
    LbBottomActionBar(modifier = modifier) {
        LbBottomActionButton(
            text = stringResource(if (isSaved) R.string.recommendation_cta_saved else R.string.recommendation_cta_save),
            onClick = onSaveClick,
            colors = if (isSaved) SavedButtonColors else LbButtonDefaults.greenColors(),
            border = if (isSaved) BorderStroke(width = 1.dp, color = GreenLine) else null,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = if (isSaved) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Green,
                        modifier = Modifier.size(15.dp).padding(end = 6.dp),
                    )
                }
            } else {
                null
            },
        )
    }
}
