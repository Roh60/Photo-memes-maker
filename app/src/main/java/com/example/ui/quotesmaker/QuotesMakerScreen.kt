package com.example.ui.quotesmaker

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuoteBackgroundPreset
import com.example.data.model.QuoteCategory
import com.example.data.model.QuoteFrameStyle
import com.example.data.model.QuoteItem
import com.example.data.model.QuoteRepository
import com.example.data.repository.CreationsRepository
import com.example.ui.components.PaletteColorPicker
import com.example.ui.components.StudioTopBar
import com.example.ui.components.TextOverlayUtils
import com.example.ui.components.ValueSliderRow
import com.example.util.BitmapUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesMakerScreen(
    initialQuoteId: String? = null,
    initialFrameStyle: QuoteFrameStyle? = null,
    initialTemplateId: String? = null,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { CreationsRepository(context) }

    // Initial template resolution
    val initialTemplate = remember(initialTemplateId) {
        if (initialTemplateId != null) QuoteRepository.getTemplateById(initialTemplateId) else null
    }

    // Initial item resolution
    val initialItem = remember(initialQuoteId) {
        if (initialQuoteId != null) QuoteRepository.getQuoteById(initialQuoteId) else null
    } ?: QuoteRepository.getQuotesByCategory(QuoteCategory.MOTIVATIONAL).first()

    // Quote Content State (initialized with template if provided, else item)
    var quoteText by remember { mutableStateOf(initialTemplate?.quote ?: initialItem.quote) }
    var authorText by remember { mutableStateOf(initialTemplate?.author ?: initialItem.author) }
    var selectedCategory by remember { mutableStateOf(initialTemplate?.category ?: initialItem.category) }
    var frameStyle by remember { mutableStateOf(initialTemplate?.frameStyle ?: initialFrameStyle ?: initialItem.suggestedFrame) }
    var badgeIcon by remember { mutableStateOf(initialTemplate?.badgeIcon ?: initialItem.badgeIcon) }

    // Styling State
    var fontId by remember { mutableStateOf(initialTemplate?.fontId ?: "Rozha") } // Editorial Serif default
    var fontSizeSp by remember { mutableFloatStateOf(initialTemplate?.fontSizeSp ?: 24f) }
    var textColor by remember { mutableStateOf(initialTemplate?.textColor ?: 0xFFFFFFFF) }
    var authorColor by remember { mutableStateOf(initialTemplate?.authorColor ?: 0xFFE2E8F0) }
    var borderColor by remember { mutableStateOf(initialTemplate?.borderColor ?: 0xFFD4AF37) } // Gold accent
    var borderWidthDp by remember { mutableFloatStateOf(initialTemplate?.borderWidthDp ?: 2.5f) }
    var cornerRadiusDp by remember { mutableFloatStateOf(initialTemplate?.cornerRadiusDp ?: 16f) }
    var frameInsetDp by remember { mutableFloatStateOf(initialTemplate?.frameInsetDp ?: 16f) }
    var textAlignment by remember { mutableStateOf(initialTemplate?.textAlign ?: "center") }
    var showQuoteMarks by remember { mutableStateOf(initialTemplate?.showQuoteMarks ?: true) }
    var showBadgeIcon by remember { mutableStateOf(initialTemplate?.showBadgeIcon ?: true) }
    var hasShadow by remember { mutableStateOf(initialTemplate?.hasShadow ?: true) }
    var shadowColor by remember { mutableStateOf(0xCC000000) }
    var shadowRadius by remember { mutableFloatStateOf(8f) }
    var shadowDx by remember { mutableFloatStateOf(2f) }
    var shadowDy by remember { mutableFloatStateOf(3f) }

    // Background State
    var backgroundPresetId by remember { mutableStateOf(initialTemplate?.backgroundPresetId ?: initialItem.suggestedBgId) }
    var customBgBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var bgDimAlpha by remember { mutableFloatStateOf(0.4f) }

    // Aspect Ratio: "1:1" (Square), "9:16" (Story/Reel), "4:5" (Portrait)
    var currentAspectRatio by remember { mutableStateOf(initialTemplate?.defaultAspectRatio ?: "1:1") }

    // Bottom Tools Navigation Tab: 0: Templates, 1: Quotes & Facts, 2: Frames, 3: Background, 4: Typography
    var selectedToolTab by remember { mutableIntStateOf(if (initialTemplateId != null) 0 else 0) }
    var isSaving by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }

    // Gallery Picker for Custom Background Photo
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val bmp = BitmapUtils.decodeUriToBitmap(context, uri)
                if (bmp != null) {
                    customBgBitmap = bmp
                    Toast.makeText(context, "Custom background photo loaded!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // High quality export bitmap generator (1080px resolution)
    fun generateQuoteExport(): Bitmap {
        val (width, height) = when (currentAspectRatio) {
            "9:16" -> Pair(1080, 1920)
            "4:5" -> Pair(1080, 1350)
            else -> Pair(1080, 1080)
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // 1. Draw Background
        val bgBmp = customBgBitmap
        if (bgBmp != null) {
            val srcRect = Rect(0, 0, bgBmp.width, bgBmp.height)
            val dstRect = Rect(0, 0, width, height)
            canvas.drawBitmap(bgBmp, srcRect, dstRect, null)

            // Dim overlay
            if (bgDimAlpha > 0f) {
                val dimPaint = Paint().apply {
                    color = android.graphics.Color.BLACK
                    alpha = (bgDimAlpha * 255).toInt().coerceIn(0, 255)
                }
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)
            }
        } else {
            // Draw Gradient
            val preset = QuoteRepository.BACKGROUND_PRESETS.firstOrNull { it.id == backgroundPresetId }
                ?: QuoteRepository.BACKGROUND_PRESETS.first()
            val colors = preset.colors.map { it.toInt() }.toIntArray()
            val shader = android.graphics.LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                colors, null, android.graphics.Shader.TileMode.CLAMP
            )
            val bgPaint = Paint().apply { this.shader = shader }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        }

        // 2. Draw Frame Style
        val insetPx = (frameInsetDp / 360f) * width
        val strokeWidthPx = (borderWidthDp / 360f) * width
        val cornerRadiusPx = (cornerRadiusDp / 360f) * width
        val frameRect = RectF(insetPx, insetPx, width - insetPx, height - insetPx)

        val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = strokeWidthPx
            color = borderColor.toInt()
        }

        when (frameStyle) {
            QuoteFrameStyle.MINIMAL_BORDER -> {
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, framePaint)
                // Draw decorative corner notches
                val notchLen = width * 0.04f
                val notchPaint = Paint(framePaint).apply { strokeWidth = strokeWidthPx * 1.5f }
                canvas.drawLine(frameRect.left, frameRect.top, frameRect.left + notchLen, frameRect.top, notchPaint)
                canvas.drawLine(frameRect.left, frameRect.top, frameRect.left, frameRect.top + notchLen, notchPaint)
                canvas.drawLine(frameRect.right, frameRect.top, frameRect.right - notchLen, frameRect.top, notchPaint)
                canvas.drawLine(frameRect.right, frameRect.top, frameRect.right, frameRect.top + notchLen, notchPaint)
                canvas.drawLine(frameRect.left, frameRect.bottom, frameRect.left + notchLen, frameRect.bottom, notchPaint)
                canvas.drawLine(frameRect.left, frameRect.bottom, frameRect.left, frameRect.bottom - notchLen, notchPaint)
                canvas.drawLine(frameRect.right, frameRect.bottom, frameRect.right - notchLen, frameRect.bottom, notchPaint)
                canvas.drawLine(frameRect.right, frameRect.bottom, frameRect.right, frameRect.bottom - notchLen, notchPaint)
            }

            QuoteFrameStyle.GOLD_LUXURY -> {
                // Outer border
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, framePaint)
                // Inner border
                val innerGap = width * 0.015f
                val innerRect = RectF(frameRect.left + innerGap, frameRect.top + innerGap, frameRect.right - innerGap, frameRect.bottom - innerGap)
                val innerPaint = Paint(framePaint).apply { strokeWidth = strokeWidthPx * 0.6f }
                canvas.drawRoundRect(innerRect, (cornerRadiusPx - innerGap).coerceAtLeast(0f), (cornerRadiusPx - innerGap).coerceAtLeast(0f), innerPaint)
                // Gold Corner Diamonds
                val diamondPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = borderColor.toInt()
                }
                val dSize = width * 0.018f
                listOf(
                    Pair(frameRect.left, frameRect.top),
                    Pair(frameRect.right, frameRect.top),
                    Pair(frameRect.left, frameRect.bottom),
                    Pair(frameRect.right, frameRect.bottom)
                ).forEach { (cx, cy) ->
                    val path = android.graphics.Path().apply {
                        moveTo(cx, cy - dSize)
                        lineTo(cx + dSize, cy)
                        lineTo(cx, cy + dSize)
                        lineTo(cx - dSize, cy)
                        close()
                    }
                    canvas.drawPath(path, diamondPaint)
                }
            }

            QuoteFrameStyle.NEON_CYBER -> {
                val glowPaint = Paint(framePaint).apply {
                    setShadowLayer(16f, 0f, 0f, borderColor.toInt())
                }
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, glowPaint)
                // Tech brackets
                val bLen = width * 0.08f
                val bPaint = Paint(framePaint).apply { strokeWidth = strokeWidthPx * 2f }
                canvas.drawLine(frameRect.left, frameRect.top + bLen, frameRect.left, frameRect.top, bPaint)
                canvas.drawLine(frameRect.left, frameRect.top, frameRect.left + bLen, frameRect.top, bPaint)
                canvas.drawLine(frameRect.right - bLen, frameRect.bottom, frameRect.right, frameRect.bottom, bPaint)
                canvas.drawLine(frameRect.right, frameRect.bottom - bLen, frameRect.right, frameRect.bottom, bPaint)
            }

            QuoteFrameStyle.TYPEWRITER_VINTAGE -> {
                // Vintage parchment card
                val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = 0x1AFFFFFF // 10% white tint
                }
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, cardPaint)
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, framePaint)
                // Decorative divider rule at bottom
                val divY = frameRect.bottom - (height * 0.16f)
                canvas.drawLine(frameRect.left + (width * 0.12f), divY, frameRect.right - (width * 0.12f), divY, framePaint)
            }

            QuoteFrameStyle.CINEMATIC_LETTERBOX -> {
                // Top and bottom black cinematic bars
                val barH = height * 0.12f
                val barPaint = Paint().apply { color = android.graphics.Color.BLACK }
                canvas.drawRect(0f, 0f, width.toFloat(), barH, barPaint)
                canvas.drawRect(0f, height - barH, width.toFloat(), height.toFloat(), barPaint)
                // Thin golden rule
                val linePaint = Paint(framePaint).apply { strokeWidth = 3f }
                canvas.drawLine(0f, barH, width.toFloat(), barH, linePaint)
                canvas.drawLine(0f, height - barH, width.toFloat(), height - barH, linePaint)
            }

            QuoteFrameStyle.GLASS_CARD -> {
                val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = 0x2EFFFFFF
                }
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, cardPaint)
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, framePaint)
            }

            QuoteFrameStyle.EDITORIAL_MAGAZINE -> {
                // Left bold stripe
                val stripePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = borderColor.toInt()
                }
                val stripeW = width * 0.025f
                canvas.drawRoundRect(
                    RectF(frameRect.left, frameRect.top, frameRect.left + stripeW, frameRect.bottom),
                    stripeW / 2f, stripeW / 2f, stripePaint
                )
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, framePaint)
            }

            QuoteFrameStyle.POLAROID_PIN -> {
                val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = 0x22FFFFFF
                }
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, cardPaint)
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, framePaint)
                // Top tape badge
                val tapeW = width * 0.22f
                val tapeH = height * 0.035f
                val tapePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = borderColor.toInt()
                    alpha = 210
                }
                canvas.drawRoundRect(
                    RectF((width - tapeW) / 2f, frameRect.top - (tapeH / 2f), (width + tapeW) / 2f, frameRect.top + (tapeH / 2f)),
                    8f, 8f, tapePaint
                )
            }

            QuoteFrameStyle.DOUBLE_RULE_CLASSIC -> {
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, framePaint)
                val gap = width * 0.012f
                val innerR = RectF(frameRect.left + gap, frameRect.top + gap, frameRect.right - gap, frameRect.bottom - gap)
                val innerP = Paint(framePaint).apply { strokeWidth = strokeWidthPx * 0.7f }
                canvas.drawRoundRect(innerR, (cornerRadiusPx - gap).coerceAtLeast(0f), (cornerRadiusPx - gap).coerceAtLeast(0f), innerP)
            }

            QuoteFrameStyle.ROUNDED_PILL -> {
                canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, framePaint)
                // Top pill category tag
                val pillW = width * 0.38f
                val pillH = height * 0.045f
                val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = borderColor.toInt()
                }
                val pillRect = RectF((width - pillW) / 2f, frameRect.top - (pillH / 2f), (width + pillW) / 2f, frameRect.top + (pillH / 2f))
                canvas.drawRoundRect(pillRect, pillH / 2f, pillH / 2f, pillPaint)
            }
        }

        // 3. Draw Header Badge / Quote Mark
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor.toInt()
            textSize = width * (fontSizeSp / 350f)
            typeface = TextOverlayUtils.resolveTypeface(context, fontId, isBold = true, isItalic = false)
            if (hasShadow) {
                setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor.toInt())
            }
        }

        val centerX = width * 0.5f
        var curY = height * 0.28f

        if (showBadgeIcon && badgeIcon.isNotEmpty()) {
            val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = width * 0.08f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(badgeIcon, centerX, curY, badgePaint)
            curY += width * 0.09f
        }

        if (showQuoteMarks) {
            val quoteMarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = borderColor.toInt()
                textSize = width * 0.12f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                alpha = 220
            }
            canvas.drawText("“", centerX, curY, quoteMarkPaint)
            curY += width * 0.05f
        }

        // 4. Draw Wrapped Quote Text
        val maxTextWidth = width * 0.76f
        val align = when (textAlignment) {
            "left" -> Paint.Align.LEFT
            "right" -> Paint.Align.RIGHT
            else -> Paint.Align.CENTER
        }
        textPaint.textAlign = align

        val quoteX = when (textAlignment) {
            "left" -> width * 0.14f
            "right" -> width * 0.86f
            else -> centerX
        }

        // Line wrapping logic
        val words = quoteText.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (textPaint.measureText(testLine) <= maxTextWidth) {
                currentLine.append(if (currentLine.isEmpty()) word else " $word")
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine.toString())

        val lineHeight = textPaint.textSize * 1.35f
        for (line in lines) {
            canvas.drawText(line, quoteX, curY, textPaint)
            curY += lineHeight
        }

        if (showQuoteMarks) {
            curY += width * 0.02f
            val closeMarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = borderColor.toInt()
                textSize = width * 0.12f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                alpha = 220
            }
            canvas.drawText("”", centerX, curY, closeMarkPaint)
            curY += width * 0.04f
        }

        // 5. Draw Author Tag
        if (authorText.isNotBlank()) {
            curY += width * 0.04f
            val authorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = authorColor.toInt()
                textSize = textPaint.textSize * 0.65f
                typeface = TextOverlayUtils.resolveTypeface(context, "Poppins", isBold = true, isItalic = true)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("— $authorText", centerX, curY, authorPaint)
        }

        return bitmap
    }

    Scaffold(
        topBar = {
            StudioTopBar(
                title = "Quotes Frame Studio",
                onBack = onNavigateBack,
                actions = {
                    // Random quote shuffle
                    IconButton(
                        onClick = {
                            val next = QuoteRepository.getRandomQuote()
                            quoteText = next.quote
                            authorText = next.author
                            selectedCategory = next.category
                            badgeIcon = next.badgeIcon
                            frameStyle = next.suggestedFrame
                            backgroundPresetId = next.suggestedBgId
                            Toast.makeText(context, "Loaded: ${next.category.displayName}", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("quote_shuffle_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Shuffle Quote", tint = MaterialTheme.colorScheme.primary)
                    }

                    // Copy quote text
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Quote", "\"$quoteText\"\n— $authorText")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Quote copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("quote_copy_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Quote")
                    }

                    // Share Quote image
                    IconButton(
                        onClick = {
                            scope.launch {
                                val bmp = generateQuoteExport()
                                BitmapUtils.shareBitmap(context, bmp, "Quote by $authorText #QuotesMaker")
                            }
                        },
                        modifier = Modifier.testTag("quote_share_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Quote Image")
                    }

                    // Save to My Creations
                    IconButton(
                        onClick = {
                            scope.launch {
                                isSaving = true
                                val bmp = generateQuoteExport()
                                repository.saveCreation(bmp, quoteText.take(24), "quote")
                                isSaving = false
                                Toast.makeText(context, "Quote Saved to My Creations! ✨", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("quote_save_btn")
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Save Quote")
                        }
                    }

                    // Export HD / Gallery Sheet Action
                    Button(
                        onClick = { showExportSheet = true },
                        modifier = Modifier.testTag("quote_export_action_btn"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Interactive Frame Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val chosenFontFamily = remember(fontId) {
                    TextOverlayUtils.resolveFontFamily(fontId)
                }

                val currentPreset = remember(backgroundPresetId) {
                    QuoteRepository.BACKGROUND_PRESETS.firstOrNull { it.id == backgroundPresetId }
                        ?: QuoteRepository.BACKGROUND_PRESETS.first()
                }

                val canvasAspect = when (currentAspectRatio) {
                    "9:16" -> 9f / 16f
                    "4:5" -> 4f / 5f
                    else -> 1f
                }

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(canvasAspect)
                        .testTag("live_quote_canvas_card"),
                    shape = RoundedCornerShape(cornerRadiusDp.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background Layer
                        if (customBgBitmap != null) {
                            Image(
                                bitmap = customBgBitmap!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (bgDimAlpha > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = bgDimAlpha))
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = currentPreset.colors.map { Color(it) }
                                        )
                                    )
                            )
                        }

                        // Frame Border Drawing Layer
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(frameInsetDp.dp)
                                .then(
                                    when (frameStyle) {
                                        QuoteFrameStyle.MINIMAL_BORDER -> {
                                            Modifier.border(
                                                width = borderWidthDp.dp,
                                                color = Color(borderColor),
                                                shape = RoundedCornerShape(cornerRadiusDp.dp)
                                            )
                                        }
                                        QuoteFrameStyle.GOLD_LUXURY -> {
                                            Modifier
                                                .border(
                                                    width = borderWidthDp.dp,
                                                    color = Color(borderColor),
                                                    shape = RoundedCornerShape(cornerRadiusDp.dp)
                                                )
                                                .padding(6.dp)
                                                .border(
                                                    width = (borderWidthDp * 0.6f).dp,
                                                    color = Color(borderColor).copy(alpha = 0.7f),
                                                    shape = RoundedCornerShape((cornerRadiusDp - 6f).coerceAtLeast(0f).dp)
                                                )
                                        }
                                        QuoteFrameStyle.NEON_CYBER -> {
                                            Modifier
                                                .shadow(12.dp, RoundedCornerShape(cornerRadiusDp.dp), spotColor = Color(borderColor))
                                                .border(
                                                    width = borderWidthDp.dp,
                                                    color = Color(borderColor),
                                                    shape = RoundedCornerShape(cornerRadiusDp.dp)
                                                )
                                        }
                                        QuoteFrameStyle.GLASS_CARD -> {
                                            Modifier
                                                .background(
                                                    color = Color.White.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(cornerRadiusDp.dp)
                                                )
                                                .border(
                                                    width = borderWidthDp.dp,
                                                    color = Color(borderColor).copy(alpha = 0.8f),
                                                    shape = RoundedCornerShape(cornerRadiusDp.dp)
                                                )
                                        }
                                        QuoteFrameStyle.EDITORIAL_MAGAZINE -> {
                                            Modifier
                                                .border(
                                                    width = 1.dp,
                                                    color = Color(borderColor).copy(alpha = 0.4f),
                                                    shape = RoundedCornerShape(cornerRadiusDp.dp)
                                                )
                                        }
                                        QuoteFrameStyle.DOUBLE_RULE_CLASSIC -> {
                                            Modifier
                                                .border(
                                                    width = borderWidthDp.dp,
                                                    color = Color(borderColor),
                                                    shape = RoundedCornerShape(cornerRadiusDp.dp)
                                                )
                                                .padding(4.dp)
                                                .border(
                                                    width = 1.dp,
                                                    color = Color(borderColor).copy(alpha = 0.6f),
                                                    shape = RoundedCornerShape((cornerRadiusDp - 4f).coerceAtLeast(0f).dp)
                                                )
                                        }
                                        QuoteFrameStyle.ROUNDED_PILL -> {
                                            Modifier.border(
                                                width = borderWidthDp.dp,
                                                color = Color(borderColor),
                                                shape = RoundedCornerShape(cornerRadiusDp.dp)
                                            )
                                        }
                                        else -> {
                                            Modifier.border(
                                                width = borderWidthDp.dp,
                                                color = Color(borderColor),
                                                shape = RoundedCornerShape(cornerRadiusDp.dp)
                                            )
                                        }
                                    }
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = when (textAlignment) {
                                    "left" -> Alignment.Start
                                    "right" -> Alignment.End
                                    else -> Alignment.CenterHorizontally
                                },
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Badge Icon
                                if (showBadgeIcon && badgeIcon.isNotBlank()) {
                                    Text(
                                        text = badgeIcon,
                                        fontSize = 28.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }

                                // Opening Quote Mark
                                if (showQuoteMarks) {
                                    Text(
                                        text = "“",
                                        fontSize = 42.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(borderColor).copy(alpha = 0.85f),
                                        lineHeight = 30.sp
                                    )
                                }

                                val shadowSpec = if (hasShadow) {
                                    Shadow(
                                        color = Color(shadowColor),
                                        offset = Offset(shadowDx, shadowDy),
                                        blurRadius = shadowRadius
                                    )
                                } else null

                                // Quote Text Content
                                Text(
                                    text = quoteText.ifBlank { "Write your inspirational quote..." },
                                    fontSize = fontSizeSp.sp,
                                    fontFamily = chosenFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(textColor),
                                    textAlign = when (textAlignment) {
                                        "left" -> TextAlign.Left
                                        "right" -> TextAlign.Right
                                        else -> TextAlign.Center
                                    },
                                    lineHeight = (fontSizeSp * 1.35f).sp,
                                    style = TextStyle(shadow = shadowSpec),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                // Closing Quote Mark
                                if (showQuoteMarks) {
                                    Text(
                                        text = "”",
                                        fontSize = 42.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(borderColor).copy(alpha = 0.85f),
                                        lineHeight = 30.sp
                                    )
                                }

                                // Author Tag
                                if (authorText.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "— $authorText",
                                        fontSize = (fontSizeSp * 0.65f).coerceAtLeast(13f).sp,
                                        fontFamily = TextOverlayUtils.resolveFontFamily("Poppins"),
                                        fontWeight = FontWeight.SemiBold,
                                        fontStyle = FontStyle.Italic,
                                        color = Color(authorColor),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Top Pill badge if ROUNDED_PILL style
                        if (frameStyle == QuoteFrameStyle.ROUNDED_PILL) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = (frameInsetDp - 12f).coerceAtLeast(4f).dp),
                                shape = RoundedCornerShape(20.dp),
                                color = Color(borderColor)
                            ) {
                                Text(
                                    text = selectedCategory.displayName.uppercase(),
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Editorial side bar indicator
                        if (frameStyle == QuoteFrameStyle.EDITORIAL_MAGAZINE) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = (frameInsetDp + 4f).dp)
                                    .width(5.dp)
                                    .height(70.dp)
                                    .background(Color(borderColor), RoundedCornerShape(3.dp))
                            )
                        }

                        // Aspect ratio badge (bottom right)
                        // Quick Export chip (bottom left)
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .clickable { showExportSheet = true }
                                .testTag("quote_export_canvas_btn"),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Export HD",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Aspect ratio badge (bottom right)
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .clickable {
                                    currentAspectRatio = when (currentAspectRatio) {
                                        "1:1" -> "9:16"
                                        "9:16" -> "4:5"
                                        else -> "1:1"
                                    }
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "📐 $currentAspectRatio",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Tools Area (Tabs & Controls)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = selectedToolTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Tab(
                            selected = selectedToolTab == 0,
                            onClick = { selectedToolTab = 0 },
                            text = { Text("Templates", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp)) },
                            modifier = Modifier.testTag("quote_tab_templates")
                        )
                        Tab(
                            selected = selectedToolTab == 1,
                            onClick = { selectedToolTab = 1 },
                            text = { Text("Quotes", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.FormatQuote, contentDescription = null, modifier = Modifier.size(15.dp)) },
                            modifier = Modifier.testTag("quote_tab_content")
                        )
                        Tab(
                            selected = selectedToolTab == 2,
                            onClick = { selectedToolTab = 2 },
                            text = { Text("Frames", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.ViewQuilt, contentDescription = null, modifier = Modifier.size(15.dp)) },
                            modifier = Modifier.testTag("quote_tab_frames")
                        )
                        Tab(
                            selected = selectedToolTab == 3,
                            onClick = { selectedToolTab = 3 },
                            text = { Text("Background", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(15.dp)) },
                            modifier = Modifier.testTag("quote_tab_bg")
                        )
                        Tab(
                            selected = selectedToolTab == 4,
                            onClick = { selectedToolTab = 4 },
                            text = { Text("Typography", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(15.dp)) },
                            modifier = Modifier.testTag("quote_tab_type")
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        when (selectedToolTab) {
                            // TAB 0: Curated Quote Templates
                            0 -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🌟 Curated Quote Templates", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = "${QuoteRepository.getAllTemplates().size} Presets",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Categories Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        QuoteCategory.values().forEach { cat ->
                                            FilterChip(
                                                selected = selectedCategory == cat,
                                                onClick = { selectedCategory = cat },
                                                label = { Text("${cat.icon} ${cat.displayName}", fontSize = 12.sp) },
                                                modifier = Modifier.testTag("tpl_cat_chip_${cat.name}")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    val templates = remember(selectedCategory) {
                                        QuoteRepository.getTemplatesByCategory(selectedCategory)
                                    }

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(templates) { tpl ->
                                            Card(
                                                modifier = Modifier
                                                    .width(230.dp)
                                                    .height(118.dp)
                                                    .clickable {
                                                        quoteText = tpl.quote
                                                        authorText = tpl.author
                                                        selectedCategory = tpl.category
                                                        frameStyle = tpl.frameStyle
                                                        backgroundPresetId = tpl.backgroundPresetId
                                                        customBgBitmap = null
                                                        fontId = tpl.fontId
                                                        fontSizeSp = tpl.fontSizeSp
                                                        textColor = tpl.textColor
                                                        authorColor = tpl.authorColor
                                                        borderColor = tpl.borderColor
                                                        borderWidthDp = tpl.borderWidthDp
                                                        cornerRadiusDp = tpl.cornerRadiusDp
                                                        frameInsetDp = tpl.frameInsetDp
                                                        textAlignment = tpl.textAlign
                                                        showQuoteMarks = tpl.showQuoteMarks
                                                        showBadgeIcon = tpl.showBadgeIcon
                                                        hasShadow = tpl.hasShadow
                                                        badgeIcon = tpl.badgeIcon
                                                        currentAspectRatio = tpl.defaultAspectRatio
                                                        Toast.makeText(context, "Applied: ${tpl.name}! ✨", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .testTag("quote_template_card_${tpl.id}"),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (quoteText == tpl.quote && frameStyle == tpl.frameStyle)
                                                        MaterialTheme.colorScheme.primaryContainer
                                                    else MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                                shape = RoundedCornerShape(12.dp),
                                                border = if (quoteText == tpl.quote && frameStyle == tpl.frameStyle)
                                                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                                else null
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(10.dp),
                                                    verticalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "${tpl.badgeIcon} ${tpl.name}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = MaterialTheme.colorScheme.surfaceVariant
                                                        ) {
                                                            Text(
                                                                text = tpl.frameStyle.displayName.take(10),
                                                                fontSize = 9.sp,
                                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        text = "“${tpl.quote}”",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "— ${tpl.author}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            maxLines = 1
                                                        )
                                                        Text(
                                                            text = "Apply ⚡",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // TAB 1: Quotes Library & Custom Input
                            1 -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Categories Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        QuoteCategory.values().forEach { cat ->
                                            FilterChip(
                                                selected = selectedCategory == cat,
                                                onClick = { selectedCategory = cat },
                                                label = { Text("${cat.icon} ${cat.displayName}", fontSize = 12.sp) },
                                                modifier = Modifier.testTag("cat_chip_${cat.name}")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Quick curated quote cards horizontal list
                                    val filteredQuotes = remember(selectedCategory) {
                                        QuoteRepository.getQuotesByCategory(selectedCategory)
                                    }

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(filteredQuotes) { item ->
                                            Card(
                                                modifier = Modifier
                                                    .width(220.dp)
                                                    .height(78.dp)
                                                    .clickable {
                                                        quoteText = item.quote
                                                        authorText = item.author
                                                        badgeIcon = item.badgeIcon
                                                        frameStyle = item.suggestedFrame
                                                        backgroundPresetId = item.suggestedBgId
                                                    }
                                                    .testTag("quote_item_${item.id}"),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (quoteText == item.quote)
                                                        MaterialTheme.colorScheme.primaryContainer
                                                    else MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(8.dp),
                                                    verticalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = item.quote,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = item.badgeIcon,
                                                            fontSize = 11.sp
                                                        )
                                                        Text(
                                                            text = "— ${item.author}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            maxLines = 1
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Quote & Author input fields
                                    OutlinedTextField(
                                        value = quoteText,
                                        onValueChange = { quoteText = it },
                                        label = { Text("Edit Quote / Fact Text") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("quote_text_input")
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = authorText,
                                            onValueChange = { authorText = it },
                                            label = { Text("Author / Source Name") },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("author_text_input")
                                        )

                                        OutlinedTextField(
                                            value = badgeIcon,
                                            onValueChange = { badgeIcon = it },
                                            label = { Text("Emoji") },
                                            modifier = Modifier
                                                .width(76.dp)
                                                .testTag("badge_icon_input")
                                        )
                                    }
                                }
                            }

                            // TAB 2: Frame Styles & Adjustments
                            2 -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("Select Quote Frame Style", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Frame styles carousel
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        QuoteFrameStyle.values().forEach { style ->
                                            val isSel = frameStyle == style
                                            Card(
                                                modifier = Modifier
                                                    .width(135.dp)
                                                    .clickable { frameStyle = style }
                                                    .testTag("frame_style_${style.name}"),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                                border = if (isSel) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(10.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    // Miniature frame representation icon
                                                    Box(
                                                        modifier = Modifier
                                                            .size(44.dp)
                                                            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                            .border(
                                                                width = 2.dp,
                                                                color = if (isSel) MaterialTheme.colorScheme.primary else Color.Gray,
                                                                shape = RoundedCornerShape(6.dp)
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text("“ ”", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = style.displayName,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Frame Adjustments
                                    ValueSliderRow(
                                        label = "Border Width",
                                        value = borderWidthDp,
                                        range = 1f..10f,
                                        onValueChange = { borderWidthDp = it }
                                    )

                                    ValueSliderRow(
                                        label = "Corner Radius",
                                        value = cornerRadiusDp,
                                        range = 0f..32f,
                                        onValueChange = { cornerRadiusDp = it }
                                    )

                                    ValueSliderRow(
                                        label = "Frame Inset / Margin",
                                        value = frameInsetDp,
                                        range = 8f..36f,
                                        onValueChange = { frameInsetDp = it }
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Border Frame Color", style = MaterialTheme.typography.labelMedium)
                                    PaletteColorPicker(
                                        selectedColor = borderColor,
                                        onColorSelected = { borderColor = it }
                                    )
                                }
                            }

                            // TAB 3: Background Presets & Custom Image
                            3 -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Aesthetic Gradients & Themes", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                        Button(
                                            onClick = {
                                                galleryLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                            modifier = Modifier.testTag("quote_pick_bg_btn"),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Photo BG", fontSize = 12.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Gradient Presets list
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        QuoteRepository.BACKGROUND_PRESETS.forEach { preset ->
                                            val isSel = customBgBitmap == null && backgroundPresetId == preset.id
                                            Card(
                                                modifier = Modifier
                                                    .size(62.dp)
                                                    .clickable {
                                                        customBgBitmap = null
                                                        backgroundPresetId = preset.id
                                                        textColor = preset.defaultTextColor
                                                        borderColor = preset.defaultBorderColor
                                                    }
                                                    .testTag("bg_preset_${preset.id}"),
                                                shape = RoundedCornerShape(10.dp),
                                                border = if (isSel) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            Brush.linearGradient(preset.colors.map { Color(it) })
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (isSel) {
                                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (customBgBitmap != null) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Active Custom Photo Background", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                            OutlinedButton(
                                                onClick = { customBgBitmap = null },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("Reset to Gradient", fontSize = 11.sp)
                                            }
                                        }
                                        ValueSliderRow(
                                            label = "Photo Dim Overlay",
                                            value = bgDimAlpha,
                                            range = 0f..0.85f,
                                            onValueChange = { bgDimAlpha = it }
                                        )
                                    }
                                }
                            }

                            // TAB 4: Typography & Styling
                            4 -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("Typography Font", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Font chips
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        TextOverlayUtils.FONTS.forEach { opt ->
                                            val isSel = fontId.equals(opt.id, ignoreCase = true)
                                            FilterChip(
                                                selected = isSel,
                                                onClick = { fontId = opt.id },
                                                label = { Text(opt.name, fontFamily = opt.fontFamily) },
                                                modifier = Modifier.testTag("quote_font_${opt.id}")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    ValueSliderRow(
                                        label = "Font Size",
                                        value = fontSizeSp,
                                        range = 16f..44f,
                                        onValueChange = { fontSizeSp = it }
                                    )

                                    // Text Alignment & Features
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Alignment", style = MaterialTheme.typography.labelMedium)
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = { textAlignment = "left" },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.FormatAlignLeft,
                                                    contentDescription = "Left",
                                                    tint = if (textAlignment == "left") MaterialTheme.colorScheme.primary else Color.Gray
                                                )
                                            }
                                            IconButton(
                                                onClick = { textAlignment = "center" },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.FormatAlignCenter,
                                                    contentDescription = "Center",
                                                    tint = if (textAlignment == "center") MaterialTheme.colorScheme.primary else Color.Gray
                                                )
                                            }
                                            IconButton(
                                                onClick = { textAlignment = "right" },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.FormatAlignRight,
                                                    contentDescription = "Right",
                                                    tint = if (textAlignment == "right") MaterialTheme.colorScheme.primary else Color.Gray
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        FilterChip(
                                            selected = showQuoteMarks,
                                            onClick = { showQuoteMarks = !showQuoteMarks },
                                            label = { Text("Quotation Marks “ ”") }
                                        )
                                        FilterChip(
                                            selected = showBadgeIcon,
                                            onClick = { showBadgeIcon = !showBadgeIcon },
                                            label = { Text("Badge Emoji") }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Text Color", style = MaterialTheme.typography.labelMedium)
                                    PaletteColorPicker(
                                        selectedColor = textColor,
                                        onColorSelected = { textColor = it }
                                    )

                                    Text("Author Tag Color", style = MaterialTheme.typography.labelMedium)
                                    PaletteColorPicker(
                                        selectedColor = authorColor,
                                        onColorSelected = { authorColor = it }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dedicated Export & Share Modal BottomSheet
        if (showExportSheet) {
            ModalBottomSheet(
                onDismissRequest = { showExportSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("quote_export_bottom_sheet")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Export & Share Quote",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choose aspect ratio and export resolution",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Aspect Ratio Selector
                    Text(
                        text = "Aspect Ratio",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("1:1", "Square", "1080×1080"),
                            Triple("9:16", "Story", "1080×1920"),
                            Triple("4:5", "Portrait", "1080×1350")
                        ).forEach { (ratio, label, res) ->
                            val isSel = currentAspectRatio == ratio
                            FilterChip(
                                selected = isSel,
                                onClick = { currentAspectRatio = ratio },
                                label = {
                                    Column(modifier = Modifier.padding(vertical = 2.dp)) {
                                        Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text(res, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("export_ratio_$ratio")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quality & Format Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⚡", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Format: High-Res PNG", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = when (currentAspectRatio) {
                                        "9:16" -> "1080 × 1920 Ultra HD"
                                        "4:5" -> "1080 × 1350 Ultra HD"
                                        else -> "1080 × 1080 Ultra HD"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Button: Save Directly to Device Gallery
                    Button(
                        onClick = {
                            scope.launch {
                                isSaving = true
                                val bmp = generateQuoteExport()
                                val uri = BitmapUtils.saveBitmapToGallery(context, bmp, "Quote_${System.currentTimeMillis()}")
                                repository.saveCreation(bmp, quoteText.take(28), "quote")
                                isSaving = false
                                showExportSheet = false
                                if (uri != null) {
                                    Toast.makeText(context, "Saved to device Gallery / Photos! 🖼️", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Saved to My Creations! ✨", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("export_save_gallery_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save to Phone Gallery (HD)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary Actions: Share Image & Save to Creations
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    val bmp = generateQuoteExport()
                                    BitmapUtils.shareBitmap(context, bmp, "Quote: \"$quoteText\" — $authorText #QuotesMaker")
                                    showExportSheet = false
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("export_share_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Image", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    isSaving = true
                                    val bmp = generateQuoteExport()
                                    repository.saveCreation(bmp, quoteText.take(28), "quote")
                                    isSaving = false
                                    showExportSheet = false
                                    Toast.makeText(context, "Saved to My Creations! ✨", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("export_save_creations_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("My Creations", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tertiary Action: Copy quote text
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Quote", "\"$quoteText\"\n— $authorText")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Quote copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("export_copy_text_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Quote Text with Author", fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

