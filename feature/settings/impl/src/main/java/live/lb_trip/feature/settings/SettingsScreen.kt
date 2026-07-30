package live.lb_trip.feature.settings

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.feature.settings.components.SettingsBottomTabs
import live.lb_trip.feature.settings.components.SettingsLogoutGroup
import live.lb_trip.feature.settings.components.SettingsMenuGroup
import live.lb_trip.feature.settings.components.SettingsProfileHeader
import live.lb_trip.feature.settings.components.SettingsSavedCoursesRow

private val Brand = LbColors.Green

@Composable
internal fun SettingsScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToSavedCourses: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val snackbarHostState = remember { SnackbarHostState() }
    val loadErrorMessage = stringResource(R.string.settings_error_load)
    val retryActionLabel = stringResource(R.string.settings_action_retry)
    val unavailableTemplate = stringResource(R.string.settings_menu_unavailable_template)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                SettingsSideEffect.ShowLoadError -> {
                    val result = snackbarHostState.showSnackbar(message = loadErrorMessage, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(SettingsIntent.Retry)
                    }
                }

                is SettingsSideEffect.ShowUnavailableMessage -> {
                    snackbarHostState.showSnackbar(message = String.format(unavailableTemplate, effect.label))
                }
            }
        }
    }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    SettingsScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateToMain = onNavigateToMain,
        onNavigateToSavedCourses = onNavigateToSavedCourses,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
private fun SettingsScreenContent(
    state: SettingsUiState,
    snackbarHostState: SnackbarHostState,
    onNavigateToMain: () -> Unit,
    onNavigateToSavedCourses: () -> Unit,
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val versionName = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
            .getOrNull()
            .orEmpty()
    }
    val editInfoLabel = stringResource(R.string.settings_edit_info)
    val retakeDiagnosisLabel = stringResource(R.string.settings_menu_retake_diagnosis)
    val licensesLabel = stringResource(R.string.settings_menu_licenses)
    val termsLabel = stringResource(R.string.settings_menu_terms)
    val privacyLabel = stringResource(R.string.settings_menu_privacy)
    val contactLabel = stringResource(R.string.settings_menu_contact)

    Scaffold(
        modifier = modifier,
        topBar = { SettingsBrandBar() },
        bottomBar = { SettingsBottomTabs(onMainClick = onNavigateToMain) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = LbColors.ScreenBg,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Brand)
                }
            } else {
                SettingsProfileHeader(
                    name = state.name,
                    email = state.email,
                    onEditInfoClick = { onIntent(SettingsIntent.MenuItemClick(editInfoLabel)) },
                    modifier = Modifier.background(Color.White),
                )
                HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)

                SettingsSavedCoursesRow(
                    count = state.savedCoursesCount,
                    onClick = onNavigateToSavedCourses,
                    modifier = Modifier.padding(top = 16.dp),
                )

                SettingsMenuGroup(
                    label = stringResource(R.string.settings_group_label),
                    items = listOf(
                        editInfoLabel to { onIntent(SettingsIntent.MenuItemClick(editInfoLabel)) },
                        retakeDiagnosisLabel to { onIntent(SettingsIntent.MenuItemClick(retakeDiagnosisLabel)) },
                        licensesLabel to { onIntent(SettingsIntent.MenuItemClick(licensesLabel)) },
                        termsLabel to { onIntent(SettingsIntent.MenuItemClick(termsLabel)) },
                        privacyLabel to { onIntent(SettingsIntent.MenuItemClick(privacyLabel)) },
                        contactLabel to { onIntent(SettingsIntent.MenuItemClick(contactLabel)) },
                    ),
                    versionName = versionName,
                    modifier = Modifier.padding(top = 22.dp),
                )

                SettingsLogoutGroup(
                    onLogoutClick = { onIntent(SettingsIntent.LogoutClick) },
                    modifier = Modifier.padding(top = 14.dp),
                )

                Text(
                    text = stringResource(R.string.settings_footer),
                    color = LbColors.Ink4,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 22.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsBrandBar(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_balance_mark),
                        contentDescription = null,
                        tint = Brand,
                        modifier = Modifier.size(26.dp),
                    )
                    val brandPrefix = stringResource(R.string.settings_brand_prefix)
                    val brandHighlight = stringResource(R.string.settings_brand_highlight)
                    val brandSuffix = stringResource(R.string.settings_brand_suffix)
                    Text(
                        text = buildAnnotatedString {
                            append(brandPrefix)
                            withStyle(SpanStyle(color = Brand)) { append(brandHighlight) }
                            append(brandSuffix)
                        },
                        color = LbColors.Ink,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        )
        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreenContent(
        state = SettingsUiState(isLoading = false, name = "홍길동", email = "local@email.com", savedCoursesCount = 5),
        snackbarHostState = remember { SnackbarHostState() },
        onNavigateToMain = {},
        onNavigateToSavedCourses = {},
        onIntent = {},
    )
}
