package live.lb_trip.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.core.designsystem.component.LbBrush
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbInputField
import live.lb_trip.domain.model.Gender

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val loadErrorMessage = stringResource(R.string.edit_profile_error_load)
    val withdrawUnavailableMessage = stringResource(R.string.edit_profile_withdraw_unavailable)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                EditProfileSideEffect.ShowLoadError -> launch { snackbarHostState.showSnackbar(loadErrorMessage) }
                is EditProfileSideEffect.ShowSaveError -> launch { snackbarHostState.showSnackbar(effect.message) }
                EditProfileSideEffect.SaveSuccess -> onSaved()
                EditProfileSideEffect.ShowWithdrawUnavailable -> launch { snackbarHostState.showSnackbar(withdrawUnavailableMessage) }
            }
        }
    }

    EditProfileScreenContent(
        state = state,
        onBack = onBack,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun EditProfileScreenContent(
    state: EditProfileUiState,
    onBack: () -> Unit,
    onIntent: (EditProfileIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    var showWithdrawSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            LbTopBar(
                onBackClick = onBack,
                backContentDescription = stringResource(R.string.edit_profile_back_cd),
                title = stringResource(R.string.edit_profile_title),
                containerColor = LbColors.Paper,
            )
        },
        bottomBar = {
            if (!state.isLoading) {
                EditProfileSubmitBar(state = state, onIntent = onIntent)
            }
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
            EditProfileForm(
                state = state,
                onIntent = onIntent,
                onWithdrawLinkClick = { showWithdrawSheet = true },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    if (showWithdrawSheet) {
        WithdrawSheet(
            onWithdrawClick = {
                showWithdrawSheet = false
                onIntent(EditProfileIntent.WithdrawClicked)
            },
            onDismiss = { showWithdrawSheet = false },
        )
    }
}

@Composable
private fun EditProfileForm(
    state: EditProfileUiState,
    onIntent: (EditProfileIntent) -> Unit,
    onWithdrawLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(
                text = stringResource(R.string.edit_profile_email_label),
                color = LbColors.Ink2,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(LbColors.SurfaceSoft)
                    .padding(horizontal = 15.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(text = state.email, color = LbColors.Ink3, fontSize = 15.5.sp)
            }
            Text(
                text = stringResource(R.string.edit_profile_email_hint),
                color = LbColors.Ink3,
                fontSize = 12.sp,
            )
        }

        LbInputField(
            required = true,
            value = state.name,
            onValueChange = { onIntent(EditProfileIntent.NameChanged(it)) },
            label = stringResource(R.string.edit_profile_name_label),
            placeholder = stringResource(R.string.edit_profile_name_placeholder),
        )

        LbInputField(
            value = state.password,
            onValueChange = { onIntent(EditProfileIntent.PasswordChanged(it)) },
            label = stringResource(R.string.edit_profile_password_label),
            placeholder = stringResource(R.string.edit_profile_password_placeholder),
            hintText = stringResource(R.string.edit_profile_password_hint),
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                PasswordVisibilityToggle(
                    onClick = { onIntent(EditProfileIntent.TogglePasswordVisibility) },
                )
            },
        )

        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            LbInputField(
                value = state.passwordConfirm,
                onValueChange = { onIntent(EditProfileIntent.PasswordConfirmChanged(it)) },
                label = stringResource(R.string.edit_profile_password_confirm_label),
                placeholder = stringResource(R.string.edit_profile_password_confirm_placeholder),
                visualTransformation = if (state.isConfirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    PasswordVisibilityToggle(
                        onClick = { onIntent(EditProfileIntent.ToggleConfirmPasswordVisibility) },
                    )
                },
            )
            if (state.password.isNotEmpty() && state.password != state.passwordConfirm) {
                Text(
                    text = stringResource(R.string.edit_profile_password_mismatch),
                    color = LbColors.RequiredMark,
                    fontSize = 12.sp,
                )
            }
        }

        BirthField(
            year = state.birthYear,
            month = state.birthMonth,
            day = state.birthDay,
            onYearChange = { onIntent(EditProfileIntent.BirthYearChanged(it)) },
            onMonthChange = { onIntent(EditProfileIntent.BirthMonthChanged(it)) },
            onDayChange = { onIntent(EditProfileIntent.BirthDayChanged(it)) },
        )

        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(
                text = stringResource(R.string.edit_profile_gender_label),
                color = LbColors.Ink2,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            GenderSegmented(
                selected = state.gender,
                onSelect = { onIntent(EditProfileIntent.GenderChanged(it)) },
            )
        }

        Text(
            text = stringResource(R.string.edit_profile_withdraw_link),
            color = LbColors.DangerStrong,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(onClick = onWithdrawLinkClick),
        )
    }
}

@Composable
private fun PasswordVisibilityToggle(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier.size(38.dp)) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_eye),
            contentDescription = stringResource(R.string.edit_profile_password_visibility_cd),
            tint = Color.Unspecified,
        )
    }
}

private fun isEditProfileFormValid(state: EditProfileUiState): Boolean =
    state.name.isNotEmpty() &&
        state.birthYear.length == 4 &&
        state.birthMonth in 1..12 &&
        (state.birthDay.toIntOrNull() ?: 0) in 1..31 &&
        (state.password.isEmpty() || state.password == state.passwordConfirm)

@Composable
private fun EditProfileSubmitBar(
    state: EditProfileUiState,
    onIntent: (EditProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LbBrush.BottomFadeGradient)
            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
            .padding(horizontal = 24.dp, vertical = 14.dp),
    ) {
        LbButton(
            onClick = { onIntent(EditProfileIntent.SaveClicked) },
            enabled = isEditProfileFormValid(state) && !state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = LbButtonDefaults.greenColors(),
        ) {
            Text(
                text = stringResource(R.string.edit_profile_submit),
                fontSize = 15.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun BirthField(
    year: String,
    month: Int,
    day: String,
    onYearChange: (String) -> Unit,
    onMonthChange: (Int) -> Unit,
    onDayChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var monthMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Text(
            text = stringResource(R.string.edit_profile_birth_date_label),
            color = LbColors.Ink2,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BasicTextField(
                value = year,
                onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) onYearChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(LbColors.Green),
                textStyle = TextStyle(color = LbColors.Ink, fontSize = 15.5.sp),
                modifier = Modifier.weight(2.2f),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 15.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (year.isEmpty()) {
                            Text(text = "YYYY", color = LbColors.Ink4, fontSize = 15.5.sp)
                        }
                        innerTextField()
                    }
                },
            )

            Box(modifier = Modifier.weight(1.5f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { monthMenuExpanded = true }
                        .padding(horizontal = 15.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = if (month == 0) "월" else "${month}월",
                        color = if (month == 0) LbColors.Ink4 else LbColors.Ink,
                        fontSize = 15.5.sp,
                    )
                }
                DropdownMenu(
                    expanded = monthMenuExpanded,
                    onDismissRequest = { monthMenuExpanded = false },
                ) {
                    for (m in 1..12) {
                        DropdownMenuItem(
                            text = { Text("${m}월") },
                            onClick = {
                                onMonthChange(m)
                                monthMenuExpanded = false
                            },
                        )
                    }
                }
            }

            BasicTextField(
                value = day,
                onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) onDayChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(LbColors.Green),
                textStyle = TextStyle(color = LbColors.Ink, fontSize = 15.5.sp),
                modifier = Modifier.weight(1.2f),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 15.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (day.isEmpty()) {
                            Text(text = "일", color = LbColors.Ink4, fontSize = 15.5.sp)
                        }
                        innerTextField()
                    }
                },
            )
        }
    }
}

@Composable
private fun GenderSegmented(selected: Gender, onSelect: (Gender) -> Unit, modifier: Modifier = Modifier) {
    val options = listOf(
        Gender.MALE to stringResource(R.string.edit_profile_gender_male),
        Gender.FEMALE to stringResource(R.string.edit_profile_gender_female),
        Gender.NOT_SPECIFIED to stringResource(R.string.edit_profile_gender_not_specified),
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
    ) {
        options.forEachIndexed { index, (gender, label) ->
            val isSelected = selected == gender
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (isSelected) LbColors.Green else Color.White)
                    .clickable { onSelect(gender) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else LbColors.Ink2,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
            if (index < options.lastIndex) {
                Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(LbColors.Line))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WithdrawSheet(onWithdrawClick: () -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(), modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.edit_profile_withdraw_sheet_title),
                color = LbColors.Ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.edit_profile_withdraw_sheet_body),
                color = LbColors.Ink2,
                fontSize = 13.5.sp,
                lineHeight = 20.sp,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LbButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = LbButtonDefaults.whiteColors(),
                ) {
                    Text(text = stringResource(R.string.edit_profile_withdraw_cancel), fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
                }
                LbButton(
                    onClick = onWithdrawClick,
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = LbButtonDefaults.whiteColors(),
                ) {
                    Text(
                        text = stringResource(R.string.edit_profile_withdraw_confirm),
                        color = LbColors.DangerStrong,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
