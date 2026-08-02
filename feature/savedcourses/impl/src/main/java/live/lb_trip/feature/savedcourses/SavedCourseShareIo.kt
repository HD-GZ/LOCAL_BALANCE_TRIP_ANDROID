package live.lb_trip.feature.savedcourses

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.content.FileProvider
import coil3.BitmapImage
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal fun shareImageFileName(): String = "lbt_report_${System.currentTimeMillis()}.png"

internal suspend fun downloadReportImageBitmap(imageLoader: ImageLoader, context: Context, url: String): Bitmap? {
    val request = ImageRequest.Builder(context).data(url).allowHardware(false).build()
    val result = imageLoader.execute(request)
    return ((result as? SuccessResult)?.image as? BitmapImage)?.bitmap
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
