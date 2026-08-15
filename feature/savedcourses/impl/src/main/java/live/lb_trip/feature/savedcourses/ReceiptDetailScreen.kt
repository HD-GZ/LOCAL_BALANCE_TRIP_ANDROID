package live.lb_trip.feature.savedcourses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.core.util.getBitmapFromUrl
import live.lb_trip.core.util.save

@Composable
internal fun ReceiptDetailScreen(
    onBack: () -> Unit,
    onUpdated: () -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReceiptDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory()) }
            .build()
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val loadErrorMessage = stringResource(R.string.savedcourses_receipt_detail_error_load)
    val updateErrorMessage = stringResource(R.string.savedcourses_receipt_detail_error_update)
    val invalidInputMessage = stringResource(R.string.savedcourses_receipt_detail_error_invalid)
    val updatedMessage = stringResource(R.string.savedcourses_receipt_detail_updated)
    val deleteErrorMessage = stringResource(R.string.savedcourses_receipt_detail_error_delete)
    val downloadSavedMessage = stringResource(R.string.savedcourses_receipt_detail_download_saved)
    val downloadFailedMessage = stringResource(R.string.savedcourses_receipt_detail_download_failed)

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                ReceiptDetailSideEffect.ShowLoadError -> launch { snackbarHostState.showSnackbar(loadErrorMessage) }
                ReceiptDetailSideEffect.ShowUpdateError -> launch { snackbarHostState.showSnackbar(updateErrorMessage) }
                ReceiptDetailSideEffect.ShowInvalidInput -> launch { snackbarHostState.showSnackbar(invalidInputMessage) }
                ReceiptDetailSideEffect.ShowUpdated -> {
                    onUpdated()
                    launch { snackbarHostState.showSnackbar(updatedMessage) }
                }
                ReceiptDetailSideEffect.ShowDeleteError -> launch { snackbarHostState.showSnackbar(deleteErrorMessage) }
                is ReceiptDetailSideEffect.DownloadUrlReady -> launch {
                    viewModel.setLoading(true)
                    coroutineScope.launch {
                        val uri = effect.url.getBitmapFromUrl(context, imageLoader)?.save(context)
                        viewModel.setLoading(false)
                        snackbarHostState.showSnackbar(if (uri != null) downloadSavedMessage else downloadFailedMessage)
                    }
                }
                ReceiptDetailSideEffect.ShowDownloadError -> launch { snackbarHostState.showSnackbar(downloadFailedMessage) }
                ReceiptDetailSideEffect.NavigateBackWithDeleted -> onDeleted()
            }
        }
    }

    ReceiptDetailScreenContent(
        state = state,
        imageLoader = imageLoader,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onBack = onBack,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

private fun receiptImageFileName(): String = "lbt_receipt_${System.currentTimeMillis()}.png"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ReceiptDetailScreenContent(
    state: ReceiptDetailUiState,
    imageLoader: ImageLoader,
    snackbarHost: @Composable () -> Unit,
    onBack: () -> Unit,
    onIntent: (ReceiptDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val directive = calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(currentWindowAdaptiveInfo())
    val isTwoPane = directive.maxHorizontalPartitions > 1

    Scaffold(
        modifier = modifier,
        topBar = {
            LbTopBar(
                onBackClick = onBack,
                backContentDescription = stringResource(R.string.savedcourses_receipt_back_content_description),
                title = stringResource(R.string.savedcourses_receipt_detail_title),
                actions = {
                    if (!state.isLoading && !state.isEditing) {
                        IconButton(onClick = { onIntent(ReceiptDetailIntent.EditClicked) }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.savedcourses_receipt_detail_edit_content_description),
                                tint = LbColors.Ink2,
                                modifier = Modifier.size(19.dp),
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = snackbarHost,
        containerColor = LbColors.Paper,
    ) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.zIndex(2f).padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LbColors.Green)
            }
        }

        if (isTwoPane) {
            ReceiptDetailTwoPaneBody(
                state = state,
                imageLoader = imageLoader,
                directive = directive,
                onIntent = onIntent,
                modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
            )
        } else {
            ReceiptDetailCompactBody(
                state = state,
                imageLoader = imageLoader,
                onIntent = onIntent,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    if (state.showDeleteConfirm) {
        ReceiptDeleteConfirmSheet(
            onDismiss = { onIntent(ReceiptDetailIntent.DeleteDismissed) },
            onConfirm = { onIntent(ReceiptDetailIntent.DeleteConfirmed) },
        )
    }
}

@Composable
private fun ReceiptDetailCompactBody(
    state: ReceiptDetailUiState,
    imageLoader: ImageLoader,
    onIntent: (ReceiptDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        ReceiptStatusBadge()
        ReceiptImage(
            imageUrl = state.imageUrl,
            imageLoader = imageLoader,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 260.dp, max = 420.dp),
        )
        ReceiptInfoContent(state = state, onIntent = onIntent)
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ReceiptDetailTwoPaneBody(
    state: ReceiptDetailUiState,
    imageLoader: ImageLoader,
    directive: PaneScaffoldDirective,
    onIntent: (ReceiptDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigator = rememberSupportingPaneScaffoldNavigator(scaffoldDirective = directive)

    NavigableSupportingPaneScaffold(
        navigator = navigator,
        modifier = modifier,
        mainPane = {
            AnimatedPane {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    ReceiptStatusBadge()
                    ReceiptImage(
                        imageUrl = state.imageUrl,
                        imageLoader = imageLoader,
                        modifier = Modifier.fillMaxWidth().weight(1f),
                    )
                }
            }
        },
        supportingPane = {
            AnimatedPane {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    ReceiptInfoContent(state = state, onIntent = onIntent)
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReceiptDeleteConfirmSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.savedcourses_receipt_detail_delete_confirm_title),
                color = LbColors.Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = stringResource(R.string.savedcourses_receipt_detail_delete_confirm_body),
                color = LbColors.Ink2,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(bottom = 20.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                LbButton(
                    onClick = onConfirm,
                    colors = receiptDetailDangerColors(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.savedcourses_receipt_detail_delete_confirm_ok),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                LbButton(
                    onClick = onDismiss,
                    colors = LbButtonDefaults.whiteColors(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.savedcourses_receipt_detail_delete_confirm_cancel),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptDetailScreenPreview() {
    ReceiptDetailScreenContent(
        state = ReceiptDetailUiState(
            isLoading = false,
            merchantName = "담양앞집",
            amount = "18000",
            paidDate = "2026-07-17",
            imageUrl = null,
        ),
        imageLoader = ImageLoader.Builder(LocalContext.current).build(),
        snackbarHost = {},
        onBack = {},
        onIntent = {},
    )
}
