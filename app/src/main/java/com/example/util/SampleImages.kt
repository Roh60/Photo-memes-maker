package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface

object SampleImages {

    /**
     * Create a vivid scenic or festive sample photo bitmap
     */
    fun createScenicSample(width: Int = 1080, height: Int = 1080, title: String = "Celebration", primaryColor: Int = 0xFFFF5722.toInt(), secondaryColor: Int = 0xFFFFC107.toInt()): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Gradient background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(primaryColor, secondaryColor, 0xFF4A148C.toInt()),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Decorative background geometric shapes
        val shapePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(40, 255, 255, 255)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width * 0.2f, height * 0.25f, width * 0.35f, shapePaint)
        canvas.drawCircle(width * 0.85f, height * 0.75f, width * 0.4f, shapePaint)

        // Sun / Moon / Diya glow
        val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 255, 235, 59)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width * 0.5f, height * 0.45f, width * 0.18f, sunPaint)

        // Sparkle stars
        val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        for (i in 0 until 16) {
            val sx = (Math.sin(i * 1.7) * 0.4 + 0.5) * width
            val sy = (Math.cos(i * 2.3) * 0.4 + 0.5) * height
            canvas.drawCircle(sx.toFloat(), sy.toFloat(), 6f + (i % 4) * 3f, starPaint)
        }

        // Title text banner in center
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = width * 0.07f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(16f, 0f, 6f, Color.argb(180, 0, 0, 0))
        }
        canvas.drawText(title, width * 0.5f, height * 0.82f, textPaint)

        return bitmap
    }

    /**
     * Create portrait sample photo
     */
    fun createPortraitSample(width: Int = 1080, height: Int = 1080, label: String = "Smile"): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val gradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(0xFF1E3A8A.toInt(), 0xFF3B82F6.toInt(), 0xFF93C5FD.toInt()),
            null,
            Shader.TileMode.CLAMP
        )
        val bg = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = gradient }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bg)

        // Silhouette person / avatar
        val avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(230, 255, 255, 255)
            style = Paint.Style.FILL
        }
        // Head
        canvas.drawCircle(width * 0.5f, height * 0.4f, width * 0.2f, avatarPaint)
        // Shoulders
        val shoulderRect = RectF(width * 0.15f, height * 0.65f, width * 0.85f, height * 1.15f)
        canvas.drawRoundRect(shoulderRect, width * 0.3f, width * 0.3f, avatarPaint)

        // Label
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = width * 0.08f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(12f, 0f, 4f, Color.BLACK)
        }
        canvas.drawText(label, width * 0.5f, height * 0.2f, textPaint)

        return bitmap
    }

    /**
     * Create funny meme character graphic for meme templates
     */
    fun createMemeGraphic(width: Int = 800, height: Int = 800, expression: String = "smirk"): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Warm yellow-orange comic background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = when (expression) {
                "shock" -> 0xFFFEF08A.toInt()
                "smirk" -> 0xFFFED7AA.toInt()
                "crying" -> 0xFFBFDBFE.toInt()
                "smart" -> 0xFFBBF7D0.toInt()
                else -> 0xFFE2E8F0.toInt()
            }
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Comic face outline
        val facePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFBBF24.toInt()
            style = Paint.Style.FILL
        }
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 14f
        }
        canvas.drawCircle(width * 0.5f, height * 0.5f, width * 0.38f, facePaint)
        canvas.drawCircle(width * 0.5f, height * 0.5f, width * 0.38f, strokePaint)

        val featurePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        when (expression) {
            "smirk" -> {
                // Sunglasses
                val glasses = RectF(width * 0.24f, height * 0.36f, width * 0.48f, height * 0.48f)
                val glasses2 = RectF(width * 0.52f, height * 0.36f, width * 0.76f, height * 0.48f)
                canvas.drawRoundRect(glasses, 18f, 18f, featurePaint)
                canvas.drawRoundRect(glasses2, 18f, 18f, featurePaint)
                canvas.drawLine(width * 0.48f, height * 0.42f, width * 0.52f, height * 0.42f, strokePaint)

                // Smirk grin
                val mouthPath = Path().apply {
                    moveTo(width * 0.35f, height * 0.65f)
                    quadTo(width * 0.55f, height * 0.76f, width * 0.72f, height * 0.60f)
                }
                canvas.drawPath(mouthPath, strokePaint)
            }
            "shock" -> {
                // Big round eyes
                canvas.drawCircle(width * 0.38f, height * 0.42f, 32f, featurePaint)
                canvas.drawCircle(width * 0.62f, height * 0.42f, 32f, featurePaint)
                // O mouth
                val mouth = RectF(width * 0.42f, height * 0.58f, width * 0.58f, height * 0.78f)
                canvas.drawOval(mouth, featurePaint)
            }
            "smart" -> {
                // Wink eye + open eye
                canvas.drawCircle(width * 0.38f, height * 0.42f, 26f, featurePaint)
                canvas.drawLine(width * 0.55f, height * 0.42f, width * 0.72f, height * 0.42f, strokePaint)
                // Finger to temple indicator
                val indexFinger = RectF(width * 0.68f, height * 0.28f, width * 0.78f, height * 0.48f)
                canvas.drawRoundRect(indexFinger, 15f, 15f, featurePaint)
                // Grin
                val mouth = Path().apply {
                    moveTo(width * 0.38f, height * 0.62f)
                    quadTo(width * 0.52f, height * 0.72f, width * 0.66f, height * 0.62f)
                }
                canvas.drawPath(mouth, strokePaint)
            }
            else -> {
                // Normal smile
                canvas.drawCircle(width * 0.38f, height * 0.42f, 24f, featurePaint)
                canvas.drawCircle(width * 0.62f, height * 0.42f, 24f, featurePaint)
                val smile = Path().apply {
                    moveTo(width * 0.35f, height * 0.60f)
                    quadTo(width * 0.50f, height * 0.74f, width * 0.65f, height * 0.60f)
                }
                canvas.drawPath(smile, strokePaint)
            }
        }

        return bitmap
    }
}
