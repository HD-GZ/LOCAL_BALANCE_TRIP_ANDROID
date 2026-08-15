package live.lb_trip.core.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import coil3.BitmapImage
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException

suspend fun String.getBitmapFromUrl(
    context: Context,
    imageLoader: ImageLoader,
): Bitmap? {
    if (!startsWith("http://") && !startsWith("https://")) return null

    val request = ImageRequest.Builder(context).data(this).build()

    val result = withContext(Dispatchers.IO) {
        imageLoader.execute(request)
    }
    return ((result as? SuccessResult)?.image as? BitmapImage)?.bitmap
}

suspend fun Bitmap.save(context: Context): Uri? = withContext(Dispatchers.IO) {
    val resolver = context.contentResolver

    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "lbt_${System.currentTimeMillis()}.png")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
    }

    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    uri?.let {
        try {
            resolver.openOutputStream(it).use { stream ->
                compress(Bitmap.CompressFormat.PNG, 100, stream!!)
            }
        } catch (_: IOException) {
            resolver.delete(it, null, null)
        }
    }

    return@withContext uri
}

private fun String.fileNameFormatter(compressLevel: ImageCompressLevel): String {
    return when (compressLevel) {
        ImageCompressLevel.JPEG_LOW -> "${this}_${compressLevel}.jpeg"
        ImageCompressLevel.PNG -> "${this}_${compressLevel}.png"
        ImageCompressLevel.HIGH_QUALITY -> "${this}_${compressLevel}.png"
    }
}

suspend fun Bitmap.toContentUri(
    context: Context,
    compressLevel: ImageCompressLevel,
    fileName: String = "cache_image_${System.currentTimeMillis()}"
): Uri? =
    withContext(Dispatchers.IO) {
        val cachePath = File(context.cacheDir, "cache_image")
        if (!cachePath.exists()) {
            cachePath.mkdirs()
        }

        val imageFile = File(cachePath, fileName.fileNameFormatter(compressLevel))
        val authority = "${context.packageName}.fileprovider"

        if (imageFile.exists()) {
            return@withContext FileProvider.getUriForFile(context, authority, imageFile)
        }

        try {
            FileOutputStream(imageFile).use {
                when (compressLevel) {
                    ImageCompressLevel.JPEG_LOW -> compress(Bitmap.CompressFormat.JPEG, 10, it)
                    ImageCompressLevel.PNG -> compress(Bitmap.CompressFormat.PNG, 10, it)
                    ImageCompressLevel.HIGH_QUALITY -> compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            }
        } catch (e: IOException) {
            Log.e("Bitmap", "Failed to convert Bitmap to Uri: $e")
        }

        return@withContext FileProvider.getUriForFile(context, authority, imageFile)
    }

enum class ImageCompressLevel {
    JPEG_LOW,
    PNG,
    HIGH_QUALITY
}
