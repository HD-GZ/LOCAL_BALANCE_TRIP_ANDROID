package live.lb_trip.feature.settings

import androidx.annotation.RawRes
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.editProfileScreen(onBack: () -> Unit, onSaved: () -> Unit) {
    composable<EditProfileRoute> {
        EditProfileScreen(onBack = onBack, onSaved = onSaved)
    }
}

fun NavGraphBuilder.licensesScreen(@RawRes librariesRawResId: Int, onBack: () -> Unit) {
    composable<LicensesRoute> {
        LicensesScreen(librariesRawResId = librariesRawResId, onBack = onBack)
    }
}
