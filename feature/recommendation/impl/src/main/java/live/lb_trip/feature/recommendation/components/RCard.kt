package live.lb_trip.feature.recommendation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import live.lb_trip.core.designsystem.R as DesignSystemR

@Composable
internal fun RCard(
    title: String,
    reason: String,
    isBest: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    imageLoader: ImageLoader? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(15.dp))
            .background(Paper)
            .border(
                width = if (isBest) 1.5.dp else 1.dp,
                color = if (isBest) GreenLine else Line,
                shape = RoundedCornerShape(15.dp),
            )
            .clickable(onClick = onClick),
    ) {
        val imageModifier = Modifier.fillMaxHeight().defaultMinSize(minHeight = 118.dp)
        if (imageUrl != null && imageLoader != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop,
                modifier = imageModifier.width(100.dp),
            )
        } else {
            RCardImagePlaceholder(modifier = imageModifier)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp, top = 13.dp, end = 13.dp, bottom = 13.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = title, color = Ink, fontSize = 16.5.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reason,
                color = Ink2,
                fontSize = 11.5.sp,
                lineHeight = 16.5.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Ink4,
                modifier = Modifier.size(18.dp).align(Alignment.End),
            )
        }
    }
}

@Composable
private fun RCardImagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(100.dp)
            .background(Brush.linearGradient(listOf(GreenTint, Color(0xFFEEF4EE)))),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_route),
            contentDescription = null,
            tint = Green,
            modifier = Modifier.size(34.dp).alpha(0.72f),
        )
    }
}
