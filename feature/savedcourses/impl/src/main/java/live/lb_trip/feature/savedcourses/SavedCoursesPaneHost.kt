package live.lb_trip.feature.savedcourses

import android.net.Uri
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

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
) {
    val directive = calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(currentWindowAdaptiveInfo())
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>(scaffoldDirective = directive)
    val scope = rememberCoroutineScope()

    NavigableListDetailPaneScaffold(
        navigator = navigator,
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
                    )
                }
            }
        },
    )
}
