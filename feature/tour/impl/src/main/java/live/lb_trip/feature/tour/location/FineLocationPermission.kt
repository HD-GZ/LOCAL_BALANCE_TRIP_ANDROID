package live.lb_trip.feature.tour.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import live.lb_trip.feature.tour.R

@Composable
internal fun rememberFineLocationPermissionGranted(): Boolean {
    val context = LocalContext.current
    var onPermissionResult by remember { mutableStateOf<(Boolean) -> Unit>({}) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        onPermissionResult(result[Manifest.permission.ACCESS_FINE_LOCATION] == true)
    }

    return rememberTourPermissionGranted(
        messages = TourPermissionRequest(
            rationaleTitleResId = R.string.tour_location_permission_rationale_title,
            rationaleMessageResId = R.string.tour_location_permission_rationale_message,
            deniedTitleResId = R.string.tour_location_permission_denied_title,
            deniedMessageResId = R.string.tour_location_permission_denied_message,
        ),
        isPermissionGranted = context::hasFineLocationPermission,
        requestPermission = { onResult ->
            onPermissionResult = onResult
            launcher.launch(LOCATION_PERMISSIONS)
        },
    )
}

private fun Context.hasFineLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)
