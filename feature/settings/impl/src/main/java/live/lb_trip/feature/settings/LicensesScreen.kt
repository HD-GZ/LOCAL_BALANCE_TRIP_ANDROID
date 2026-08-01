package live.lb_trip.feature.settings

import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.chipColors
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR

@Composable
fun LicensesScreen(@RawRes librariesRawResId: Int, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val libraries by produceLibraries(librariesRawResId)

    Scaffold(
        modifier = modifier,
        topBar = { LicensesAppBar(onBackClick = onBack) },
        containerColor = LbColors.Paper,
    ) { innerPadding ->
        LibrariesContainer(
            libraries = libraries,
            modifier = Modifier.padding(innerPadding),
            colors = LibraryDefaults.libraryColors(
                libraryBackgroundColor = LbColors.Paper,
                licenseChipColors = LibraryDefaults.chipColors(
                    containerColor = LbColors.Green,
                    contentColor = LbColors.Paper
                ),
                dialogConfirmButtonColor = LbColors.Green,
            ),
        )
    }
}

@Composable
private fun LicensesAppBar(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(LbColors.Paper)
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(54.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(40.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_back),
                contentDescription = stringResource(R.string.licenses_back_cd),
                tint = Color.Unspecified,
            )
        }
        Text(
            text = stringResource(R.string.licenses_title),
            color = LbColors.Ink,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 56.dp),
        )
    }
}
