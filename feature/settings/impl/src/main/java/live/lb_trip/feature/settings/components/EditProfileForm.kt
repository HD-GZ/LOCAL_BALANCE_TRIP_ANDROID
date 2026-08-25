package live.lb_trip.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbBirthField
import live.lb_trip.core.designsystem.component.LbInputField
import live.lb_trip.feature.settings.EditProfileIntent
import live.lb_trip.feature.settings.EditProfileUiState
import live.lb_trip.feature.settings.R

@Composable
internal fun EditProfileForm(
    state: EditProfileUiState,
    onIntent: (EditProfileIntent) -> Unit,
    onWithdrawLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val passwordFocusRequester = remember { FocusRequester() }
    val passwordConfirmFocusRequester = remember { FocusRequester() }
    val birthYearFocusRequester = remember { FocusRequester() }

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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
        )

        LbInputField(
            value = state.password,
            onValueChange = { onIntent(EditProfileIntent.PasswordChanged(it)) },
            label = stringResource(R.string.edit_profile_password_label),
            placeholder = stringResource(R.string.edit_profile_password_placeholder),
            hintText = stringResource(R.string.edit_profile_password_hint),
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { passwordConfirmFocusRequester.requestFocus() }),
            textFieldModifier = Modifier.focusRequester(passwordFocusRequester),
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { birthYearFocusRequester.requestFocus() }),
                textFieldModifier = Modifier.focusRequester(passwordConfirmFocusRequester),
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

        LbBirthField(
            year = state.birthYear,
            month = state.birthMonth,
            day = state.birthDay,
            onYearChange = { onIntent(EditProfileIntent.BirthYearChanged(it)) },
            onMonthChange = { onIntent(EditProfileIntent.BirthMonthChanged(it)) },
            onDayChange = { onIntent(EditProfileIntent.BirthDayChanged(it)) },
            label = stringResource(R.string.edit_profile_birth_date_label),
            yearFocusRequester = birthYearFocusRequester,
        )

        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(
                text = stringResource(R.string.edit_profile_gender_label),
                color = LbColors.Ink2,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            EditProfileGenderSelector(
                selected = state.gender,
                onSelect = { onIntent(EditProfileIntent.GenderChanged(it)) },
            )
        }

        Text(
            text = stringResource(R.string.edit_profile_withdraw_link),
            color = LbColors.DangerStrong,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onWithdrawLinkClick),
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
