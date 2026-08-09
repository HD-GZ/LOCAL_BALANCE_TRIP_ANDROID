package live.lb_trip.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.component.LbBrush
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.feature.settings.EditProfileIntent
import live.lb_trip.feature.settings.EditProfileUiState
import live.lb_trip.feature.settings.R

private fun isEditProfileFormValid(state: EditProfileUiState): Boolean =
    state.name.isNotEmpty() &&
        state.birthYear.length == 4 &&
        state.birthMonth in 1..12 &&
        (state.birthDay.toIntOrNull() ?: 0) in 1..31 &&
        (state.password.isEmpty() || state.password == state.passwordConfirm)

@Composable
internal fun EditProfileSubmitBar(
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
