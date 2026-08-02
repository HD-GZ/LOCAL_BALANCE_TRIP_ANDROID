package live.lb_trip.feature.recommendation

import androidx.activity.compose.LocalActivity
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
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
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
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.feature.recommendation.components.BenefitRow
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
    val uriHandler = LocalUriHandler.current

    val snackbarHostState = remember { SnackbarHostState() }
    val saveConfirmationMessage = stringResource(R.string.recommendation_snackbar_save_confirmation)
    val saveErrorMessage = stringResource(R.string.recommendation_snackbar_save_error)
    val retryActionLabel = stringResource(R.string.recommendation_action_retry)
    val courseNotFoundMessage = stringResource(R.string.recommendation_error_course_not_found)
    val emptyPlacesMessage = stringResource(R.string.recommendation_error_empty_places)
    val genericLoadErrorMessage = stringResource(R.string.recommendation_error_generic_detail)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is RecommendationDetailSideEffect.ShowLoadError -> {
                    val message = when (effect.reason) {
                        DetailLoadErrorReason.CourseNotFound -> courseNotFoundMessage
                        DetailLoadErrorReason.EmptyPlaces -> emptyPlacesMessage
                        DetailLoadErrorReason.Unknown -> genericLoadErrorMessage
                    }
                    val result = snackbarHostState.showSnackbar(message = message, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(RecommendationDetailIntent.Retry)
                    }
                }
                RecommendationDetailSideEffect.ShowSaveConfirmation -> snackbarHostState.showSnackbar(saveConfirmationMessage)
                RecommendationDetailSideEffect.ShowSaveError -> snackbarHostState.showSnackbar(saveErrorMessage)
                is RecommendationDetailSideEffect.OpenBenefitUrl -> uriHandler.openUri(effect.url)
            }
        }
    }

    DetailScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
private fun DetailScreenContent(
    state: RecommendationDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onIntent: (RecommendationDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val activity = LocalActivity.current
    if (activity != null) {
        SideEffect {
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
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
                    Text(text = state.title, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                    stops = state.stops,
                    expandedIndices = state.expandedStopIndices,
                    playingStopIndex = state.playingStopIndex,
                    onToggle = { onIntent(RecommendationDetailIntent.StopToggled(it)) },
                    onTogglePlayback = { onIntent(RecommendationDetailIntent.PlaybackToggled(it)) },
                )

                if (state.benefits.isNotEmpty()) {
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
                            state.benefits.fastForEachIndexed { index, benefit ->
                                if (index > 0) HorizontalDivider(color = LineSoft, thickness = 1.dp)
                                BenefitRow(
                                    benefit = benefit,
                                    onClick = { onIntent(RecommendationDetailIntent.BenefitClicked(benefit.url)) },
                                )
                            }
                        }
                    }
                }
            }

            if (state.stops.isNotEmpty()) {
                RecommendationCtaBar(
                    isSaved = state.isSaved,
                    onSaveClick = { onIntent(RecommendationDetailIntent.SaveClicked) },
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars),
        )

        if (state.isLoading) {
            LbLoadingOverlay()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    DetailScreenContent(
        state = RecommendationDetailUiState(
            isLoading = false,
            title = "전라북도 임실군 골목 미식 코스",
            expandedStopIndices = persistentSetOf(0, 2),
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onBack = {},
        onIntent = {},
    )
}
