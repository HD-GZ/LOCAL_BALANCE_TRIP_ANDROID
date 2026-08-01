package live.lb_trip.feature.settings

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.chipColors
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbTopBar

@Composable
fun LicensesScreen(@RawRes librariesRawResId: Int, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val libraries by produceLibraries(librariesRawResId)

    Scaffold(
        modifier = modifier,
        topBar = {
            LbTopBar(
                onBackClick = onBack,
                backContentDescription = stringResource(R.string.licenses_back_cd),
                title = stringResource(R.string.licenses_title),
                containerColor = LbColors.Paper,
            )
        },
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
