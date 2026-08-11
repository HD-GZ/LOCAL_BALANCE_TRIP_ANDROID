package live.lb_trip.core.designsystem.component

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldPredictiveBackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * List-detail pane directive shared by every two-pane screen host: lowers the two-pane threshold
 * from EXPANDED to MEDIUM (so fold/tablet width triggers it) and removes the default gap between
 * panes, since each host's list/detail content supplies its own edge spacing.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun calculateLbListDetailDirective(): PaneScaffoldDirective =
    calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(currentWindowAdaptiveInfo())
        .let { it.copy(horizontalPartitionSpacerSize = 0.dp) }

/**
 * In two-pane mode both panes are already visible, so a single back press should leave the
 * feature immediately instead of first collapsing the detail pane. Only single-pane layouts need
 * predictive back for step-by-step list<->detail navigation.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun <T> LbSinglePaneBackHandler(navigator: ThreePaneScaffoldNavigator<T>, isTwoPane: Boolean) {
    if (!isTwoPane) {
        ThreePaneScaffoldPredictiveBackHandler(
            navigator = navigator,
            backBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange,
        )
    }
}
