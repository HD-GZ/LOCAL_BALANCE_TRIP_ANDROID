package live.lb_trip.feature.tour.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import live.lb_trip.feature.tour.R

@Composable
internal fun rememberActivityRecognitionPermissionGranted(): Boolean {
    val context = LocalContext.current
    var permissionResult by rememberSaveable { mutableStateOf<Boolean?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        permissionResult = isGranted
    }

    return rememberTourPermissionGranted(
        messages = TourPermissionRequest(
            rationaleTitleResId = R.string.tour_activity_recognition_permission_rationale_title,
            rationaleMessageResId = R.string.tour_activity_recognition_permission_rationale_message,
            deniedTitleResId = R.string.tour_activity_recognition_permission_denied_title,
            deniedMessageResId = R.string.tour_activity_recognition_permission_denied_message,
        ),
        isPermissionGranted = context::hasActivityRecognitionPermission,
        permissionResult = permissionResult,
        onPermissionResultConsumed = { permissionResult = null },
        requestPermission = {
            launcher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        },
    )
}

private fun Context.hasActivityRecognitionPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) ==
        PackageManager.PERMISSION_GRANTED
