package live.lb_trip.feature.settings

import androidx.annotation.RawRes
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import live.lb_trip.domain.model.TermsType

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

fun NavGraphBuilder.termsScreen(onBack: () -> Unit) {
    composable<TermsRoute> {
        TermsScreen(
            type = TermsType.SERVICE,
            fallbackTitle = stringResource(R.string.settings_menu_terms),
            onBack = onBack,
        )
    }
}

fun NavGraphBuilder.privacyScreen(onBack: () -> Unit) {
    composable<PrivacyRoute> {
        TermsScreen(
            type = TermsType.PRIVACY,
            fallbackTitle = stringResource(R.string.settings_menu_privacy),
            onBack = onBack,
        )
    }
}
