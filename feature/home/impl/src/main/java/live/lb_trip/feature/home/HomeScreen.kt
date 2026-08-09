package live.lb_trip.feature.home

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.domain.model.TravelStatus

private val TextPrimary = LbColors.Ink
private val Brand = LbColors.Green
private val HeroGradientMid = Color(0xFF276044)
private val HeroGradientEnd = Color(0xFF245A40)
private val HeroBlob = Color(0x24FFFFFF)
private val HeroEyebrowText = Color(0xB8FFFFFF)
private val HeroBodyText = Color(0xD1FFFFFF)
private val SavedCardRegionText = Color(0xFF928D84)

@Composable
fun HomeTabContent(
    onStartDiagnosisClick: () -> Unit,
    onSavedAllClick: () -> Unit,
    onCourseClick: (Long) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onIntent: (HomeIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val loadErrorMessage = stringResource(R.string.home_error_courses_load)
    val retryActionLabel = stringResource(R.string.home_action_retry)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                HomeSideEffect.ShowCoursesLoadError -> {
                    val result = snackbarHostState.showSnackbar(message = loadErrorMessage, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        onIntent(HomeIntent.Retry)
                    }
                }
            }
        }
    }

    HomeTabContentBody(
        state = state,
        onStartDiagnosisClick = onStartDiagnosisClick,
        onSavedAllClick = onSavedAllClick,
        onCourseClick = onCourseClick,
        modifier = modifier,
    )
}

@Composable
private fun HomeTabContentBody(
    state: HomeUiState,
    onStartDiagnosisClick: () -> Unit,
    onSavedAllClick: () -> Unit,
    onCourseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 22.dp),
    ) {
        HomeDiagnosisHero(onStartDiagnosisClick = onStartDiagnosisClick)

        if (!state.isLoadingCourses && state.courses.isNotEmpty()) {
            HomeSavedCoursesSection(
                courses = state.courses,
                onSavedAllClick = onSavedAllClick,
                onCourseClick = onCourseClick,
                modifier = Modifier.padding(top = 26.dp),
            )
        } else if (state.isLoadingCourses) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Brand)
            }
        }
    }
}

@Composable
private fun HomeSavedCoursesSection(
    courses: ImmutableList<HomeCourseSummary>,
    onSavedAllClick: () -> Unit,
    onCourseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(R.string.home_saved_section_title),
                color = TextPrimary,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.home_saved_count_template, courses.size),
                color = LbColors.GreenDk,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(LbColors.GreenTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
            Box(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.home_saved_view_all),
                color = LbColors.Green,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onSavedAllClick)
                    .padding(4.dp),
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            modifier = Modifier.padding(top = 12.dp),
        ) {
            items(courses, key = { it.savedCourseId }) { course ->
                HomeSavedCourseCard(
                    course = course,
                    onClick = { onCourseClick(course.savedCourseId) },
                    modifier = Modifier.width(297.dp),
                )
            }
        }
    }
}

@Composable
private fun HomeSavedCourseCard(course: HomeCourseSummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .border(1.dp, LbColors.Line, RoundedCornerShape(15.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp)
                .background(Brush.linearGradient(listOf(LbColors.GreenTint2, LbColors.GreenBlock)))
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_route),
                contentDescription = null,
                tint = LbColors.Green,
                modifier = Modifier.align(Alignment.Center).size(38.dp),
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(start = 15.dp, top = 13.dp, end = 15.dp, bottom = 15.dp),
        ) {
            Text(
                text = course.status.toLabel(),
                color = SavedCardRegionText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = course.courseName,
                color = TextPrimary,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TravelStatus.toLabel(): String = when (this) {
    TravelStatus.BEFORE_TRIP -> stringResource(R.string.home_status_before_trip)
    TravelStatus.TRAVELING -> stringResource(R.string.home_status_traveling)
    TravelStatus.COMPLETED -> stringResource(R.string.home_status_completed)
}

@Composable
private fun HomeDiagnosisHero(onStartDiagnosisClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(listOf(LbColors.GreenDeep, HeroGradientMid, HeroGradientEnd)),
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-34).dp)
                .size(150.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(HeroBlob, Color.Transparent))),
        )
        Column(modifier = Modifier.padding(start = 22.dp, top = 22.dp, end = 22.dp, bottom = 20.dp)) {
            Text(
                text = stringResource(R.string.home_hero_eyebrow),
                color = HeroEyebrowText,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.06.em,
            )
            Text(
                text = stringResource(R.string.home_hero_title),
                color = Color.White,
                fontSize = 21.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.02).em,
                modifier = Modifier.padding(top = 9.dp, bottom = 8.dp),
            )
            Text(
                text = stringResource(R.string.home_hero_description),
                color = HeroBodyText,
                fontSize = 12.5.sp,
                lineHeight = 19.4.sp,
                modifier = Modifier.padding(bottom = 17.dp),
            )
            LbButton(
                onClick = onStartDiagnosisClick,
                colors = LbButtonDefaults.whiteColors(),
                shape = RoundedCornerShape(13.dp),
                elevation = null,
                modifier = Modifier.fillMaxWidth().height(50.dp),
            ) {
                Text(
                    text = stringResource(R.string.home_cta_start_diagnosis),
                    color = LbColors.GreenForest,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = LbColors.GreenForest,
                    modifier = Modifier.padding(start = 8.dp).size(18.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeTabContentBody(
        state = HomeUiState(isLoadingCourses = false, courses = persistentListOf()),
        onStartDiagnosisClick = {},
        onSavedAllClick = {},
        onCourseClick = {},
    )
}
