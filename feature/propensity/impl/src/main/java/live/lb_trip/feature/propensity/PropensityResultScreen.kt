package live.lb_trip.feature.propensity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.feature.propensity.components.Brand
import live.lb_trip.feature.propensity.components.OutlineBorder
import live.lb_trip.feature.propensity.components.PropensityStepShell
import live.lb_trip.feature.propensity.components.TextSecondary

@Composable
internal fun PropensityResultScreen(
    state: PropensityUiState,
    onBack: () -> Unit,
    onIntent: (PropensityIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PropensityStepShell(
        stepNumber = 3,
        title = stringResource(R.string.propensity_title_result),
        headerTitle = stringResource(R.string.propensity_header_result_title),
        headerSubtitle = stringResource(R.string.propensity_header_result_subtitle),
        state = state,
        onBack = onBack,
        modifier = modifier,
    ) {
        ResultStepContent(
            state = state,
            onIntent = onIntent,
        )
    }
}

@Composable
private fun ResultStepContent(
    state: PropensityUiState,
    onIntent: (PropensityIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components { add(OkHttpNetworkFetcherFactory()) }.build()
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.resultImageUrl != null) {
            AsyncImage(
                model = state.resultImageUrl,
                contentDescription = null,
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .border(BorderStroke(1.dp, OutlineBorder), RoundedCornerShape(36.dp)),
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
        if (!state.resultCode.isNullOrBlank()) {
            Text(
                text = state.resultCode,
                color = Brand,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Text(
            text = state.resultType.orEmpty(),
            color = Brand,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = state.resultDescription.orEmpty(),
            color = TextSecondary,
            fontSize = 12.5.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LbButton(
                onClick = { onIntent(PropensityIntent.RestartDiagnosis) },
                colors = LbButtonDefaults.whiteColors(),
                border = BorderStroke(width = 1.dp, color = OutlineBorder),
                modifier = Modifier.weight(1f).height(52.dp),
            ) {
                Text(
                    text = stringResource(R.string.propensity_cta_restart),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            LbButton(
                onClick = { onIntent(PropensityIntent.CourseRecommendationClicked) },
                colors = LbButtonDefaults.greenColors(),
                modifier = Modifier.weight(1f).height(52.dp),
            ) {
                Text(
                    text = stringResource(R.string.propensity_cta_course_recommendation),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PropensityResultPreview() {
    PropensityResultScreen(
        state = PropensityUiState(
            resultType = "실속형 로컬 체험 여행자",
            resultCode = "LVEAI",
            resultDescription = "럭셔리보다 실속을, 유명 명소보다 골목 상권을, 눈으로 보는 관람보다 직접 해보는 체험을 즐기는 홀로 떠나는 1인 여행자예요.",
        ),
        onBack = {},
        onIntent = {},
    )
}
