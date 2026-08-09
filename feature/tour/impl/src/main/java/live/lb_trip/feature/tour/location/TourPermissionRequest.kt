package live.lb_trip.feature.tour.location

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.feature.tour.R

internal data class TourPermissionRequest(
    @param:StringRes val rationaleTitleResId: Int,
    @param:StringRes val rationaleMessageResId: Int,
    @param:StringRes val deniedTitleResId: Int,
    @param:StringRes val deniedMessageResId: Int,
)

@Composable
internal fun rememberTourPermissionGranted(
    messages: TourPermissionRequest,
    isPermissionGranted: () -> Boolean,
    requestPermission: ((Boolean) -> Unit) -> Unit,
): Boolean {
    var isGranted by remember { mutableStateOf(isPermissionGranted()) }
    var hasRequested by rememberSaveable { mutableStateOf(false) }
    var showRationaleDialog by rememberSaveable { mutableStateOf(false) }
    var showDeniedDialog by rememberSaveable { mutableStateOf(false) }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        isGranted = isPermissionGranted()
    }

    LaunchedEffect(Unit) {
        if (!isGranted && !hasRequested) {
            hasRequested = true
            showRationaleDialog = true
        }
    }

    if (showRationaleDialog) {
        TourPermissionDialog(
            title = stringResource(messages.rationaleTitleResId),
            message = stringResource(messages.rationaleMessageResId),
            onConfirm = {
                showRationaleDialog = false
                requestPermission { granted ->
                    isGranted = granted
                    if (!isGranted) showDeniedDialog = true
                }
            },
        )
    }

    if (showDeniedDialog) {
        TourPermissionDialog(
            title = stringResource(messages.deniedTitleResId),
            message = stringResource(messages.deniedMessageResId),
            onConfirm = { showDeniedDialog = false },
        )
    }

    return isGranted
}

@Composable
private fun TourPermissionDialog(title: String, message: String, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onConfirm,
        title = { Text(text = title, color = LbColors.Ink) },
        text = { Text(text = message, color = LbColors.Ink2) },
        confirmButton = {
            LbButton(onClick = onConfirm, colors = LbButtonDefaults.greenColors()) {
                Text(text = stringResource(R.string.tour_action_confirm))
            }
        },
        containerColor = LbColors.Paper,
    )
}
