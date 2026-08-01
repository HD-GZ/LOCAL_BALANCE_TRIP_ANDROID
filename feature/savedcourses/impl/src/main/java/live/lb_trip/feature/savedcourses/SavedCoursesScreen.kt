package live.lb_trip.feature.savedcourses

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.domain.model.TravelStatus

@Composable
internal fun SavedCoursesScreen(
    onBack: () -> Unit,
    onCourseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedCoursesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val loadErrorMessage = stringResource(R.string.savedcourses_error_load)
    val retryActionLabel = stringResource(R.string.savedcourses_action_retry)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                SavedCoursesSideEffect.ShowLoadError -> {
                    val result =
                        snackbarHostState.showSnackbar(message = loadErrorMessage, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(SavedCoursesIntent.Retry)
                    }
                }
            }
        }
    }

    val view = LocalView.current
    val activity = LocalActivity.current
    if (activity != null) {
        SideEffect {
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
        }
    }

    SavedCoursesScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onCourseClick = onCourseClick,
        modifier = modifier,
    )
}

@Composable
private fun SavedCoursesScreenContent(
    state: SavedCoursesUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onCourseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LbTopBar(
                onBackClick = onBack,
                backContentDescription = stringResource(R.string.savedcourses_back_content_description),
                title = stringResource(R.string.savedcourses_title),
                titleTrailing = {
                    Text(
                        text = stringResource(R.string.savedcourses_count_template, state.courses.size),
                        color = LbColors.Green,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 6.dp),
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(LbColors.ScreenBg),
        ) {
            when {
                state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LbColors.Green)
                }

                state.courses.isEmpty() -> Text(
                    text = stringResource(R.string.savedcourses_empty),
                    color = LbColors.Ink3,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 32.dp),
                )

                else -> SavedCoursesList(courses = state.courses, onCourseClick = onCourseClick)
            }
        }
    }
}

@Composable
private fun SavedCoursesList(
    courses: ImmutableList<SavedCourseSummary>,
    onCourseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        items(courses, key = { it.savedCourseId }) { course ->
            SavedCoursesRow(course = course, onClick = { onCourseClick(course.savedCourseId) })
        }
    }
}

@Composable
private fun SavedCoursesRow(course: SavedCourseSummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, LbColors.Line, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(width = 76.dp, height = 92.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(listOf(LbColors.GreenTint2, LbColors.GreenBlock))),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_route),
                contentDescription = null,
                tint = LbColors.Green,
                modifier = Modifier.size(28.dp),
            )
        }
        Column {
            SavedCourseStatusBadge(status = course.status)
            Text(
                text = course.courseName,
                color = LbColors.Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun SavedCourseStatusBadge(status: TravelStatus, modifier: Modifier = Modifier) {
    val label = when (status) {
        TravelStatus.BEFORE_TRIP -> stringResource(R.string.savedcourses_status_before_trip)
        TravelStatus.TRAVELING -> stringResource(R.string.savedcourses_status_traveling)
        TravelStatus.COMPLETED -> stringResource(R.string.savedcourses_status_completed)
    }
    Text(
        text = label,
        color = LbColors.Green,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(LbColors.GreenTint2)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun SavedCoursesScreenPreview() {
    SavedCoursesScreenContent(
        state = SavedCoursesUiState(isLoading = false, courses = persistentListOf()),
        snackbarHostState = remember { SnackbarHostState() },
        onBack = {},
        onCourseClick = {},
    )
}
