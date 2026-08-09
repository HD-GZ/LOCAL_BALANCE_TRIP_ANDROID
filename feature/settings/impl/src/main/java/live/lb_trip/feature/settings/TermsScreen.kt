package live.lb_trip.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.domain.model.TermsType

@Composable
internal fun TermsScreen(
    type: TermsType,
    fallbackTitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TermsViewModel = hiltViewModel(),
    onIntent: (TermsIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val loadErrorMessage = stringResource(R.string.terms_error_load)
    val retryActionLabel = stringResource(R.string.terms_action_retry)

    LaunchedEffect(type) {
        onIntent(TermsIntent.Load(type))
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                TermsSideEffect.ShowLoadError -> {
                    val result = snackbarHostState.showSnackbar(message = loadErrorMessage, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        onIntent(TermsIntent.Retry)
                    }
                }
            }
        }
    }

    TermsScreenContent(
        state = state,
        fallbackTitle = fallbackTitle,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun TermsScreenContent(
    state: TermsUiState,
    fallbackTitle: String,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LbTopBar(
                onBackClick = onBack,
                backContentDescription = stringResource(R.string.terms_back_cd),
                title = state.title.ifEmpty { fallbackTitle },
                containerColor = LbColors.Paper,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = LbColors.Paper,
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = LbColors.Green)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            ) {
                Text(
                    text = stringResource(R.string.terms_revision_template, state.effectiveDate, state.version),
                    color = LbColors.Ink3,
                    fontSize = 10.5.sp,
                    modifier = Modifier.padding(bottom = 18.dp),
                )
                state.blocks.forEachIndexed { index, block ->
                    TermsBlockContent(block = block, isFirst = index == 0)
                }
            }
        }
    }
}

@Composable
private fun TermsBlockContent(block: TermsBlock, isFirst: Boolean, modifier: Modifier = Modifier) {
    when (block) {
        is TermsBlock.Heading -> Text(
            text = block.text,
            color = LbColors.Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(top = if (isFirst) 0.dp else 20.dp, bottom = 8.dp),
        )
        is TermsBlock.Paragraph -> Text(
            text = block.text,
            color = LbColors.Ink2,
            fontSize = 12.5.sp,
            lineHeight = 21.sp,
            modifier = modifier.padding(bottom = 10.dp),
        )
        is TermsBlock.BulletList -> Column(modifier = modifier.padding(bottom = 10.dp)) {
            block.items.forEach { item -> TermsBulletItem(text = item) }
        }
    }
}

@Composable
private fun TermsBulletItem(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(bottom = 6.dp)) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(4.dp)
                .clip(CircleShape)
                .background(LbColors.Green),
        )
        Text(
            text = text,
            color = LbColors.Ink2,
            fontSize = 12.5.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
