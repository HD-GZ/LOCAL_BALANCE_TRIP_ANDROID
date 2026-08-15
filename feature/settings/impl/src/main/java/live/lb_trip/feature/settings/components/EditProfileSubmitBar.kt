package live.lb_trip.feature.settings.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import live.lb_trip.core.designsystem.component.LbBottomActionBar
import live.lb_trip.core.designsystem.component.LbBottomActionButton
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
    LbBottomActionBar(
        modifier = modifier,
        windowInsets = WindowInsets.navigationBars.union(WindowInsets.ime),
    ) {
        LbBottomActionButton(
            text = stringResource(R.string.edit_profile_submit),
            onClick = { onIntent(EditProfileIntent.SaveClicked) },
            enabled = isEditProfileFormValid(state) && !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
