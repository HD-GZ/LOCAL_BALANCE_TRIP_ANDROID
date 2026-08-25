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
import androidx.compose.ui.util.fastForEach
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import kotlinx.collections.immutable.persistentListOf
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.domain.model.RecommendedRegion
import live.lb_trip.feature.recommendation.components.Ink
import live.lb_trip.feature.recommendation.components.Ink2
import live.lb_trip.feature.recommendation.components.Paper
import live.lb_trip.feature.recommendation.components.RCard
import live.lb_trip.feature.recommendation.components.RecommendationBrandBar
import live.lb_trip.feature.recommendation.components.RecommendationFlowStepper
import live.lb_trip.feature.recommendation.components.ScreenBg

@Composable
internal fun RegionScreen(
    onBack: () -> Unit,
    onRegionSelected: (regionId: Long, regionName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegionViewModel = hiltViewModel(),
    onIntent: (RegionIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val retryActionLabel = stringResource(R.string.recommendation_action_retry)
    val propensityNotFoundMessage = stringResource(R.string.recommendation_error_propensity_not_found)
    val tourApiUnavailableMessage = stringResource(R.string.recommendation_error_tour_api_unavailable)
    val emptyRegionsMessage = stringResource(R.string.recommendation_error_empty_regions)
    val genericErrorMessage = stringResource(R.string.recommendation_error_generic_region)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is RegionSideEffect.ShowError -> {
                    val message = when (effect.reason) {
                        RegionLoadErrorReason.PropensityNotFound -> propensityNotFoundMessage
                        RegionLoadErrorReason.TourApiUnavailable -> tourApiUnavailableMessage
                        RegionLoadErrorReason.Empty -> emptyRegionsMessage
                        RegionLoadErrorReason.Unknown -> genericErrorMessage
                    }
                    val result = snackbarHostState.showSnackbar(message = message, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) onIntent(RegionIntent.Retry)
                }
            }
        }
    }

    RegionScreenContent(
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
        onRegionSelected = onRegionSelected,
        modifier = modifier,
    )
}

@Composable
private fun RegionScreenContent(
    state: RegionUiState,
    snackbarHost: @Composable BoxScope.() -> Unit,
    onBack: () -> Unit,
    onRegionSelected: (regionId: Long, regionName: String) -> Unit,
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
                title = stringResource(R.string.recommendation_title_region),
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
                    currentStep = 1,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(18.dp))

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(
                        text = stringResource(R.string.recommendation_header_region_title),
                        color = Ink,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(
                        text = stringResource(R.string.recommendation_header_region_subtitle),
                        color = Ink2,
                        fontSize = 12.5.sp,
                        lineHeight = 19.sp,
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.regions.fastForEach { region ->
                            RCard(
                                title = region.name,
                                reason = region.reason,
                                imageUrl = region.imageUrl,
                                imageLoader = imageLoader,
                                onClick = { onRegionSelected(region.id, region.name) },
                            )
                        }
                    }
                }
            }
        }

        snackbarHost()

        if (state.isLoading) {
            LbLoadingOverlay(label = stringResource(R.string.recommendation_loading_label))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegionScreenPreview() {
    RegionScreenContent(
        state = RegionUiState(
            isLoading = false,
            regions = persistentListOf(
                RecommendedRegion(id = 1, name = "전라남도 담양군", reason = "로컬 미식 상권이 풍부해요.", imageUrl = null),
            ),
        ),
        snackbarHost = {},
        onBack = {},
        onRegionSelected = { _, _ -> },
    )
}
