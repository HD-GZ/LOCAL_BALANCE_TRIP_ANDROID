package live.lb_trip.core.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.EntryPointAccessors
import live.lb_trip.core.di.FacebookAppIdEntryPoint

fun instagramStoryShare(
    context: Context,
    backgroundImage: Uri?,
    stickerImage: Uri?
) {
    val facebookAppId = EntryPointAccessors.fromApplication(
        context.applicationContext,
        FacebookAppIdEntryPoint::class.java,
    ).facebookAppId()

    val intent = Intent("com.instagram.share.ADD_TO_STORY")
    intent.putExtra("source_application", facebookAppId)
    intent.setDataAndType(backgroundImage, "image/png")
    intent.putExtra("interactive_asset_uri", stickerImage)
    intent.putExtra("top_background_color", "#33FF33")
    intent.putExtra("bottom_background_color", "#FF00FF")

    intent.clipData = ClipData.newUri(context.contentResolver, "sticker_image", stickerImage)

    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    context.grantUriPermission("com.instagram.android", stickerImage, Intent.FLAG_GRANT_READ_URI_PERMISSION)

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}
