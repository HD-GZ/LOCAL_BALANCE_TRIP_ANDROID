package live.lb_trip.feature.onboarding

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbBottomActionBar
import live.lb_trip.core.designsystem.component.LbBottomActionButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbChip
import live.lb_trip.core.designsystem.component.LbChipDefaults

private val GreenGradient = Brush.linearGradient(
    colorStops = arrayOf(
        0.07f to Color(0xFF34744F),
        0.5f to Color(0xFF2B6447),
        0.9f to Color(0xFF234F3A),
    ),
    start = Offset(Float.POSITIVE_INFINITY, 0f),
    end = Offset(0f, Float.POSITIVE_INFINITY),
)

@Composable
internal fun OnboardingScreen(
    onSignupClick: () -> Unit,
    onSigninClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val activity = LocalActivity.current
    if (activity != null) {
        SideEffect {
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = false
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        HeroSection(modifier = Modifier.weight(1f))
        ActionSection(
            onSignupClick = onSignupClick,
            onSigninClick = onSigninClick,
        )
    }
}

@Composable
private fun HeroSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(GreenGradient),
    ) {
        LbChip(
            label = stringResource(R.string.onboarding_eyebrow),
            colors = LbChipDefaults.glassColors(),
            sizes = LbChipDefaults.glassSizes(),
            leading = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_onboarding_hero_mark),
                    contentDescription = null,
                    tint = LbColors.GreenGlassText,
                    modifier = Modifier.size(14.dp),
                )
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 26.dp, top = 30.dp),
        )

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_onboarding_bg),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
        )

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_onboarding_hero_mark),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.Center)
                .padding(bottom = 60.dp),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.onboarding_hero_prefix))
                    withStyle(SpanStyle(color = Color(0xFFBFE6CD))) {
                        append(stringResource(R.string.onboarding_hero_highlight))
                    }
                    append(stringResource(R.string.onboarding_hero_suffix))
                },
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 35.6.sp,
                letterSpacing = (-0.594).sp,
            )
            Text(
                text = stringResource(R.string.onboarding_description),
                color = Color(0xFFD6E7DC),
                fontSize = 13.5.sp,
                lineHeight = 21.6.sp,
            )
        }
    }
}

@Composable
private fun ActionSection(
    onSignupClick: () -> Unit,
    onSigninClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LbBottomActionBar(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LbChip(label = stringResource(R.string.onboarding_chip_ktx))
            LbChip(label = stringResource(R.string.onboarding_chip_half_price))
            LbChip(label = stringResource(R.string.onboarding_chip_tourism_resident))
        }
        Spacer(modifier = Modifier.height(7.dp))
        LbBottomActionButton(
            text = stringResource(R.string.onboarding_signup),
            onClick = onSignupClick,
            modifier = Modifier.fillMaxWidth(),
        )
        LbBottomActionButton(
            text = stringResource(R.string.onboarding_signin),
            onClick = onSigninClick,
            colors = LbButtonDefaults.whiteColors(),
            border = BorderStroke(width = 1.dp, color = LbColors.Line2),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreview() {
    OnboardingScreen(onSignupClick = {}, onSigninClick = {})
}
