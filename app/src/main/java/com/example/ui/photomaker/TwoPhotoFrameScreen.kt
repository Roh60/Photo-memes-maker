package com.example.ui.photomaker

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.CreationsRepository
import com.example.ui.components.PaletteColorPicker
import com.example.ui.components.StudioTopBar
import com.example.ui.components.ValueSliderRow
import com.example.util.BitmapUtils
import com.example.util.SampleImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class TwoFrameLayout {
    SIDE_BY_SIDE,
    TOP_BOTTOM,
    BEFORE_AFTER,
    COUPLE_ROMANCE,
    BEST_FRIENDS,
    PICTURE_IN_PICTURE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoPhotoFrameScreen(
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { CreationsRepository(context) }

    // Two Photos state
    var photo1 by remember {
        mutableStateOf<Bitmap?>(SampleImages.createScenicSample(600, 600, "Left Photo", 0xFF6366F1.toInt(), 0xFF8B5CF6.toInt()))
    }
    var photo2 by remember {
        mutableStateOf<Bitmap?>(SampleImages.createScenicSample(600, 600, "Right Photo", 0xFFEC4899.toInt(), 0xFFF43F5E.toInt()))
    }

    // Selected photo target for replacement: 1 or 2
    var activePhotoTarget by remember { mutableStateOf(1) }
    var isProcessing by remember { mutableStateOf(false) }

    // Layout configuration
    var selectedLayout by remember { mutableStateOf(TwoFrameLayout.SIDE_BY_SIDE) }
    var dividerSpacing by remember { mutableFloatStateOf(8f) }
    var cornerRadiusDp by remember { mutableFloatStateOf(12f) }
    var borderWidthDp by remember { mutableFloatStateOf(4f) }
    var borderColor by remember { mutableStateOf(0xFFFFFFFF) }
    var frameBgColor by remember { mutableStateOf(0xFF0F172A) }

    // Independent filters
    var photo1Filter by remember { mutableStateOf("none") }
    var photo2Filter by remember { mutableStateOf("none") }

    // Optional caption
    var bannerTitle by remember { mutableStateOf("Together Forever") }
    var showBanner by remember { mutableStateOf(true) }

    // Processed bitmaps
    var processedPhoto1 by remember { mutableStateOf<Bitmap?>(null) }
    var processedPhoto2 by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(photo1, photo1Filter) {
        val src = photo1 ?: return@LaunchedEffect
        withContext(Dispatchers.Default) {
            val cm = BitmapUtils.createFilterColorMatrix(0f, 1f, 1f, 0f, photo1Filter)
            processedPhoto1 = BitmapUtils.processBitmap(src, cm)
        }
    }

    LaunchedEffect(photo2, photo2Filter) {
        val src = photo2 ?: return@LaunchedEffect
        withContext(Dispatchers.Default) {
            val cm = BitmapUtils.createFilterColorMatrix(0f, 1f, 1f, 0f, photo2Filter)
            processedPhoto2 = BitmapUtils.processBitmap(src, cm)
        }
    }

    // Active bottom tab: "layout", "filters", "style", "caption"
    var bottomTab by remember { mutableStateOf("layout") }

    // Media launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isProcessing = true
                val bmp = BitmapUtils.decodeUriToBitmap(context, uri)
                if (bmp != null) {
                    if (activePhotoTarget == 1) photo1 = bmp else photo2 = bmp
                }
                isProcessing = false
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            if (activePhotoTarget == 1) photo1 = bmp else photo2 = bmp
        }
    }

    // Dual-photo gallery launcher
    val dualGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 2)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            scope.launch {
                isProcessing = true
                if (uris.size >= 1) {
                    val bmp1 = BitmapUtils.decodeUriToBitmap(context, uris[0])
                    if (bmp1 != null) photo1 = bmp1
                }
                if (uris.size >= 2) {
                    val bmp2 = BitmapUtils.decodeUriToBitmap(context, uris[1])
                    if (bmp2 != null) photo2 = bmp2
                    Toast.makeText(context, "Loaded 2 photos from gallery!", Toast.LENGTH_SHORT).show()
                }
                isProcessing = false
            }
        }
    }

    fun swapPhotos() {
        val temp = photo1
        photo1 = photo2
        photo2 = temp
        val tempFilter = photo1Filter
        photo1Filter = photo2Filter
        photo2Filter = tempFilter
        Toast.makeText(context, "Swapped photo 1 and photo 2", Toast.LENGTH_SHORT).show()
    }

    // Export renderer
    fun generateTwoFrameExport(): Bitmap {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // Background
        val bgPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = frameBgColor.toInt()
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val p1 = processedPhoto1 ?: photo1
        val p2 = processedPhoto2 ?: photo2

        val borderPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor.toInt()
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = borderWidthDp * 3f
        }

        val padding = 24f
        val gap = dividerSpacing * 2f
        val r = cornerRadiusDp * 2.5f

        when (selectedLayout) {
            TwoFrameLayout.SIDE_BY_SIDE -> {
                val colW = (width - padding * 2 - gap) / 2f
                val colH = height - padding * 2

                if (p1 != null) {
                    val rect1 = android.graphics.RectF(padding, padding, padding + colW, padding + colH)
                    val path1 = android.graphics.Path().apply { addRoundRect(rect1, r, r, android.graphics.Path.Direction.CW) }
                    canvas.save()
                    canvas.clipPath(path1)
                    canvas.drawBitmap(p1, null, rect1, null)
                    canvas.restore()
                    if (borderWidthDp > 0) canvas.drawRoundRect(rect1, r, r, borderPaint)
                }

                if (p2 != null) {
                    val rect2 = android.graphics.RectF(padding + colW + gap, padding, width - padding, padding + colH)
                    val path2 = android.graphics.Path().apply { addRoundRect(rect2, r, r, android.graphics.Path.Direction.CW) }
                    canvas.save()
                    canvas.clipPath(path2)
                    canvas.drawBitmap(p2, null, rect2, null)
                    canvas.restore()
                    if (borderWidthDp > 0) canvas.drawRoundRect(rect2, r, r, borderPaint)
                }
            }

            TwoFrameLayout.TOP_BOTTOM -> {
                val rowW = width - padding * 2
                val rowH = (height - padding * 2 - gap) / 2f

                if (p1 != null) {
                    val rect1 = android.graphics.RectF(padding, padding, width - padding, padding + rowH)
                    val path1 = android.graphics.Path().apply { addRoundRect(rect1, r, r, android.graphics.Path.Direction.CW) }
                    canvas.save()
                    canvas.clipPath(path1)
                    canvas.drawBitmap(p1, null, rect1, null)
                    canvas.restore()
                    if (borderWidthDp > 0) canvas.drawRoundRect(rect1, r, r, borderPaint)
                }

                if (p2 != null) {
                    val rect2 = android.graphics.RectF(padding, padding + rowH + gap, width - padding, height - padding)
                    val path2 = android.graphics.Path().apply { addRoundRect(rect2, r, r, android.graphics.Path.Direction.CW) }
                    canvas.save()
                    canvas.clipPath(path2)
                    canvas.drawBitmap(p2, null, rect2, null)
                    canvas.restore()
                    if (borderWidthDp > 0) canvas.drawRoundRect(rect2, r, r, borderPaint)
                }
            }

            TwoFrameLayout.BEFORE_AFTER -> {
                val colW = (width - padding * 2 - gap) / 2f
                val colH = height - padding * 2

                if (p1 != null) {
                    val rect1 = android.graphics.RectF(padding, padding, padding + colW, padding + colH)
                    val path1 = android.graphics.Path().apply { addRoundRect(rect1, r, r, android.graphics.Path.Direction.CW) }
                    canvas.save()
                    canvas.clipPath(path1)
                    canvas.drawBitmap(p1, null, rect1, null)
                    canvas.restore()
                    if (borderWidthDp > 0) canvas.drawRoundRect(rect1, r, r, borderPaint)

                    // "BEFORE" Badge
                    val badgePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.BLACK
                        alpha = 180
                    }
                    canvas.drawRect(padding + 20f, padding + 20f, padding + 180f, padding + 70f, badgePaint)
                    val txtPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.WHITE
                        textSize = 30f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    canvas.drawText("BEFORE", padding + 35f, padding + 56f, txtPaint)
                }

                if (p2 != null) {
                    val rect2 = android.graphics.RectF(padding + colW + gap, padding, width - padding, padding + colH)
                    val path2 = android.graphics.Path().apply { addRoundRect(rect2, r, r, android.graphics.Path.Direction.CW) }
                    canvas.save()
                    canvas.clipPath(path2)
                    canvas.drawBitmap(p2, null, rect2, null)
                    canvas.restore()
                    if (borderWidthDp > 0) canvas.drawRoundRect(rect2, r, r, borderPaint)

                    // "AFTER" Badge
                    val badgePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = 0xFF10B981.toInt()
                    }
                    canvas.drawRect(padding + colW + gap + 20f, padding + 20f, padding + colW + gap + 160f, padding + 70f, badgePaint)
                    val txtPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.WHITE
                        textSize = 30f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    canvas.drawText("AFTER", padding + colW + gap + 35f, padding + 56f, txtPaint)
                }
            }

            TwoFrameLayout.PICTURE_IN_PICTURE -> {
                // Large background photo
                if (p1 != null) {
                    val fullRect = android.graphics.RectF(padding, padding, width - padding, height - padding)
                    val fullPath = android.graphics.Path().apply { addRoundRect(fullRect, r, r, android.graphics.Path.Direction.CW) }
                    canvas.save()
                    canvas.clipPath(fullPath)
                    canvas.drawBitmap(p1, null, fullRect, null)
                    canvas.restore()
                }

                // Inset smaller photo bottom right
                if (p2 != null) {
                    val insetW = width * 0.42f
                    val insetH = height * 0.42f
                    val insetRect = android.graphics.RectF(
                        width - padding - insetW - 20f,
                        height - padding - insetH - 20f,
                        width - padding - 20f,
                        height - padding - 20f
                    )
                    val insetPath = android.graphics.Path().apply { addRoundRect(insetRect, 32f, 32f, android.graphics.Path.Direction.CW) }
                    canvas.save()
                    canvas.clipPath(insetPath)
                    canvas.drawBitmap(p2, null, insetRect, null)
                    canvas.restore()
                    canvas.drawRoundRect(insetRect, 32f, 32f, borderPaint)
                }
            }

            else -> {
                // COUPLE / BEST FRIENDS layout: two circles or rounded cards with decorative heart/star
                val cardW = (width - padding * 2 - gap) / 2f
                val cardH = height * 0.7f
                val topY = height * 0.15f

                if (p1 != null) {
                    val rect1 = android.graphics.RectF(padding, topY, padding + cardW, topY + cardH)
                    val path1 = android.graphics.Path().apply { addRoundRect(rect1, cardW / 2f, cardW / 2f, android.graphics.Path.Direction.CW) }
                    canvas.save()
                    canvas.clipPath(path1)
                    canvas.drawBitmap(p1, null, rect1, null)
                    canvas.restore()
                    canvas.drawRoundRect(rect1, cardW / 2f, cardW / 2f, borderPaint)
                }

                if (p2 != null) {
                    val rect2 = android.graphics.RectF(padding + cardW + gap, topY, width - padding, topY + cardH)
                    val path2 = android.graphics.Path().apply { addRoundRect(rect2, cardW / 2f, cardW / 2f, android.graphics.Path.Direction.CW) }
                    canvas.save()
                    canvas.clipPath(path2)
                    canvas.drawBitmap(p2, null, rect2, null)
                    canvas.restore()
                    canvas.drawRoundRect(rect2, cardW / 2f, cardW / 2f, borderPaint)
                }

                // Central heart emoji
                val heartPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 72f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val icon = if (selectedLayout == TwoFrameLayout.COUPLE_ROMANCE) "💖" else "🤝"
                canvas.drawText(icon, width * 0.5f, height * 0.52f, heartPaint)
            }
        }

        // Draw bottom banner if enabled
        if (showBanner && bannerTitle.isNotEmpty()) {
            val bannerPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textSize = 42f
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setShadowLayer(14f, 0f, 4f, android.graphics.Color.BLACK)
            }
            canvas.drawText(bannerTitle, width * 0.5f, height - 40f, bannerPaint)
        }

        return bitmap
    }

    Scaffold(
        topBar = {
            StudioTopBar(
                title = "2 Photo Frame Maker",
                onBack = onNavigateBack,
                actions = {
                    // Swap Photos button
                    IconButton(
                        onClick = {
                            val temp = photo1
                            photo1 = photo2
                            photo2 = temp
                            val tempF = photo1Filter
                            photo1Filter = photo2Filter
                            photo2Filter = tempF
                            Toast.makeText(context, "Photos Swapped!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("swap_photos_btn")
                    ) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Swap Photos")
                    }

                    // Share
                    IconButton(
                        onClick = {
                            val bmp = generateTwoFrameExport()
                            BitmapUtils.shareBitmap(context, bmp, "Created with Photo & Memes Maker")
                        },
                        modifier = Modifier.testTag("two_frame_share_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }

                    // Save
                    IconButton(
                        onClick = {
                            scope.launch {
                                isProcessing = true
                                val bmp = generateTwoFrameExport()
                                val uri = BitmapUtils.saveBitmapToGallery(context, bmp, "2PhotoFrame")
                                repository.saveCreation(bmp, "2 Photo Frame", "two_frame")
                                isProcessing = false
                                Toast.makeText(context, "Frame Saved to Gallery!", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.testTag("two_frame_save_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live 2-Photo Canvas Preview
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val canvasSide = minOf(constraints.maxWidth, constraints.maxHeight)
                    val p1 = processedPhoto1 ?: photo1
                    val p2 = processedPhoto2 ?: photo2

                    Canvas(
                        modifier = Modifier
                            .size((canvasSide / LocalContext.current.resources.displayMetrics.density).dp)
                            .testTag("two_photo_canvas")
                    ) {
                        drawRect(color = Color(frameBgColor))

                        val pad = 12f
                        val gap = dividerSpacing
                        val r = cornerRadiusDp

                        when (selectedLayout) {
                            TwoFrameLayout.SIDE_BY_SIDE -> {
                                val colW = (size.width - pad * 2 - gap) / 2f
                                val colH = size.height - pad * 2

                                if (p1 != null) {
                                    val r1 = Rect(pad, pad, pad + colW, pad + colH)
                                    val path1 = Path().apply { addRoundRect(RoundRect(r1, CornerRadius(r, r))) }
                                    clipPath(path1) {
                                        drawImage(
                                            image = p1.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(r1.left.toInt(), r1.top.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(r1.width.toInt(), r1.height.toInt())
                                        )
                                    }
                                    if (borderWidthDp > 0) drawRoundRect(color = Color(borderColor), topLeft = Offset(r1.left, r1.top), size = Size(r1.width, r1.height), cornerRadius = CornerRadius(r, r), style = Stroke(borderWidthDp * 1.5f))
                                }

                                if (p2 != null) {
                                    val r2 = Rect(pad + colW + gap, pad, size.width - pad, pad + colH)
                                    val path2 = Path().apply { addRoundRect(RoundRect(r2, CornerRadius(r, r))) }
                                    clipPath(path2) {
                                        drawImage(
                                            image = p2.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(r2.left.toInt(), r2.top.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(r2.width.toInt(), r2.height.toInt())
                                        )
                                    }
                                    if (borderWidthDp > 0) drawRoundRect(color = Color(borderColor), topLeft = Offset(r2.left, r2.top), size = Size(r2.width, r2.height), cornerRadius = CornerRadius(r, r), style = Stroke(borderWidthDp * 1.5f))
                                }
                            }

                            TwoFrameLayout.TOP_BOTTOM -> {
                                val rowW = size.width - pad * 2
                                val rowH = (size.height - pad * 2 - gap) / 2f

                                if (p1 != null) {
                                    val r1 = Rect(pad, pad, size.width - pad, pad + rowH)
                                    val path1 = Path().apply { addRoundRect(RoundRect(r1, CornerRadius(r, r))) }
                                    clipPath(path1) {
                                        drawImage(
                                            image = p1.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(r1.left.toInt(), r1.top.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(r1.width.toInt(), r1.height.toInt())
                                        )
                                    }
                                    if (borderWidthDp > 0) drawRoundRect(color = Color(borderColor), topLeft = Offset(r1.left, r1.top), size = Size(r1.width, r1.height), cornerRadius = CornerRadius(r, r), style = Stroke(borderWidthDp * 1.5f))
                                }

                                if (p2 != null) {
                                    val r2 = Rect(pad, pad + rowH + gap, size.width - pad, size.height - pad)
                                    val path2 = Path().apply { addRoundRect(RoundRect(r2, CornerRadius(r, r))) }
                                    clipPath(path2) {
                                        drawImage(
                                            image = p2.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(r2.left.toInt(), r2.top.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(r2.width.toInt(), r2.height.toInt())
                                        )
                                    }
                                    if (borderWidthDp > 0) drawRoundRect(color = Color(borderColor), topLeft = Offset(r2.left, r2.top), size = Size(r2.width, r2.height), cornerRadius = CornerRadius(r, r), style = Stroke(borderWidthDp * 1.5f))
                                }
                            }

                            TwoFrameLayout.BEFORE_AFTER -> {
                                val colW = (size.width - pad * 2 - gap) / 2f
                                val colH = size.height - pad * 2

                                if (p1 != null) {
                                    val r1 = Rect(pad, pad, pad + colW, pad + colH)
                                    val path1 = Path().apply { addRoundRect(RoundRect(r1, CornerRadius(r, r))) }
                                    clipPath(path1) {
                                        drawImage(
                                            image = p1.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(r1.left.toInt(), r1.top.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(r1.width.toInt(), r1.height.toInt())
                                        )
                                    }
                                    drawIntoCanvas {
                                        val bp = android.graphics.Paint().apply { color = android.graphics.Color.BLACK; alpha = 180 }
                                        it.nativeCanvas.drawRect(pad + 10f, pad + 10f, pad + 120f, pad + 45f, bp)
                                        val tp = android.graphics.Paint().apply { color = android.graphics.Color.WHITE; textSize = 22f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
                                        it.nativeCanvas.drawText("BEFORE", pad + 20f, pad + 36f, tp)
                                    }
                                }

                                if (p2 != null) {
                                    val r2 = Rect(pad + colW + gap, pad, size.width - pad, pad + colH)
                                    val path2 = Path().apply { addRoundRect(RoundRect(r2, CornerRadius(r, r))) }
                                    clipPath(path2) {
                                        drawImage(
                                            image = p2.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(r2.left.toInt(), r2.top.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(r2.width.toInt(), r2.height.toInt())
                                        )
                                    }
                                    drawIntoCanvas {
                                        val bp = android.graphics.Paint().apply { color = 0xFF10B981.toInt() }
                                        it.nativeCanvas.drawRect(pad + colW + gap + 10f, pad + 10f, pad + colW + gap + 110f, pad + 45f, bp)
                                        val tp = android.graphics.Paint().apply { color = android.graphics.Color.WHITE; textSize = 22f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
                                        it.nativeCanvas.drawText("AFTER", pad + colW + gap + 20f, pad + 36f, tp)
                                    }
                                }
                            }

                            TwoFrameLayout.PICTURE_IN_PICTURE -> {
                                if (p1 != null) {
                                    val r1 = Rect(pad, pad, size.width - pad, size.height - pad)
                                    clipPath(Path().apply { addRoundRect(RoundRect(r1, CornerRadius(r, r))) }) {
                                        drawImage(
                                            image = p1.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(r1.left.toInt(), r1.top.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(r1.width.toInt(), r1.height.toInt())
                                        )
                                    }
                                }
                                if (p2 != null) {
                                    val iw = size.width * 0.42f
                                    val ih = size.height * 0.42f
                                    val r2 = Rect(size.width - pad - iw - 10f, size.height - pad - ih - 10f, size.width - pad - 10f, size.height - pad - 10f)
                                    clipPath(Path().apply { addRoundRect(RoundRect(r2, CornerRadius(16f, 16f))) }) {
                                        drawImage(
                                            image = p2.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(r2.left.toInt(), r2.top.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(r2.width.toInt(), r2.height.toInt())
                                        )
                                    }
                                    drawRoundRect(color = Color(borderColor), topLeft = Offset(r2.left, r2.top), size = Size(r2.width, r2.height), cornerRadius = CornerRadius(16f, 16f), style = Stroke(borderWidthDp * 1.5f))
                                }
                            }

                            else -> {
                                val cw = (size.width - pad * 2 - gap) / 2f
                                val ch = size.height * 0.72f
                                val topY = size.height * 0.12f

                                if (p1 != null) {
                                    val r1 = Rect(pad, topY, pad + cw, topY + ch)
                                    clipPath(Path().apply { addRoundRect(RoundRect(r1, CornerRadius(cw / 2f, cw / 2f))) }) {
                                        drawImage(
                                            image = p1.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(r1.left.toInt(), r1.top.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(r1.width.toInt(), r1.height.toInt())
                                        )
                                    }
                                    drawRoundRect(color = Color(borderColor), topLeft = Offset(r1.left, r1.top), size = Size(r1.width, r1.height), cornerRadius = CornerRadius(cw / 2f, cw / 2f), style = Stroke(borderWidthDp * 1.5f))
                                }

                                if (p2 != null) {
                                    val r2 = Rect(pad + cw + gap, topY, size.width - pad, topY + ch)
                                    clipPath(Path().apply { addRoundRect(RoundRect(r2, CornerRadius(cw / 2f, cw / 2f))) }) {
                                        drawImage(
                                            image = p2.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(r2.left.toInt(), r2.top.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(r2.width.toInt(), r2.height.toInt())
                                        )
                                    }
                                    drawRoundRect(color = Color(borderColor), topLeft = Offset(r2.left, r2.top), size = Size(r2.width, r2.height), cornerRadius = CornerRadius(cw / 2f, cw / 2f), style = Stroke(borderWidthDp * 1.5f))
                                }

                                drawIntoCanvas {
                                    val ep = android.graphics.Paint().apply { textSize = 48f; textAlign = android.graphics.Paint.Align.CENTER }
                                    it.nativeCanvas.drawText("💖", size.width * 0.5f, size.height * 0.52f, ep)
                                }
                            }
                        }

                        if (showBanner && bannerTitle.isNotEmpty()) {
                            drawIntoCanvas {
                                val bp = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                    color = android.graphics.Color.WHITE
                                    textSize = size.width * 0.05f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                                    setShadowLayer(8f, 0f, 2f, android.graphics.Color.BLACK)
                                }
                                it.nativeCanvas.drawText(bannerTitle, size.width * 0.5f, size.height - 18f, bp)
                            }
                        }
                    }

                    // Choose which photo to replace buttons
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (activePhotoTarget == 1) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.clickable { activePhotoTarget = 1 }.testTag("select_photo1_target")
                        ) {
                            Text(
                                text = "Photo 1",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (activePhotoTarget == 2) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.clickable { activePhotoTarget = 2 }.testTag("select_photo2_target")
                        ) {
                            Text(
                                text = "Photo 2",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.clickable { cameraLauncher.launch(null) }.testTag("two_frame_cam")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White, modifier = Modifier.padding(6.dp).size(18.dp))
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.clickable {
                                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }.testTag("two_frame_gallery")
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.padding(6.dp).size(18.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .clickable {
                                    dualGalleryLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .testTag("two_frame_select_both_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Select 2",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Sub Tab Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = bottomTab == "layout",
                    onClick = { bottomTab = "layout" },
                    label = { Text("Layout") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = bottomTab == "filters",
                    onClick = { bottomTab = "filters" },
                    label = { Text("Filters") },
                    leadingIcon = { Icon(Icons.Default.Filter, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = bottomTab == "style",
                    onClick = { bottomTab = "style" },
                    label = { Text("Borders") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = bottomTab == "caption",
                    onClick = { bottomTab = "caption" },
                    label = { Text("Caption") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Bottom Controller Tray
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    when (bottomTab) {
                        "layout" -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf(
                                    TwoFrameLayout.SIDE_BY_SIDE to "Side by Side",
                                    TwoFrameLayout.TOP_BOTTOM to "Top & Bottom",
                                    TwoFrameLayout.BEFORE_AFTER to "Before & After",
                                    TwoFrameLayout.COUPLE_ROMANCE to "Couple Romance",
                                    TwoFrameLayout.BEST_FRIENDS to "Best Friends",
                                    TwoFrameLayout.PICTURE_IN_PICTURE to "PIP Inset"
                                ).forEach { (layout, label) ->
                                    val isSelected = selectedLayout == layout
                                    Card(
                                        modifier = Modifier
                                            .width(96.dp)
                                            .clickable { selectedLayout = layout }
                                            .testTag("layout_${layout.name}"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = when (layout) {
                                                    TwoFrameLayout.SIDE_BY_SIDE -> "❚ ❚"
                                                    TwoFrameLayout.TOP_BOTTOM -> "☰"
                                                    TwoFrameLayout.BEFORE_AFTER -> "⇄"
                                                    TwoFrameLayout.COUPLE_ROMANCE -> "💖"
                                                    TwoFrameLayout.BEST_FRIENDS -> "👥"
                                                    TwoFrameLayout.PICTURE_IN_PICTURE -> "▣"
                                                },
                                                fontSize = 24.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        "filters" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Filter for Photo ${activePhotoTarget} (${if (activePhotoTarget == 1) photo1Filter else photo2Filter})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(top = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("none" to "Normal", "bw" to "B&W", "vivid" to "Vivid", "warm" to "Warm", "sepia" to "Sepia", "cinematic" to "Cinema", "hdr" to "HDR", "neon" to "Neon").forEach { (fid, fname) ->
                                        val curF = if (activePhotoTarget == 1) photo1Filter else photo2Filter
                                        FilterChip(
                                            selected = curF == fid,
                                            onClick = {
                                                if (activePhotoTarget == 1) photo1Filter = fid else photo2Filter = fid
                                            },
                                            label = { Text(fname) }
                                        )
                                    }
                                }
                            }
                        }

                        "style" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                ValueSliderRow(
                                    label = "Divider Spacing",
                                    value = dividerSpacing,
                                    range = 0f..28f,
                                    onValueChange = { dividerSpacing = it }
                                )
                                ValueSliderRow(
                                    label = "Corner Radius",
                                    value = cornerRadiusDp,
                                    range = 0f..32f,
                                    onValueChange = { cornerRadiusDp = it }
                                )
                                ValueSliderRow(
                                    label = "Border Width",
                                    value = borderWidthDp,
                                    range = 0f..12f,
                                    onValueChange = { borderWidthDp = it }
                                )
                                Text("Border Color", style = MaterialTheme.typography.labelMedium)
                                PaletteColorPicker(
                                    selectedColor = borderColor,
                                    onColorSelected = { borderColor = it }
                                )
                            }
                        }

                        "caption" -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                OutlinedTextField(
                                    value = bannerTitle,
                                    onValueChange = { bannerTitle = it },
                                    label = { Text("Frame Caption / Subtitle") },
                                    modifier = Modifier.fillMaxWidth().testTag("two_frame_caption_input")
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilterChip(
                                        selected = showBanner,
                                        onClick = { showBanner = !showBanner },
                                        label = { Text(if (showBanner) "Banner Shown" else "Banner Hidden") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
