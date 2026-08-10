package live.lb_trip.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.feature.home.components.IncentiveCard

@Composable
internal fun PolicyListScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PolicyListViewModel = hiltViewModel(),
    onIntent: (PolicyListIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current
    val loadErrorMessage = stringResource(R.string.home_error_incentives_load)
    val retryActionLabel = stringResource(R.string.home_action_retry)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is PolicyListSideEffect.OpenUrl -> uriHandler.openUri(effect.url)
                PolicyListSideEffect.ShowLoadError -> {
                    val result = snackbarHostState.showSnackbar(message = loadErrorMessage, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        onIntent(PolicyListIntent.Retry)
                    }
                }
            }
        }
    }

    PolicyListScreenContent(
        state = state,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onBack = onBack,
        onCardClick = { onIntent(PolicyListIntent.CardClicked(it)) },
        modifier = modifier,
    )
}

@Composable
private fun PolicyListScreenContent(
    state: PolicyListUiState,
    snackbarHost: @Composable () -> Unit,
    onBack: () -> Unit,
    onCardClick: (HomeIncentiveCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LbTopBar(
                onBackClick = onBack,
                backContentDescription = stringResource(R.string.home_policy_list_back_content_description),
                title = stringResource(R.string.home_policy_list_title),
            )
        },
        snackbarHost = snackbarHost,
        containerColor = LbColors.ScreenBg,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when {
                state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LbColors.Green)
                }

                state.cards.isEmpty() -> Text(
                    text = stringResource(R.string.home_policy_list_empty),
                    color = LbColors.Ink3,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
                )

                else -> PolicyListBody(cards = state.cards, onCardClick = onCardClick)
            }
        }
    }
}

@Composable
private fun PolicyListBody(
    cards: ImmutableList<HomeIncentiveCard>,
    onCardClick: (HomeIncentiveCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        items(cards, key = { it.regionName + it.title + it.url }) { card ->
            IncentiveCard(card = card, onClick = { onCardClick(card) })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PolicyListScreenPreview() {
    PolicyListScreenContent(
        state = PolicyListUiState(isLoading = false, cards = persistentListOf()),
        snackbarHost = {},
        onBack = {},
        onCardClick = {},
    )
}
