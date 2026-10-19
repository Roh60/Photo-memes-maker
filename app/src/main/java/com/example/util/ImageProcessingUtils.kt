package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import com.example.data.model.PhotoAdjustment
import kotlin.math.max
import kotlin.math.min

/**
 * Robust image processing utility class that supports applying Cinematic, Vintage,
 * Black & White, and other creative filters to photos, including intensity adjustment
 * sliders, color grading, adjustments, and vignette effects.
 */
object ImageProcessingUtils {

    // Identity Color Matrix array (4x5)
    private val IDENTITY_MATRIX_ARRAY = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    /**
     * Creates a ColorMatrix for the Cinematic (Teal & Orange) film grade.
     * Features: Warm highlights, cool teal shadows, boosted contrast and saturation.
     */
    fun createCinematicColorMatrix(): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                1.15f, 0.00f, 0.00f, 0f, 12f,
                0.00f, 1.02f, 0.00f, 0f, 4f,
                0.00f, 0.00f, 1.25f, 0f, 18f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
    }

    /**
     * Creates a ColorMatrix for the 1970s Vintage / Analog film aesthetic.
     * Features: Raised black floor (faded shadows), warm yellow tint, reduced blue channel,
     * and subtle desaturation.
     */
    fun createVintageColorMatrix(): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                0.90f, 0.12f, 0.08f, 0f, 22f,
                0.08f, 0.85f, 0.08f, 0f, 16f,
                0.06f, 0.08f, 0.72f, 0f, 28f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
    }

    /**
     * Creates a ColorMatrix for high-contrast classic Black & White / Monochrome.
     * Uses standard perceptual luminance coefficients (0.299 R, 0.587 G, 0.114 B)
     * with expanded tonal contrast.
     */
    fun createBlackAndWhiteColorMatrix(): ColorMatrix {
        val bwMatrix = ColorMatrix()
        bwMatrix.setSaturation(0f)

        // Add subtle contrast boost for crisp, deep black & white
        val c = 1.15f
        val t = 128f * (1f - c)
        val contrastMatrix = ColorMatrix(
            floatArrayOf(
                c, 0f, 0f, 0f, t,
                0f, c, 0f, 0f, t,
                0f, 0f, c, 0f, t,
                0f, 0f, 0f, 1f, 0f
            )
        )
        bwMatrix.postConcat(contrastMatrix)
        return bwMatrix
    }

    /**
     * Creates a ColorMatrix for Sepia antique archival tone.
     */
    fun createSepiaColorMatrix(): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f,     0f,     0f,     1f, 0f
            )
        )
    }

    /**
     * Creates a ColorMatrix for Glow / Dreamy luminous radiance.
     */
    fun createGlowColorMatrix(): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                1.12f, 0.05f, 0.05f, 0f, 20f,
                0.02f, 1.08f, 0.02f, 0f, 18f,
                0.08f, 0.02f, 1.15f, 0f, 24f,
                0f,    0f,    0f,    1f, 0f
            )
        )
    }

    /**
     * Creates a ColorMatrix for Aesthetic soft pastel tones.
     */
    fun createAestheticColorMatrix(): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                1.05f, 0.08f, 0.02f, 0f, 14f,
                0.02f, 1.02f, 0.05f, 0f, 10f,
                0.08f, 0.02f, 1.12f, 0f, 20f,
                0f,    0f,    0f,    1f, 0f
            )
        )
    }

    /**
     * Creates a ColorMatrix for Warm golden sunset glow.
     */
    fun createWarmColorMatrix(): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                1.16f, 0.02f, 0.00f, 0f, 18f,
                0.00f, 1.06f, 0.00f, 0f, 8f,
                0.00f, 0.00f, 0.88f, 0f, -12f,
                0f,    0f,    0f,    1f, 0f
            )
        )
    }

    /**
     * Creates a ColorMatrix for Cool crisp cyan tones.
     */
    fun createCoolColorMatrix(): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                0.88f, 0.00f, 0.00f, 0f, -8f,
                0.00f, 1.04f, 0.02f, 0f, 6f,
                0.02f, 0.00f, 1.22f, 0f, 22f,
                0f,    0f,    0f,    1f, 0f
            )
        )
    }

    /**
     * Creates a ColorMatrix for Festive sparkling celebration.
     */
    fun createFestiveColorMatrix(): ColorMatrix {
        val matrix = ColorMatrix()
        matrix.setSaturation(1.35f)
        val warmTint = ColorMatrix(
            floatArrayOf(
                1.15f, 0.00f, 0.00f, 0f, 16f,
                0.00f, 1.05f, 0.00f, 0f, 10f,
                0.00f, 0.00f, 0.95f, 0f, 5f,
                0f,    0f,    0f,    1f, 0f
            )
        )
        matrix.postConcat(warmTint)
        return matrix
    }

    /**
     * Creates a ColorMatrix for HDR Clarity.
     */
    fun createHdrColorMatrix(): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                1.25f, 0f, 0f, 0f, -10f,
                0f, 1.25f, 0f, 0f, -10f,
                0f, 0f, 1.25f, 0f, -10f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    /**
     * Creates a ColorMatrix for Cyber Neon.
     */
    fun createNeonColorMatrix(): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                1.30f, 0f, 0f, 0f, 25f,
                0f, 0.80f, 0f, 0f, 0f,
                0f, 0f, 1.40f, 0f, 30f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    /**
     * Get target ColorMatrix for preset name.
     */
    fun getPresetColorMatrix(preset: String): ColorMatrix {
        return when (preset.lowercase()) {
            "cinematic" -> createCinematicColorMatrix()
            "vintage", "retro" -> createVintageColorMatrix()
            "bw", "b&w", "grayscale", "black & white" -> createBlackAndWhiteColorMatrix()
            "sepia" -> createSepiaColorMatrix()
            "glow" -> createGlowColorMatrix()
            "aesthetic" -> createAestheticColorMatrix()
            "warm" -> createWarmColorMatrix()
            "cool" -> createCoolColorMatrix()
            "festive" -> createFestiveColorMatrix()
            "hdr" -> createHdrColorMatrix()
            "neon" -> createNeonColorMatrix()
            "vivid" -> {
                val vivid = ColorMatrix()
                vivid.setSaturation(1.45f)
                vivid
            }
            else -> ColorMatrix() // Identity
        }
    }

    /**
     * Interpolates smoothly between the Identity ColorMatrix and a target filter ColorMatrix
     * according to the intensity [0.0f..1.0f].
     *
     * At intensity 0.0f: Returns Identity (original unmodified photo).
     * At intensity 0.5f: Returns 50% subtle blend.
     * At intensity 1.0f: Returns 100% full filter effect.
     */
    fun interpolateColorMatrix(targetMatrix: ColorMatrix, intensity: Float): ColorMatrix {
        val clamped = intensity.coerceIn(0.0f, 1.0f)
        if (clamped >= 0.999f) return ColorMatrix(targetMatrix)
        if (clamped <= 0.001f) return ColorMatrix()

        val targetArray = targetMatrix.array
        val blended = FloatArray(20)
        for (i in 0 until 20) {
            blended[i] = IDENTITY_MATRIX_ARRAY[i] * (1f - clamped) + targetArray[i] * clamped
        }
        return ColorMatrix(blended)
    }

    /**
     * Builds a comprehensive ColorMatrix combining a filter preset with an intensity slider,
     * plus brightness, contrast, saturation, and warmth adjustments.
     */
    fun buildAdjustedColorMatrix(
        preset: String = "none",
        intensity: Float = 1.0f,
        brightness: Float = 0f,
        contrast: Float = 1.0f,
        saturation: Float = 1.0f,
        warmth: Float = 0f
    ): ColorMatrix {
        val result = ColorMatrix()

        // 1. Preset with intensity interpolation
        if (!preset.equals("none", ignoreCase = true) && intensity > 0.01f) {
            val basePresetMatrix = getPresetColorMatrix(preset)
            val intensityMatrix = interpolateColorMatrix(basePresetMatrix, intensity)
            result.postConcat(intensityMatrix)
        }

        // 2. Saturation
        if (saturation != 1.0f && !preset.equals("bw", ignoreCase = true)) {
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

        // 4. Brightness
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

        // 5. Warmth
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
     * Applies a ColorMatrix directly to a bitmap, returning a processed copy.
     */
    fun applyColorMatrix(source: Bitmap, colorMatrix: ColorMatrix): Bitmap {
        if (source.isRecycled) return source
        val width = max(1, source.width)
        val height = max(1, source.height)

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
        canvas.drawBitmap(source, 0f, 0f, paint)
        return output
    }

    /**
     * Applies the Cinematic filter with an intensity adjustment slider [0.0f..1.0f].
     */
    fun applyCinematicFilter(source: Bitmap, intensity: Float = 1.0f): Bitmap {
        val matrix = interpolateColorMatrix(createCinematicColorMatrix(), intensity)
        return applyColorMatrix(source, matrix)
    }

    /**
     * Applies the Vintage filter with an intensity adjustment slider [0.0f..1.0f].
     */
    fun applyVintageFilter(source: Bitmap, intensity: Float = 1.0f): Bitmap {
        val matrix = interpolateColorMatrix(createVintageColorMatrix(), intensity)
        return applyColorMatrix(source, matrix)
    }

    /**
     * Applies the Black & White filter with an intensity adjustment slider [0.0f..1.0f].
     */
    fun applyBlackAndWhiteFilter(source: Bitmap, intensity: Float = 1.0f): Bitmap {
        val matrix = interpolateColorMatrix(createBlackAndWhiteColorMatrix(), intensity)
        return applyColorMatrix(source, matrix)
    }

    /**
     * Applies any named preset with an intensity adjustment slider [0.0f..1.0f].
     */
    fun applyFilterPreset(source: Bitmap, preset: String, intensity: Float = 1.0f): Bitmap {
        val matrix = interpolateColorMatrix(getPresetColorMatrix(preset), intensity)
        return applyColorMatrix(source, matrix)
    }

    /**
     * Applies all adjustments from a [PhotoAdjustment] object (preset, intensity, brightness,
     * contrast, saturation, warmth, vignette, etc.)
     */
    fun applyAdjustments(source: Bitmap, adjustment: PhotoAdjustment): Bitmap {
        if (source.isRecycled) return source

        val matrix = buildAdjustedColorMatrix(
            preset = adjustment.filterPreset,
            intensity = adjustment.filterIntensity,
            brightness = adjustment.brightness,
            contrast = adjustment.contrast,
            saturation = adjustment.saturation,
            warmth = adjustment.warmth
        )

        var processed = applyColorMatrix(source, matrix)

        if (adjustment.vignette > 0.05f) {
            processed = applyVignette(processed, adjustment.vignette)
        }

        return processed
    }

    /**
     * Applies a radial vignette shadow around the perimeter of the image.
     */
    fun applyVignette(source: Bitmap, intensity: Float): Bitmap {
        if (source.isRecycled) return source
        val clampedIntensity = intensity.coerceIn(0f, 1f)
        if (clampedIntensity <= 0.01f) return source

        val width = source.width
        val height = source.height
        val output = source.copy(Bitmap.Config.ARGB_8888, true) ?: return source
        val canvas = Canvas(output)

        val radius = max(width, height) / 1.15f
        val shader = RadialGradient(
            width / 2f,
            height / 2f,
            radius,
            intArrayOf(Color.TRANSPARENT, Color.argb((clampedIntensity * 220).toInt(), 0, 0, 0)),
            floatArrayOf(0.55f, 1.0f),
            Shader.TileMode.CLAMP
        )

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.shader = shader
        }
        canvas.drawRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), paint)
        return output
    }

    /**
     * Generates a high-performance, low-resolution thumbnail of the image for fast preview rendering.
     */
    fun createThumbnail(source: Bitmap, maxDimension: Int = 120): Bitmap {
        if (source.isRecycled) return source
        val srcWidth = source.width
        val srcHeight = source.height
        if (srcWidth <= 0 || srcHeight <= 0) return source

        val maxOriginal = max(srcWidth, srcHeight).toFloat()
        val scale = min(1.0f, maxDimension.toFloat() / maxOriginal)

        val targetWidth = max(1, (srcWidth * scale).toInt())
        val targetHeight = max(1, (srcHeight * scale).toInt())

        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
    }

    /**
     * Blends two bitmaps together with a given alpha opacity [0.0f..1.0f].
     */
    fun blendBitmaps(base: Bitmap, overlay: Bitmap, alpha: Float): Bitmap {
        if (base.isRecycled) return base
        val output = Bitmap.createBitmap(base.width, base.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawBitmap(base, 0f, 0f, null)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            this.alpha = (alpha.coerceIn(0f, 1f) * 255).toInt()
        }
        val srcRect = Rect(0, 0, overlay.width, overlay.height)
        val dstRect = Rect(0, 0, base.width, base.height)
        canvas.drawBitmap(overlay, srcRect, dstRect, paint)
        return output
    }
}
