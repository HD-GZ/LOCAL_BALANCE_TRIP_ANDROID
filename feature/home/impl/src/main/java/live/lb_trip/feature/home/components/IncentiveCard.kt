package live.lb_trip.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.feature.home.HomeIncentiveCard
import live.lb_trip.feature.home.ddayLabel
import live.lb_trip.feature.home.formattedEndDate

@Composable
fun IncentiveCard(card: HomeIncentiveCard, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isClosingSoon = card.dday != null && card.dday in 0..14
    val stateColor = if (isClosingSoon) LbColors.Warning else LbColors.GreenDk
    val stateBackground = if (isClosingSoon) Color(0xFFF7EFD9) else LbColors.GreenTint2

    Column(
        verticalArrangement = Arrangement.spacedBy(7.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(LbColors.Paper)
            .border(1.dp, LbColors.Line, RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(
                text = card.regionName,
                color = LbColors.GreenDk,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(LbColors.GreenTint)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            )
            Text(
                text = card.ddayLabel(),
                color = stateColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(stateBackground)
                    .padding(horizontal = 9.dp, vertical = 3.dp),
            )
        }
        Text(
            text = card.title,
            color = LbColors.Ink,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (card.description != null) {
            Text(
                text = card.description,
                color = LbColors.Ink2,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        val endDateLabel = card.formattedEndDate()
        if (endDateLabel != null) {
            Text(
                text = "~ $endDateLabel",
                color = LbColors.GreenDk,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
