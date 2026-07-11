package live.lb_trip.feature.recommendation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.view.WindowCompat
import live.lb_trip.feature.recommendation.components.Ink
import live.lb_trip.feature.recommendation.components.Ink2
import live.lb_trip.feature.recommendation.components.Paper
import live.lb_trip.feature.recommendation.components.RCard
import live.lb_trip.feature.recommendation.components.RecommendationBrandBar
import live.lb_trip.feature.recommendation.components.RecommendationFlowStepper
import live.lb_trip.feature.recommendation.components.ScreenBg

@Composable
internal fun CourseScreen(
    regionIndex: Int,
    onBack: () -> Unit,
    onCourseSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    val region = RecommendationSampleData.regions.getOrNull(regionIndex)

    Box(modifier = modifier.fillMaxSize().background(Paper)) {
        Column(modifier = Modifier.fillMaxSize()) {
            RecommendationBrandBar(
                title = stringResource(
                    R.string.recommendation_title_course,
                    region?.provinceShortName.orEmpty(),
                    region?.name.orEmpty(),
                ),
                onBackClick = onBack,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(ScreenBg)
                    .verticalScroll(rememberScrollState())
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(vertical = 16.dp),
            ) {
                RecommendationFlowStepper(
                    currentStep = 2,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(18.dp))

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(
                        text = stringResource(
                            R.string.recommendation_title_course,
                            region?.provinceShortName.orEmpty(),
                            region?.name.orEmpty(),
                        ),
                        color = Ink,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(
                        text = stringResource(R.string.recommendation_header_course_subtitle, region?.name.orEmpty()),
                        color = Ink2,
                        fontSize = 12.5.sp,
                        lineHeight = 19.sp,
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        RecommendationSampleData.courses.fastForEachIndexed { index, course ->
                            RCard(
                                title = course.name,
                                reason = course.reason,
                                isBest = course.best,
                                onClick = { onCourseSelected(index) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CourseScreenPreview() {
    CourseScreen(regionIndex = 0, onBack = {}, onCourseSelected = {})
}
