package live.lb_trip.feature.onboarding

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.slack.circuit.runtime.ui.Ui
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults

private val HeroGradient = Brush.linearGradient(
    colorStops = arrayOf(
        0f to Color(0xFF34744F),
        0.52f to Color(0xFF2B6447),
        1f to Color(0xFF234F3A),
    ),
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

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(HeroGradient)
                    .windowInsetsPadding(WindowInsets.statusBars),
            ) {
                TopoLines(modifier = Modifier.fillMaxSize())
                HeroContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp),
                )
            }

            OnboardingFooter(
                onSignupClick = { state.eventSink(OnboardingEvent.NavigateToSignup) },
                onSigninClick = { state.eventSink(OnboardingEvent.NavigateToSignin) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
            )
        }
    }
}

@Composable
private fun TopoLines(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val scaleX = size.width / 412f
        val scaleY = size.height / 520f
        val strokeWidth = 1.4f * scaleX

        for (i in 0..6) {
            val path = Path()
            val startY = (120 + i * 52) * scaleY
            path.moveTo(-20 * scaleX, startY)

            val cp1x = 90 * scaleX
            val cp1y = (70 + i * 52) * scaleY
            val cp2x = 150 * scaleX
            val cp2y = (175 + i * 52) * scaleY
            val ex = 230 * scaleX
            val ey = (130 + i * 52) * scaleY

            path.cubicTo(cp1x, cp1y, cp2x, cp2y, ex, ey)

            // SVG S command: reflect last control point around current endpoint
            val rcp1x = 2 * ex - cp2x
            val rcp1y = 2 * ey - cp2y
            path.cubicTo(
                rcp1x, rcp1y,
                380 * scaleX, (60 + i * 52) * scaleY,
                440 * scaleX, (120 + i * 52) * scaleY,
            )

            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.14f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
    }
}

@Composable
private fun HeroContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            Text(
                text = "취향 · 가치소비 기반 AI 큐레이션",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                letterSpacing = (-0.12).sp,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        BalanceMark(modifier = Modifier.size(150.dp))

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = buildAnnotatedString {
                append("내 취향과 예산에 맞춘\n")
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append("로컬 슬로우 트립")
                }
                append("을 설계해요")
            },
            color = Color.White,
            fontSize = 23.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.506).sp,
            textAlign = TextAlign.Center,
            lineHeight = 33.sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "친환경 로컬 코스 추천부터 KTX 할인·반값여행·관광주민증 혜택까지\n자동으로 연결해요.",
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun BalanceMark(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val s = size.width / 150f
        val cx = size.width / 2
        val cy = size.height / 2
        val center = Offset(cx, cy)

        drawCircle(
            color = Color.White.copy(alpha = 0.16f),
            radius = 70 * s,
            center = center,
            style = Stroke(width = 1.4f * s),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.22f),
            radius = 56 * s,
            center = center,
            style = Stroke(width = 1.4f * s),
        )
        drawRoundRect(
            color = Color(0xFFEAF6EE),
            topLeft = Offset(33 * s, 33 * s),
            size = Size(84 * s, 84 * s),
            cornerRadius = CornerRadius(26 * s),
            style = Stroke(width = 2.4f * s),
        )
        drawCircle(
            color = Color(0xFFEAF6EE),
            radius = 22 * s,
            center = center,
            style = Stroke(width = 2.4f * s),
        )
        drawLine(
            color = Color(0xFFBFE6CD),
            start = Offset(75 * s, 22 * s),
            end = Offset(75 * s, 128 * s),
            strokeWidth = 2.4f * s,
        )
        drawCircle(
            color = Color(0xFFBFE6CD),
            radius = 4.5f * s,
            center = Offset(75 * s, 53 * s),
        )
        drawCircle(
            color = Color(0xFFEAF6EE),
            radius = 4.5f * s,
            center = Offset(75 * s, 97 * s),
        )
    }
}

@Composable
private fun OnboardingFooter(
    onSignupClick: () -> Unit,
    onSigninClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("KTX 할인", "반값여행", "관광주민증").forEach { label ->
                Row(
                    modifier = Modifier
                        .border(1.dp, Color(0xFFC4DDCD), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2F6F4F)),
                    )
                    Text(
                        text = label,
                        color = Color(0xFF2F6F4F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(11.dp))

        LbButton(
            onClick = onSigninClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = LbButtonDefaults.whiteColors(),
            border = BorderStroke(1.dp, Color(0xFFC3BDB3)),
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
