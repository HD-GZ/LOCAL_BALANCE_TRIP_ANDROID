package live.lb_trip.core.contract

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import dagger.hilt.android.EntryPointAccessors
import live.lb_trip.core.di.FacebookAppIdEntryPoint

class InstagramStoryContract : ActivityResultContract<InstagramStoryData, String?>() {
    override fun createIntent(
        context: Context,
        input: InstagramStoryData
    ): Intent {
        val facebookAppId = EntryPointAccessors.fromApplication(
            context.applicationContext,
            FacebookAppIdEntryPoint::class.java,
        ).facebookAppId()

        context.grantUriPermission("com.instagram.android", input.stickerImage, Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val intent = Intent("com.instagram.share.ADD_TO_STORY")
        intent.putExtra("source_application", facebookAppId)
        intent.setDataAndType(input.backgroundImage, "image/png")
        intent.putExtra("interactive_asset_uri", input.stickerImage)
        intent.putExtra("top_background_color", "#33FF33")
        intent.putExtra("bottom_background_color", "#FF00FF")

        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)


        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return null
    }
}


data class InstagramStoryData(
    val backgroundImage: Uri?,
    val stickerImage: Uri?
)
