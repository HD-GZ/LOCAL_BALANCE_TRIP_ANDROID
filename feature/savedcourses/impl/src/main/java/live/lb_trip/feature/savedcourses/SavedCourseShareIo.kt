package live.lb_trip.feature.savedcourses

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.Density
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

private const val SHARE_CARD_WIDTH_PX = 1080
private val SHARE_CARD_EXPORT_DENSITY = Density(density = 3f, fontScale = 1f)

internal fun shareImageFileName(): String = "lbt_report_${System.currentTimeMillis()}.png"

internal suspend fun renderComposableToBitmap(
    activity: Activity,
    content: @Composable () -> Unit,
): Bitmap = suspendCancellableCoroutine { continuation ->
    val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
    val composeView = ComposeView(activity).apply {
        layoutParams = ViewGroup.LayoutParams(SHARE_CARD_WIDTH_PX, ViewGroup.LayoutParams.WRAP_CONTENT)
        visibility = View.INVISIBLE
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            CompositionLocalProvider(LocalDensity provides SHARE_CARD_EXPORT_DENSITY) {
                content()
            }
        }
    }
    rootView.addView(composeView)
    continuation.invokeOnCancellation { rootView.removeView(composeView) }
    composeView.doOnLayout { view ->
        val width = view.width
        val height = view.height
        if (width <= 0 || height <= 0) {
            rootView.removeView(view)
            continuation.resumeWithException(IllegalStateException("Share card measured ${width}x$height"))
            return@doOnLayout
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        rootView.removeView(view)
        continuation.resume(bitmap)
    }
}

internal suspend fun saveBitmapToGallery(context: Context, bitmap: Bitmap, displayName: String): Boolean =
    withContext(Dispatchers.IO) {
        runCatching {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LocalBalanceTrip")
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return@runCatching false
            resolver.openOutputStream(uri)?.use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
            true
        }.getOrDefault(false)
    }

internal suspend fun shareBitmapImage(context: Context, bitmap: Bitmap, fileName: String): Boolean =
    runCatching {
        val uri = withContext(Dispatchers.IO) {
            val shareDir = File(context.cacheDir, "share_images").apply { mkdirs() }
            val file = File(shareDir, fileName)
            FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
            FileProvider.getUriForFile(context, "${context.packageName}.savedcourses.fileprovider", file)
        }
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(sendIntent, null))
        true
    }.getOrDefault(false)
