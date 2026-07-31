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
internal fun rememberFineLocationPermissionGranted(): Boolean {
    val context = LocalContext.current
    var isGranted by remember { mutableStateOf(context.hasFineLocationPermission()) }
    var hasRequested by rememberSaveable { mutableStateOf(false) }
    var showRationaleDialog by rememberSaveable { mutableStateOf(false) }
    var showDeniedDialog by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        isGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (!isGranted) showDeniedDialog = true
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        isGranted = context.hasFineLocationPermission()
    }

    LaunchedEffect(Unit) {
        if (!isGranted && !hasRequested) {
            hasRequested = true
            showRationaleDialog = true
        }
    }

    if (showRationaleDialog) {
        LocationPermissionDialog(
            title = stringResource(R.string.tour_location_permission_rationale_title),
            message = stringResource(R.string.tour_location_permission_rationale_message),
            onConfirm = {
                showRationaleDialog = false
                launcher.launch(LOCATION_PERMISSIONS)
            },
        )
    }

    if (showDeniedDialog) {
        LocationPermissionDialog(
            title = stringResource(R.string.tour_location_permission_denied_title),
            message = stringResource(R.string.tour_location_permission_denied_message),
            onConfirm = { showDeniedDialog = false },
        )
    }

    return isGranted
}

@Composable
private fun LocationPermissionDialog(title: String, message: String, onConfirm: () -> Unit) {
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

private fun Context.hasFineLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)
