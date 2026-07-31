package live.lb_trip.feature.savedcourses

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.savedCoursesScreen(onBack: () -> Unit, onCourseClick: (Long) -> Unit) {
    composable<SavedCoursesRoute> {
        SavedCoursesScreen(onBack = onBack, onCourseClick = onCourseClick)
    }
}

fun NavGraphBuilder.savedCourseDetailScreen(
    onBack: () -> Unit,
    onNavigateToTour: (Long) -> Unit,
    onNavigateToReceiptCapture: (Long) -> Unit,
) {
    composable<SavedCourseDetailRoute> { backStackEntry ->
        val receiptRegistered by backStackEntry.savedStateHandle
            .getStateFlow(RECEIPT_REGISTERED_RESULT_KEY, false)
            .collectAsStateWithLifecycle()
        SavedCourseDetailScreen(
            onBack = onBack,
            onNavigateToTour = onNavigateToTour,
            onNavigateToReceiptCapture = onNavigateToReceiptCapture,
            receiptRegistered = receiptRegistered,
            onReceiptRegisteredConsumed = {
                backStackEntry.savedStateHandle[RECEIPT_REGISTERED_RESULT_KEY] = false
            },
        )
    }
}

fun NavGraphBuilder.receiptCaptureScreen(onBack: () -> Unit, onSubmitted: () -> Unit) {
    composable<ReceiptCaptureRoute> {
        ReceiptCaptureScreen(onBack = onBack, onSubmitted = onSubmitted)
    }
}
