package live.lb_trip.feature.savedcourses

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

fun NavGraphBuilder.savedCoursesScreen(onBack: () -> Unit, onCourseClick: (Long) -> Unit) {
    composable<SavedCoursesRoute> {
        SavedCoursesScreen(onBack = onBack, onCourseClick = onCourseClick)
    }
}

fun NavGraphBuilder.savedCourseDetailScreen(
    onBack: () -> Unit,
    onNavigateToTour: (Long) -> Unit,
    onNavigateToReceiptCapture: (Long, Uri) -> Unit,
    onNavigateToReceiptDetail: (Long, Long) -> Unit,
) {
    composable<SavedCourseDetailRoute> { backStackEntry ->
        val receiptRegistered by backStackEntry.savedStateHandle
            .getStateFlow(RECEIPT_REGISTERED_RESULT_KEY, false)
            .collectAsStateWithLifecycle()
        val tourEnded by backStackEntry.savedStateHandle
            .getStateFlow(TOUR_ENDED_RESULT_KEY, false)
            .collectAsStateWithLifecycle()
        SavedCourseDetailScreen(
            onBack = onBack,
            onNavigateToTour = onNavigateToTour,
            onNavigateToReceiptCapture = onNavigateToReceiptCapture,
            onNavigateToReceiptDetail = onNavigateToReceiptDetail,
            receiptRegistered = receiptRegistered,
            onReceiptRegisteredConsumed = {
                backStackEntry.savedStateHandle[RECEIPT_REGISTERED_RESULT_KEY] = false
            },
            tourEnded = tourEnded,
            onTourEndedConsumed = {
                backStackEntry.savedStateHandle[TOUR_ENDED_RESULT_KEY] = false
            },
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
