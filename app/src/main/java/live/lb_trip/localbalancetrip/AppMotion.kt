package live.lb_trip.localbalancetrip

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneMotionDefaults
import androidx.navigation.NavBackStackEntry

/**
 * Reuses the same spring specs the adaptive pane scaffolds animate with
 * (spring(dampingRatio = 0.8f, stiffness = 380f)) so the top-level NavHost's screen
 * transitions feel consistent with pane transitions elsewhere in the app.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal val appEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInHorizontally(PaneMotionDefaults.OffsetAnimationSpec) { it } + fadeIn(PaneMotionDefaults.VisibilityAnimationSpec)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal val appExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutHorizontally(PaneMotionDefaults.OffsetAnimationSpec) { -it / 4 } + fadeOut(PaneMotionDefaults.VisibilityAnimationSpec)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal val appPopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInHorizontally(PaneMotionDefaults.OffsetAnimationSpec) { -it / 4 } + fadeIn(PaneMotionDefaults.VisibilityAnimationSpec)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal val appPopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutHorizontally(PaneMotionDefaults.OffsetAnimationSpec) { it } + fadeOut(PaneMotionDefaults.VisibilityAnimationSpec)
}
