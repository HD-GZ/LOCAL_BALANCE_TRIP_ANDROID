package live.lb_trip.feature.settings

import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import live.lb_trip.domain.model.TermsType

fun NavGraphBuilder.termsScreen(navController: NavController) {
    composable<TermsRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<TermsRoute>()
        val type = runCatching { TermsType.valueOf(route.type) }.getOrDefault(TermsType.SERVICE)
        TermsScreen(
            type = type,
            fallbackTitle = stringResource(
                if (type == TermsType.PRIVACY) R.string.settings_menu_privacy else R.string.settings_menu_terms,
            ),
            onBack = navController::popBackStack,
        )
    }
}
