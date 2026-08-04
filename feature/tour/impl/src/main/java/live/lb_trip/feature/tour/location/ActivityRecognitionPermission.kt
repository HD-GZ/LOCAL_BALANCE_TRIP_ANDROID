package live.lb_trip.feature.tour.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.feature.tour.R

@Composable
internal fun rememberActivityRecognitionPermissionGranted(): Boolean {
    val context = LocalContext.current
    var isGranted by remember { mutableStateOf(context.hasActivityRecognitionPermission()) }
    var hasRequested by rememberSaveable { mutableStateOf(false) }
    var showRationaleDialog by rememberSaveable { mutableStateOf(false) }
    var showDeniedDialog by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        isGranted = granted
        if (!isGranted) showDeniedDialog = true
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        isGranted = context.hasActivityRecognitionPermission()
    }

    LaunchedEffect(Unit) {
        if (!isGranted && !hasRequested) {
            hasRequested = true
            showRationaleDialog = true
        }
    }

    if (showRationaleDialog) {
        ActivityRecognitionPermissionDialog(
            title = stringResource(R.string.tour_activity_recognition_permission_rationale_title),
            message = stringResource(R.string.tour_activity_recognition_permission_rationale_message),
            onConfirm = {
                showRationaleDialog = false
                launcher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            },
        )
    }

    if (showDeniedDialog) {
        ActivityRecognitionPermissionDialog(
            title = stringResource(R.string.tour_activity_recognition_permission_denied_title),
            message = stringResource(R.string.tour_activity_recognition_permission_denied_message),
            onConfirm = { showDeniedDialog = false },
        )
    }

    return isGranted
}

@Composable
private fun ActivityRecognitionPermissionDialog(title: String, message: String, onConfirm: () -> Unit) {
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

private fun Context.hasActivityRecognitionPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) ==
        PackageManager.PERMISSION_GRANTED
