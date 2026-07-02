package live.lb_trip.feature.onboarding

import android.app.Activity
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.slack.circuit.runtime.ui.Ui
import live.lb_trip.core.designsystem.component.LbButton
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

class OnboardingUi : Ui<OnboardingState> {
    @Composable
    override fun Content(state: OnboardingState, modifier: Modifier) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }

        Column(modifier = modifier.fillMaxSize()) {
            HeroSection(modifier = Modifier.weight(1f))
            ActionSection(
                onSignupClick = { state.eventSink(OnboardingEvent.NavigateToSignup) },
                onSigninClick = { state.eventSink(OnboardingEvent.NavigateToSignin) },
            )
        }
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
            label = "취향 · 가치소비 기반 AI 큐레이션",
            colors = LbChipDefaults.glassColors(),
            sizes = LbChipDefaults.glassSizes(),
            leading = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_onboarding_hero_mark),
                    contentDescription = null,
                    tint = Color(0xFFDFF0E6),
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
                    append("내 취향과 예산에 맞춘\n")
                    withStyle(SpanStyle(color = Color(0xFFBFE6CD))) {
                        append("로컬 슬로우 트립")
                    }
                    append("을 설계해요")
                },
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 35.6.sp,
                letterSpacing = (-0.594).sp,
            )
            Text(
                text = "친환경 로컬 코스 추천부터 KTX 할인·반값여행·관광주민증 혜택까지 자동으로 연결해요.",
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 26.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LbChip(label = "KTX 할인")
            LbChip(label = "반값여행")
            LbChip(label = "관광주민증")
        }
        Spacer(modifier = Modifier.height(7.dp))
        LbButton(
            onClick = onSignupClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = LbButtonDefaults.greenColors(),
        ) {
            Text(
                text = "이메일로 회원가입",
                fontSize = 15.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.155).sp,
            )
        }
        LbButton(
            onClick = onSigninClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = LbButtonDefaults.whiteColors(),
            border = BorderStroke(width = 1.dp, color = Color(0xFFC3BDB3)),
        ) {
            Text(
                text = "이미 계정이 있어요 · 로그인",
                fontSize = 15.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.155).sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreview() {
    OnboardingUi().Content(state = OnboardingState(), modifier = Modifier)
}
