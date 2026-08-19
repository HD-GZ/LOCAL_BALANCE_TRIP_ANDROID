package live.lb_trip.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbChip
import live.lb_trip.core.designsystem.component.LbChipDefaults
import live.lb_trip.domain.model.ProfileType

private const val TYPE_PEEK_COUNT = 3

@Composable
internal fun HomeNoTypeCard(
    types: ImmutableList<ProfileType>,
    imageLoader: ImageLoader,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(R.string.home_mytype_section_title),
                color = LbColors.Ink,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
            )
            Box(modifier = Modifier.weight(1f))
            LbChip(
                label = stringResource(R.string.home_notype_chip),
                colors = LbChipDefaults.colors(),
                sizes = LbChipDefaults.sizes().copy(height = 22.dp, horizontalPadding = 10.dp, fontSize = 10.5.sp),
                leading = null,
            )
        }
        Text(
            text = stringResource(R.string.home_notype_subtitle),
            color = LbColors.Ink3,
            fontSize = 11.5.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 12.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LbColors.Paper)
                .border(1.dp, LbColors.GreenLine, RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(LbColors.GreenTint)
                        .border(1.dp, LbColors.GreenLine, RoundedCornerShape(13.dp)),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_route),
                        contentDescription = null,
                        tint = LbColors.Green,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = stringResource(R.string.home_notype_card_title),
                        color = LbColors.Ink,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.home_notype_card_description),
                        color = LbColors.Ink2,
                        fontSize = 12.sp,
                        lineHeight = 18.5.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
            Column {
                HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                ) {
                    NoTypeStep(
                        value = stringResource(R.string.home_notype_step_time_value),
                        label = stringResource(R.string.home_notype_step_time_label),
                        modifier = Modifier.weight(1f),
                    )
                    NoTypeStep(
                        value = stringResource(R.string.home_notype_step_questions_value),
                        label = stringResource(R.string.home_notype_step_questions_label),
                        modifier = Modifier.weight(1f),
                    )
                    NoTypeStep(
                        value = stringResource(R.string.home_notype_step_retake_value),
                        label = stringResource(R.string.home_notype_step_retake_label),
                        modifier = Modifier.weight(1f),
                    )
                }
                HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
            }
            LbButton(
                onClick = onStartClick,
                colors = LbButtonDefaults.greenColors(),
                shape = RoundedCornerShape(13.dp),
                elevation = null,
                modifier = Modifier.fillMaxWidth().heightIn(min = 46.dp),
            ) {
                Text(
                    text = stringResource(R.string.home_notype_cta),
                    color = LbColors.Paper,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = LbColors.Paper,
                    modifier = Modifier.padding(start = 8.dp).size(16.dp),
                )
            }
        }
        if (types.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 11.dp),
            ) {
                types.take(TYPE_PEEK_COUNT).forEach { type ->
                    NoTypePeekChip(type = type, imageLoader = imageLoader)
                }
                val remaining = types.size - TYPE_PEEK_COUNT
                if (remaining > 0) {
                    Text(
                        text = stringResource(R.string.home_notype_peek_more_template, remaining),
                        color = LbColors.Ink3,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun NoTypeStep(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = value,
            color = LbColors.Ink,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = label,
            color = LbColors.Ink3,
            fontSize = 11.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun NoTypePeekChip(type: ProfileType, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(LbColors.SurfaceSoft)
            .border(1.dp, LbColors.LineSoft, RoundedCornerShape(100.dp))
            .padding(horizontal = 12.dp),
    ) {
        AsyncImage(
            model = type.imageUrl,
            contentDescription = null,
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(LbColors.GreenTint),
        )
        Text(text = type.nickname, color = LbColors.Ink2, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
    }
}
