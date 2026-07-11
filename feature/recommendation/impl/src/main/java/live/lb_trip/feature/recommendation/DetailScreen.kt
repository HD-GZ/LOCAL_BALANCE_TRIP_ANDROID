package live.lb_trip.feature.recommendation

import android.app.Activity
import androidx.compose.foundation.background
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.collections.immutable.persistentSetOf
import live.lb_trip.feature.recommendation.components.IncentiveRow
import live.lb_trip.feature.recommendation.components.Ink
import live.lb_trip.feature.recommendation.components.LineSoft
import live.lb_trip.feature.recommendation.components.Paper
import live.lb_trip.feature.recommendation.components.RecommendationBrandBar
import live.lb_trip.feature.recommendation.components.RecommendationCtaBar
import live.lb_trip.feature.recommendation.components.RecommendationFlowStepper
import live.lb_trip.feature.recommendation.components.ScreenBg
import live.lb_trip.feature.recommendation.components.Timeline

@Composable
internal fun DetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecommendationDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val course = RecommendationSampleData.courses.getOrNull(viewModel.courseIndex)

    val snackbarHostState = remember { SnackbarHostState() }
    val saveConfirmationMessage = stringResource(R.string.recommendation_snackbar_save_confirmation)
    val tourStubMessage = stringResource(R.string.recommendation_snackbar_tour_stub)
    val incentiveStubMessage = stringResource(R.string.recommendation_snackbar_incentive_stub)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                RecommendationDetailSideEffect.ShowSaveConfirmation -> snackbarHostState.showSnackbar(saveConfirmationMessage)
                RecommendationDetailSideEffect.ShowTourStub -> snackbarHostState.showSnackbar(tourStubMessage)
                RecommendationDetailSideEffect.ShowIncentiveStub -> snackbarHostState.showSnackbar(incentiveStubMessage)
            }
        }
    }

    DetailScreenContent(
        course = course,
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onStopClick = viewModel::toggleStopExpanded,
        onTogglePlayback = viewModel::toggleAudioPlayback,
        onSaveClick = viewModel::saveCourse,
        onTourStartClick = viewModel::onTourStartClicked,
        onIncentiveClick = viewModel::onIncentiveClicked,
        modifier = modifier,
    )
}

@Composable
private fun DetailScreenContent(
    course: RecommendedCourse?,
    state: RecommendationDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onStopClick: (Int) -> Unit,
    onTogglePlayback: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onTourStartClick: () -> Unit,
    onIncentiveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Paper)) {
        Column(modifier = Modifier.fillMaxSize()) {
            RecommendationBrandBar(
                title = stringResource(R.string.recommendation_title_detail),
                onBackClick = onBack,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(ScreenBg)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
            ) {
                RecommendationFlowStepper(
                    currentStep = 3,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(18.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(text = course?.name.orEmpty(), color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = stringResource(R.string.recommendation_section_course_order),
                        color = Ink,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

                Timeline(
                    stops = RecommendationSampleData.stops,
                    expandedIndices = state.expandedStopIndices,
                    playingStopIndex = state.playingStopIndex,
                    onToggle = onStopClick,
                    onTogglePlayback = onTogglePlayback,
                )

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HorizontalDivider(color = LineSoft, thickness = 1.dp, modifier = Modifier.padding(vertical = 18.dp))

                    Text(
                        text = stringResource(R.string.recommendation_section_incentives),
                        color = Ink,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Column {
                        RecommendationSampleData.incentives.fastForEachIndexed { index, incentive ->
                            if (index > 0) HorizontalDivider(color = LineSoft, thickness = 1.dp)
                            IncentiveRow(incentive = incentive, onClick = onIncentiveClick)
                        }
                    }
                }
            }

            RecommendationCtaBar(
                isSaved = state.isSaved,
                onSaveClick = onSaveClick,
                onTourStartClick = onTourStartClick,
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    DetailScreenContent(
        course = RecommendationSampleData.courses.firstOrNull(),
        state = RecommendationDetailUiState(expandedStopIndices = persistentSetOf(0, 2)),
        snackbarHostState = remember { SnackbarHostState() },
        onBack = {},
        onStopClick = {},
        onTogglePlayback = {},
        onSaveClick = {},
        onTourStartClick = {},
        onIncentiveClick = {},
    )
}
