package live.lb_trip.feature.recommendation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import kotlinx.collections.immutable.persistentListOf
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.domain.model.RecommendedCourse
import live.lb_trip.feature.recommendation.components.Ink
import live.lb_trip.feature.recommendation.components.Ink2
import live.lb_trip.feature.recommendation.components.Paper
import live.lb_trip.feature.recommendation.components.RCard
import live.lb_trip.feature.recommendation.components.RecommendationBrandBar
import live.lb_trip.feature.recommendation.components.RecommendationFlowStepper
import live.lb_trip.feature.recommendation.components.ScreenBg

@Composable
internal fun CourseScreen(
    onBack: () -> Unit,
    onCourseSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CourseViewModel = hiltViewModel(),
    onIntent: (CourseIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val retryActionLabel = stringResource(R.string.recommendation_action_retry)
    val regionNotFoundMessage = stringResource(R.string.recommendation_error_region_not_found)
    val emptyCoursesMessage = stringResource(R.string.recommendation_error_empty_courses)
    val genericErrorMessage = stringResource(R.string.recommendation_error_generic_course)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CourseSideEffect.ShowError -> {
                    val message = when (effect.reason) {
                        CourseLoadErrorReason.RegionNotFound -> regionNotFoundMessage
                        CourseLoadErrorReason.Empty -> emptyCoursesMessage
                        CourseLoadErrorReason.Unknown -> genericErrorMessage
                    }
                    val result = snackbarHostState.showSnackbar(message = message, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) onIntent(CourseIntent.Retry)
                }
            }
        }
    }

    CourseScreenContent(
        regionName = viewModel.regionName,
        state = state,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars),
            )
        },
        onBack = onBack,
        onCourseSelected = onCourseSelected,
        modifier = modifier,
    )
}

@Composable
private fun CourseScreenContent(
    regionName: String,
    state: CourseUiState,
    snackbarHost: @Composable BoxScope.() -> Unit,
    onBack: () -> Unit,
    onCourseSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val activity = LocalActivity.current
    if (activity != null) {
        SideEffect {
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
        }
    }

    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components { add(OkHttpNetworkFetcherFactory()) }.build()
    }

    Box(modifier = modifier.fillMaxSize().background(Paper)) {
        Column(modifier = Modifier.fillMaxSize()) {
            RecommendationBrandBar(
                title = stringResource(R.string.recommendation_title_course, regionName),
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
                        text = stringResource(R.string.recommendation_title_course, regionName),
                        color = Ink,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(
                        text = stringResource(R.string.recommendation_header_course_subtitle, regionName),
                        color = Ink2,
                        fontSize = 12.5.sp,
                        lineHeight = 19.sp,
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.courses.fastForEachIndexed { index, course ->
                            RCard(
                                title = course.title,
                                reason = course.reason,
                                isBest = index == 0,
                                imageUrl = course.imageUrl,
                                imageLoader = imageLoader,
                                onClick = { onCourseSelected(course.id) },
                            )
                        }
                    }
                }
            }
        }

        snackbarHost()

        if (state.isLoading) {
            LbLoadingOverlay()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CourseScreenPreview() {
    CourseScreenContent(
        regionName = "전라남도 담양군",
        state = CourseUiState(
            isLoading = false,
            courses = persistentListOf(
                RecommendedCourse(
                    id = 1,
                    title = "남도 골목 미식 슬로우 트립",
                    reason = "실속 소비 + 로컬 미식 성향을 반영했어요.",
                    imageUrl = null,
                ),
            ),
        ),
        snackbarHost = {},
        onBack = {},
        onCourseSelected = {},
    )
}
