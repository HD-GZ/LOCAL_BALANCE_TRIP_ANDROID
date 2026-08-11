package live.lb_trip.feature.savedcourses

import android.net.Uri
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldPredictiveBackHandler
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.component.LbEmptyDetailPane

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun SavedCoursesPaneHost(
    onBack: () -> Unit,
    onNavigateToTour: (Long) -> Unit,
    onNavigateToReceiptCapture: (Long, Uri) -> Unit,
    onNavigateToReceiptDetail: (Long, Long) -> Unit,
    receiptRegistered: Boolean,
    onReceiptRegisteredConsumed: () -> Unit,
    tourEnded: Boolean,
    onTourEndedConsumed: () -> Unit,
    modifier: Modifier = Modifier,
    initialSavedCourseId: Long? = null,
) {
    val directive = calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(currentWindowAdaptiveInfo())
        .let { it.copy(horizontalPartitionSpacerSize = 0.dp) }
    val isTwoPane = directive.maxHorizontalPartitions > 1
    val initialHistory = remember(initialSavedCourseId) {
        buildList {
            add(ThreePaneScaffoldDestinationItem<Long>(ListDetailPaneScaffoldRole.List))
            if (initialSavedCourseId != null) {
                add(ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.Detail, initialSavedCourseId))
            }
        }
    }
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>(
        scaffoldDirective = directive,
        initialDestinationHistory = initialHistory,
    )
    val scope = rememberCoroutineScope()

    // In two-pane mode both panes are already visible, so a single back press should leave the
    // feature immediately instead of first collapsing the detail pane. Only the compact/single-pane
    // layout gets predictive back handling for step-by-step list<->detail navigation.
    if (!isTwoPane) {
        ThreePaneScaffoldPredictiveBackHandler(
            navigator = navigator,
            backBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange,
        )
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        scaffoldState = navigator.scaffoldState,
        modifier = modifier,
        listPane = {
            AnimatedPane {
                SavedCoursesScreen(
                    onBack = onBack,
                    onCourseClick = { savedCourseId ->
                        scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, savedCourseId) }
                    },
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val savedCourseId = navigator.currentDestination?.contentKey
                if (savedCourseId != null) {
                    SavedCourseDetailScreen(
                        savedCourseId = savedCourseId,
                        onBack = { scope.launch { navigator.navigateBack() } },
                        onNavigateToTour = onNavigateToTour,
                        onNavigateToReceiptCapture = onNavigateToReceiptCapture,
                        onNavigateToReceiptDetail = onNavigateToReceiptDetail,
                        receiptRegistered = receiptRegistered,
                        onReceiptRegisteredConsumed = onReceiptRegisteredConsumed,
                        tourEnded = tourEnded,
                        onTourEndedConsumed = onTourEndedConsumed,
                        showBackButton = !isTwoPane,
                    )
                } else {
                    LbEmptyDetailPane(message = stringResource(R.string.savedcourses_select_prompt))
                }
            }
        },
    )
}
