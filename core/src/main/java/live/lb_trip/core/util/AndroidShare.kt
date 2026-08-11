package live.lb_trip.core.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import live.lb_trip.core.BuildConfig

fun shareReport(
    context: Context,
    username: String,
    imageUrl: Uri?,
    shareUrl: String? = BuildConfig.WEB_URL
) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareUrl)

        putExtra(Intent.EXTRA_TITLE, "${username}님의 여행 기록을 지금 확인해보세요!")
        imageUrl?.let {
            data = it
            clipData = ClipData.newUri(context.contentResolver, "report_image", it)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
