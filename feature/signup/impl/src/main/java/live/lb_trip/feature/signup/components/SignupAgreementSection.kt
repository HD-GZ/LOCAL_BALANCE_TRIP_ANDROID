package live.lb_trip.feature.signup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.feature.signup.R

@Composable
internal fun AgreeBlock(
    termsAgreed: Boolean,
    privacyAgreed: Boolean,
    marketingAgreed: Boolean,
    onToggleTos: () -> Unit,
    onTogglePrivacy: () -> Unit,
    onToggleMarketing: () -> Unit,
    onToggleAll: () -> Unit,
    onTermsLabelClick: () -> Unit,
    onPrivacyLabelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val allAgreed = termsAgreed && privacyAgreed && marketingAgreed

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleAll)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AgreeCheckbox(checked = allAgreed)
            Text(
                text = stringResource(R.string.signup_agree_all),
                color = LbColors.Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(LbColors.LineSoft),
        )

        AgreeRow(
            checked = termsAgreed,
            label = stringResource(R.string.signup_terms),
            required = true,
            onClick = onToggleTos,
            onLabelClick = onTermsLabelClick,
        )
        AgreeRow(
            checked = privacyAgreed,
            label = stringResource(R.string.signup_privacy),
            required = true,
            onClick = onTogglePrivacy,
            onLabelClick = onPrivacyLabelClick,
        )
        AgreeRow(
            checked = marketingAgreed,
            label = stringResource(R.string.signup_marketing),
            required = false,
            onClick = onToggleMarketing,
        )
    }
}

@Composable
private fun AgreeRow(
    checked: Boolean,
    label: String,
    required: Boolean,
    onClick: () -> Unit,
    onLabelClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AgreeCheckbox(checked = checked)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(if (required) R.string.signup_required else R.string.signup_optional),
                color = if (required) LbColors.Green else LbColors.Ink3,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = label,
                color = LbColors.Ink2,
                fontSize = 13.5.sp,
                textDecoration = if (onLabelClick != null) TextDecoration.Underline else null,
                modifier = if (onLabelClick != null) Modifier.clickable(onClick = onLabelClick) else Modifier,
            )
        }
    }
}

@Composable
private fun AgreeCheckbox(checked: Boolean) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(if (checked) LbColors.Green else Color.Transparent)
            .border(1.dp, if (checked) LbColors.Green else LbColors.Line2, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}
