package live.lb_trip.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.feature.settings.components.EditProfileForm
import live.lb_trip.feature.settings.components.EditProfileSubmitBar
import live.lb_trip.feature.settings.components.EditProfileWithdrawDialog

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val loadErrorMessage = stringResource(R.string.edit_profile_error_load)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                EditProfileSideEffect.ShowLoadError -> launch { snackbarHostState.showSnackbar(loadErrorMessage) }
                is EditProfileSideEffect.ShowSaveError -> launch { snackbarHostState.showSnackbar(effect.message) }
                EditProfileSideEffect.SaveSuccess -> onSaved()
                is EditProfileSideEffect.ShowWithdrawError -> launch { snackbarHostState.showSnackbar(effect.message) }
            }
        }
    }

    EditProfileScreenContent(
        state = state,
        onBack = onBack,
        onIntent = viewModel::onIntent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
        showBackButton = showBackButton,
    )
}

@Composable
private fun EditProfileScreenContent(
    state: EditProfileUiState,
    onBack: () -> Unit,
    onIntent: (EditProfileIntent) -> Unit,
    snackbarHost: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
) {
    var showWithdrawDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                LbTopBar(
                    onBackClick = onBack,
                    backContentDescription = stringResource(R.string.edit_profile_back_cd),
                    title = stringResource(R.string.edit_profile_title),
                    containerColor = LbColors.Paper,
                    showBackButton = showBackButton,
                )
            },
            bottomBar = {
                if (!state.isLoading) {
                    EditProfileSubmitBar(state = state, onIntent = onIntent)
                }
            },
            snackbarHost = snackbarHost,
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
                EditProfileForm(
                    state = state,
                    onIntent = onIntent,
                    onWithdrawLinkClick = { showWithdrawDialog = true },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }

        if (state.isWithdrawing) {
            LbLoadingOverlay()
        }
    }

    if (showWithdrawDialog) {
        EditProfileWithdrawDialog(
            onConfirm = {
                showWithdrawDialog = false
                onIntent(EditProfileIntent.WithdrawClicked)
            },
            onDismiss = { showWithdrawDialog = false },
        )
    }
}
