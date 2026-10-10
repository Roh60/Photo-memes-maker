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
            if (maxDimension < 1) return@withContext null
            try {
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri)?.use { input ->
                    BitmapFactory.decodeStream(input, null, bounds)
                } ?: return@withContext null

                if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@withContext null
                val sampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxDimension)
                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                context.contentResolver.openInputStream(uri)?.use { input ->
                    BitmapFactory.decodeStream(input, null, decodeOptions)
                }
            } catch (_: Exception) {
                null
            } catch (_: OutOfMemoryError) {
                null
            }
        }

    /** Chooses a power-of-two sample size so very large gallery images don't exhaust memory. */
    internal fun calculateInSampleSize(width: Int, height: Int, maxDimension: Int): Int {
        if (width <= 0 || height <= 0 || maxDimension <= 0) return 1
        var sample = 1
        while (width / sample > maxDimension || height / sample > maxDimension) {
            if (sample > Int.MAX_VALUE / 2) break
            sample *= 2
        }
        return sample
    }

    /**
     * Create combined ColorMatrix for Brightness, Contrast, Saturation, Warmth, and Filter Style
     */
    fun createFilterColorMatrix(
        brightness: Float, // -100 to 100 (0 default)
        contrast: Float,   // 0.5 to 2.0 (1.0 default)
        saturation: Float, // 0.0 to 2.5 (1.0 default)
        warmth: Float,     // -50 to 50 (0 default)
        filterType: String // "none", "vivid", "warm", "bw", "sepia", "cinematic", "hdr", "neon", "vintage"
    ): ColorMatrix {
        val result = ColorMatrix()

        // 1. Base filter preset
        when (filterType.lowercase()) {
            "bw" -> {
                val bw = ColorMatrix()
                bw.setSaturation(0f)
                result.postConcat(bw)
            }
            "sepia" -> {
                val sepiaMatrix = ColorMatrix(
                    floatArrayOf(
                        0.393f, 0.769f, 0.189f, 0f, 0f,
                        0.349f, 0.686f, 0.168f, 0f, 0f,
                        0.272f, 0.534f, 0.131f, 0f, 0f,
                        0f,     0f,     0f,     1f, 0f
                    )
                )
                result.postConcat(sepiaMatrix)
            }
            "vivid" -> {
                val vivid = ColorMatrix()
                vivid.setSaturation(1.4f)
                result.postConcat(vivid)
            }
            "warm" -> {
                val warmMatrix = ColorMatrix(
                    floatArrayOf(
                        1.12f, 0f,    0f,    0f, 15f,
                        0f,    1.04f, 0f,    0f, 5f,
                        0f,    0f,    0.88f, 0f, -10f,
                        0f,    0f,    0f,    1f, 0f
                    )
                )
                result.postConcat(warmMatrix)
            }
            "cinematic" -> {
                val cine = ColorMatrix(
                    floatArrayOf(
                        1.1f, 0f, 0f, 0f, 10f,
                        0f, 1.0f, 0f, 0f, 0f,
                        0f, 0f, 1.25f, 0f, 15f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                result.postConcat(cine)
            }
            "hdr" -> {
                val hdr = ColorMatrix(
                    floatArrayOf(
                        1.25f, 0f, 0f, 0f, -10f,
                        0f, 1.25f, 0f, 0f, -10f,
                        0f, 0f, 1.25f, 0f, -10f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                result.postConcat(hdr)
            }
            "neon" -> {
                val neon = ColorMatrix(
                    floatArrayOf(
                        1.3f, 0f, 0f, 0f, 25f,
                        0f, 0.8f, 0f, 0f, 0f,
                        0f, 0f, 1.4f, 0f, 30f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                result.postConcat(neon)
            }
            "vintage" -> {
                val vintage = ColorMatrix(
                    floatArrayOf(
                        0.9f, 0.1f, 0.1f, 0f, 20f,
                        0.1f, 0.8f, 0.1f, 0f, 15f,
                        0.1f, 0.1f, 0.7f, 0f, 30f,
                        0f,   0f,   0f,   1f, 0f
                    )
                )
                result.postConcat(vintage)
            }
        }

        // 2. Saturation
        if (saturation != 1.0f && filterType.lowercase() != "bw") {
            val satMatrix = ColorMatrix()
            satMatrix.setSaturation(saturation)
            result.postConcat(satMatrix)
        }

        // 3. Contrast: c in [0.5..2.0], translate = 128 * (1 - c)
        if (contrast != 1.0f) {
            val t = 128f * (1f - contrast)
            val contrastMatrix = ColorMatrix(
                floatArrayOf(
                    contrast, 0f,       0f,       0f, t,
                    0f,       contrast, 0f,       0f, t,
                    0f,       0f,       contrast, 0f, t,
                    0f,       0f,       0f,       1f, 0f
                )
            )
            result.postConcat(contrastMatrix)
        }

        // 4. Brightness: add offset to RGB
        if (brightness != 0f) {
            val brightMatrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, brightness,
                    0f, 1f, 0f, 0f, brightness,
                    0f, 0f, 1f, 0f, brightness,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            result.postConcat(brightMatrix)
        }

        // 5. Warmth: warm boosts red & green, cools blue; cool boosts blue
        if (warmth != 0f) {
            val redShift = warmth * 0.8f
            val greenShift = warmth * 0.3f
            val blueShift = -warmth * 0.8f
            val warmMatrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, redShift,
                    0f, 1f, 0f, 0f, greenShift,
                    0f, 0f, 1f, 0f, blueShift,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            result.postConcat(warmMatrix)
        }

        return result
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
        // Let Bitmap.createBitmap calculate the expanded bounds for rotations (e.g. 90°).
        // The old fixed-size canvas clipped corners when rotating non-square photos.
        val transform = Matrix().apply {
            if (flipH) postScale(-1f, 1f, source.width / 2f, source.height / 2f)
            if (flipV) postScale(1f, -1f, source.width / 2f, source.height / 2f)
            if (rotationDegrees % 360f != 0f) {
                postRotate(rotationDegrees, source.width / 2f, source.height / 2f)
            }
        }
        val transformed = if (flipH || flipV || rotationDegrees % 360f != 0f) {
            Bitmap.createBitmap(source, 0, 0, source.width, source.height, transform, true)
        } else {
            source
        }
        val output = Bitmap.createBitmap(transformed.width, transformed.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        if (colorMatrix != null) paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(transformed, 0f, 0f, paint)
        if (transformed !== source && !transformed.isRecycled) transformed.recycle()
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
        if (bitmap.isRecycled || bitmap.width <= 0 || bitmap.height <= 0) return@withContext null
        val format = if (isPng) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        val mimeType = if (isPng) "image/png" else "image/jpeg"
        val extension = if (isPng) ".png" else ".jpg"
        val safeTitle = title.trim()
            .replace(Regex("[^\\p{L}\\p{N} _.-]"), "_")
            .ifBlank { "PhotoMemes_${System.currentTimeMillis()}" }
            .take(80)
        val fileName = "$safeTitle$extension"

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
            val stream = resolver.openOutputStream(uri) ?: throw java.io.IOException("Could not open image output")
            stream.use {
                if (!bitmap.compress(format, if (isPng) 100 else 95, it)) {
                    throw java.io.IOException("Image encoding failed")
                }
                it.flush()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val published = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }
                if (resolver.update(uri, published, null, null) <= 0) {
                    throw java.io.IOException("Could not finish saving image")
                }
            }
            uri
        } catch (_: Exception) {
            runCatching { resolver.delete(uri, null, null) }
            null
        } catch (_: OutOfMemoryError) {
            runCatching { resolver.delete(uri, null, null) }
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
        val dir = File(context.filesDir, "creations").apply {
            if (!exists() && !mkdirs()) throw java.io.IOException("Could not create creations folder")
        }
        val safeName = File(filename).name.ifBlank { "creation_${System.currentTimeMillis()}.png" }
        val file = File(dir, safeName)
        FileOutputStream(file).use { out ->
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                throw java.io.IOException("Could not encode image")
            }
            out.flush()
        }
        file
    }

    /**
     * Share image via Android native Intent chooser using FileProvider
     */
    fun shareBitmap(context: Context, bitmap: Bitmap, shareText: String = "Created with Photo & Memes Maker") {
        try {
            if (bitmap.isRecycled || bitmap.width <= 0 || bitmap.height <= 0) return
            val cachePath = File(context.cacheDir, "shared_images").apply { if (!exists()) mkdirs() }
            // Prevent temporary exports from accumulating indefinitely.
            cachePath.listFiles()?.filter { System.currentTimeMillis() - it.lastModified() > 24L * 60 * 60 * 1000 }
                ?.forEach { it.delete() }
            val file = File(cachePath, "share_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)) {
                    throw java.io.IOException("Could not encode image for sharing")
                }
                out.flush()
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
