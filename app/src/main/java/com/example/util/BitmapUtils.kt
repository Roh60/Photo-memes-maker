package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

object BitmapUtils {

    /**
     * Decode a bitmap from Android URI safely with downsampling if too large
     */
    suspend fun decodeUriToBitmap(context: Context, uri: Uri, maxDimension: Int = 1920): Bitmap? =
        withContext(Dispatchers.IO) {
            try {
                var input: InputStream? = context.contentResolver.openInputStream(uri) ?: return@withContext null
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(input, null, options)
                input?.close()

                var inSampleSize = 1
                val width = options.outWidth
                val height = options.outHeight
                if (width > maxDimension || height > maxDimension) {
                    val halfWidth = width / 2
                    val halfHeight = height / 2
                    while ((halfWidth / inSampleSize) >= maxDimension && (halfHeight / inSampleSize) >= maxDimension) {
                        inSampleSize *= 2
                    }
                }

                val decodeOptions = BitmapFactory.Options().apply {
                    this.inSampleSize = inSampleSize
                    inMutable = true
                }
                input = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(input, null, decodeOptions)
                input?.close()
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    /**
     * Create combined ColorMatrix for Brightness, Contrast, Saturation, Warmth, and Filter Style
     */
    /**
     * Create combined ColorMatrix for Brightness, Contrast, Saturation, Warmth, and Filter Style with Intensity
     */
    fun createFilterColorMatrix(
        brightness: Float, // -100 to 100 (0 default)
        contrast: Float,   // 0.5 to 2.0 (1.0 default)
        saturation: Float, // 0.0 to 2.5 (1.0 default)
        warmth: Float,     // -50 to 50 (0 default)
        filterType: String, // "none", "vivid", "warm", "bw", "sepia", "cinematic", "hdr", "neon", "vintage"
        filterIntensity: Float = 1.0f // 0.0f to 1.0f intensity
    ): ColorMatrix {
        return ImageProcessingUtils.buildAdjustedColorMatrix(
            preset = filterType,
            intensity = filterIntensity,
            brightness = brightness,
            contrast = contrast,
            saturation = saturation,
            warmth = warmth
        )
    }

    /**
     * Apply filter matrix, rotation, and flips to a source bitmap
     */
    fun processBitmap(
        source: Bitmap,
        colorMatrix: ColorMatrix?,
        rotationDegrees: Float = 0f,
        flipH: Boolean = false,
        flipV: Boolean = false
    ): Bitmap {
        val matrix = Matrix()
        if (flipH) matrix.postScale(-1f, 1f, source.width / 2f, source.height / 2f)
        if (flipV) matrix.postScale(1f, -1f, source.width / 2f, source.height / 2f)
        if (rotationDegrees % 360 != 0f) matrix.postRotate(rotationDegrees, source.width / 2f, source.height / 2f)

        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        if (colorMatrix != null) {
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
        canvas.drawBitmap(source, matrix, paint)
        return output
    }

    /**
     * Save bitmap to device Gallery (MediaStore) and returns its content URI
     */
    suspend fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        title: String = "PhotoMemes_${System.currentTimeMillis()}",
        isPng: Boolean = false
    ): Uri? = withContext(Dispatchers.IO) {
        val format = if (isPng) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        val mimeType = if (isPng) "image/png" else "image/jpeg"
        val extension = if (isPng) ".png" else ".jpg"
        val fileName = "$title$extension"

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PhotoMemesMaker")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return@withContext null

        try {
            resolver.openOutputStream(uri)?.use { stream ->
                bitmap.compress(format, 95, stream)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Save bitmap locally into app private filesDir (for drafts & creations)
     */
    suspend fun saveBitmapLocally(
        context: Context,
        bitmap: Bitmap,
        filename: String = "creation_${System.currentTimeMillis()}.png"
    ): File = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, "creations").apply { if (!exists()) mkdirs() }
        val file = File(dir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        file
    }

    /**
     * Share image via Android native Intent chooser using FileProvider
     */
    fun shareBitmap(context: Context, bitmap: Bitmap, shareText: String = "Created with Photo & Memes Maker") {
        try {
            val cachePath = File(context.cacheDir, "shared_images").apply { if (!exists()) mkdirs() }
            val file = File(cachePath, "share_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, shareText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Image via"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
