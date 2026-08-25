package live.lb_trip.feature.savedcourses

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

fun NavGraphBuilder.savedCoursesScreen(
    onBack: () -> Unit,
    onNavigateToTour: (Long) -> Unit,
    onNavigateToReceiptCapture: (Long, Uri) -> Unit,
    onNavigateToReceiptDetail: (Long, Long) -> Unit,
) {
    composable<SavedCoursesRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<SavedCoursesRoute>()
        val receiptRegistered by backStackEntry.savedStateHandle
            .getStateFlow(RECEIPT_REGISTERED_RESULT_KEY, false)
            .collectAsStateWithLifecycle()
        val tourStarted by backStackEntry.savedStateHandle
            .getStateFlow(TOUR_STARTED_RESULT_KEY, false)
            .collectAsStateWithLifecycle()
        val tourEnded by backStackEntry.savedStateHandle
            .getStateFlow(TOUR_ENDED_RESULT_KEY, false)
            .collectAsStateWithLifecycle()
        val listNeedsRefresh by backStackEntry.savedStateHandle
            .getStateFlow(SAVED_COURSES_REFRESH_RESULT_KEY, false)
            .collectAsStateWithLifecycle()
        val tourEndedShowReport by backStackEntry.savedStateHandle
            .getStateFlow(TOUR_ENDED_SHOW_REPORT_RESULT_KEY, false)
            .collectAsStateWithLifecycle()
        SavedCoursesPaneHost(
            onBack = onBack,
            onNavigateToTour = onNavigateToTour,
            onNavigateToReceiptCapture = onNavigateToReceiptCapture,
            onNavigateToReceiptDetail = onNavigateToReceiptDetail,
            receiptRegistered = receiptRegistered,
            onReceiptRegisteredConsumed = {
                backStackEntry.savedStateHandle[RECEIPT_REGISTERED_RESULT_KEY] = false
            },
            tourStarted = tourStarted,
            tourEnded = tourEnded,
            tourEndedShowReport = tourEndedShowReport,
            onTourResultConsumed = {
                backStackEntry.savedStateHandle[TOUR_STARTED_RESULT_KEY] = false
                backStackEntry.savedStateHandle[TOUR_ENDED_RESULT_KEY] = false
                backStackEntry.savedStateHandle[TOUR_ENDED_SHOW_REPORT_RESULT_KEY] = false
            },
            listNeedsRefresh = listNeedsRefresh,
            onListRefreshConsumed = {
                backStackEntry.savedStateHandle[SAVED_COURSES_REFRESH_RESULT_KEY] = false
            },
            initialSavedCourseId = route.initialSavedCourseId,
        )
    }
}

fun NavGraphBuilder.receiptCaptureScreen(onBack: () -> Unit, onSubmitted: () -> Unit) {
    composable<ReceiptCaptureRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ReceiptCaptureRoute>()
        ReceiptCaptureScreen(imageUri = route.imageUri.toUri(), onBack = onBack, onSubmitted = onSubmitted)
    }
}

fun NavGraphBuilder.receiptDetailScreen(onBack: () -> Unit, onUpdated: () -> Unit, onDeleted: () -> Unit) {
    composable<ReceiptDetailRoute> {
        ReceiptDetailScreen(onBack = onBack, onUpdated = onUpdated, onDeleted = onDeleted)
    }
}

fun NavGraphBuilder.sharedCourseDetailScreen(onBack: () -> Unit) {
    composable<SharedCourseRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<SharedCourseRoute>()
        SharedCourseDetailScreen(token = route.token, onBack = onBack)
    }
}
