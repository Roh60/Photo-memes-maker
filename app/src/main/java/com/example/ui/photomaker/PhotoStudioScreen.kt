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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.data.model.LayerItem
import com.example.data.model.LayerOrderAction
import com.example.data.model.LayerUtils
import com.example.ui.components.LayerManagementSidebar
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AVAILABLE_FILTER_PRESETS
import com.example.data.model.MemeTextBox
import com.example.data.model.PhotoAdjustment
import com.example.data.model.PlacedSticker
import com.example.data.model.StickerLibrary
import com.example.data.repository.CreationsRepository
import com.example.ui.components.PaletteColorPicker
import com.example.ui.components.StudioTopBar
import com.example.ui.components.ValueSliderRow
import com.example.util.BitmapUtils
import com.example.util.SampleImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class StudioTab {
    FILTERS,
    ADJUST,
    TRANSFORM,
    TEXT,
    STICKERS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoStudioScreen(
    initialBitmap: Bitmap? = null,
    onLaunchCamera: (() -> Unit)? = null,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { CreationsRepository(context) }

    // Source Bitmap state
    var sourceBitmap by remember {
        mutableStateOf<Bitmap?>(initialBitmap ?: SampleImages.createScenicSample(800, 800, "Sunset Glow"))
    }

    var isComparingOriginal by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // Adjustments
    var adjustment by remember { mutableStateOf(PhotoAdjustment()) }
    var history by remember { mutableStateOf(listOf(PhotoAdjustment())) }
    var historyIndex by remember { mutableIntStateOf(0) }

    // Keep the editor in sync when a new gallery/camera bitmap arrives.
    LaunchedEffect(initialBitmap) {
        if (initialBitmap != null && initialBitmap !== sourceBitmap) {
            sourceBitmap = initialBitmap
            adjustment = PhotoAdjustment()
            history = listOf(PhotoAdjustment())
            historyIndex = 0
        }
    }

    // Aspect ratio: 1.0 (1:1), 0.8 (4:5), 1.777 (16:9), 0.5625 (9:16)
    var selectedAspectRatio by remember { mutableFloatStateOf(1.0f) }

    // Text Overlays
    val textBoxes = remember { mutableStateListOf<MemeTextBox>() }
    var activeTextBox by remember { mutableStateOf<MemeTextBox?>(null) }
    var showTextSheet by remember { mutableStateOf(false) }

    // Sticker Overlays
    val placedStickers = remember { mutableStateListOf<PlacedSticker>() }
    var showStickerSheet by remember { mutableStateOf(false) }

    // Layer Management Sidebar & Ordering
    var showLayersSidebar by remember { mutableStateOf(false) }
    val layerOrder = remember { mutableStateListOf<String>() }

    val currentLayers = remember(
        textBoxes.toList(), placedStickers.toList(), layerOrder.toList()
    ) {
        LayerUtils.buildSortedLayers(textBoxes.toList(), placedStickers.toList(), layerOrder.toList())
    }

    // Studio Tool Tab
    var currentTab by remember { mutableStateOf(StudioTab.FILTERS) }

    // Processed preview bitmap
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Coroutine to recalculate processed bitmap whenever adjustments or source change
    LaunchedEffect(sourceBitmap, adjustment) {
        val src = sourceBitmap ?: return@LaunchedEffect
        withContext(Dispatchers.Default) {
            val cm = BitmapUtils.createFilterColorMatrix(
                brightness = adjustment.brightness,
                contrast = adjustment.contrast,
                saturation = adjustment.saturation,
                warmth = adjustment.warmth,
                filterType = adjustment.filterPreset
            )
            val processed = BitmapUtils.processBitmap(
                source = src,
                colorMatrix = cm,
                rotationDegrees = adjustment.rotation,
                flipH = adjustment.flipH,
                flipV = adjustment.flipV
            )
            previewBitmap = processed
        }
    }

    // Media pickers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isProcessing = true
                try {
                    val bmp = BitmapUtils.decodeUriToBitmap(context, uri)
                    if (bmp != null) {
                        sourceBitmap = bmp
                        adjustment = PhotoAdjustment()
                        history = listOf(PhotoAdjustment())
                        historyIndex = 0
                    } else {
                        Toast.makeText(
                            context,
                            "Photo decode nahi hui. Dusri image try karein.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (_: Exception) {
                    Toast.makeText(
                        context,
                        "Photo load karte waqt error aaya.",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    isProcessing = false
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            sourceBitmap = bmp
            adjustment = PhotoAdjustment()
            history = listOf(PhotoAdjustment())
            historyIndex = 0
        }
    }

    // Export renderer
    fun generateFinalBitmap(): Bitmap {
        val base = previewBitmap ?: sourceBitmap ?: Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
        val result = Bitmap.createBitmap(base.width, base.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(result)
        canvas.drawBitmap(base, 0f, 0f, null)

        val stickerPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            textSize = base.width * 0.12f
            textAlign = android.graphics.Paint.Align.CENTER
        }

        // Draw layers in stack order, skipping hidden layers
        for (layer in currentLayers) {
            if (!layer.isVisible) continue
            when (layer) {
                is LayerItem.Sticker -> {
                    val stk = layer.sticker
                    canvas.drawText(stk.emoji, stk.xPercent * base.width, stk.yPercent * base.height, stickerPaint)
                }
                is LayerItem.Text -> {
                    val tb = layer.textBox
                    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = tb.textColor.toInt()
                        textSize = base.width * (tb.fontSizeSp / 350f)
                        textAlign = when (tb.textAlign) {
                            "left" -> android.graphics.Paint.Align.LEFT
                            "right" -> android.graphics.Paint.Align.RIGHT
                            else -> android.graphics.Paint.Align.CENTER
                        }
                        typeface = if (tb.isBold) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
                    }
                    val strokePaint = android.graphics.Paint(paint).apply {
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = paint.textSize * 0.12f
                        color = tb.strokeColor.toInt()
                    }
                    val textToDraw = if (tb.isAllCaps) tb.text.uppercase() else tb.text
                    val x = tb.xPercent * base.width
                    val y = tb.yPercent * base.height
                    canvas.drawText(textToDraw, x, y, strokePaint)
                    canvas.drawText(textToDraw, x, y, paint)
                }
            }
        }

        return result
    }

    Scaffold(
        topBar = {
            StudioTopBar(
                title = "Photo Studio",
                onBack = onNavigateBack,
                actions = {
                    // Layer Management Sidebar button
                    IconButton(
                        onClick = { showLayersSidebar = !showLayersSidebar },
                        modifier = Modifier.testTag("photo_layers_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (currentLayers.isNotEmpty()) {
                                    Badge {
                                        Text("${currentLayers.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = "Layers Manager",
                                tint = if (showLayersSidebar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Undo button
                    IconButton(
                        onClick = {
                            if (historyIndex > 0) {
                                historyIndex--
                                adjustment = history[historyIndex]
                            }
                        },
                        enabled = historyIndex > 0,
                        modifier = Modifier.testTag("undo_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                    }

                    // Reset button
                    IconButton(
                        onClick = {
                            adjustment = PhotoAdjustment()
                            history = listOf(PhotoAdjustment())
                            historyIndex = 0
                            textBoxes.clear()
                            placedStickers.clear()
                            Toast.makeText(context, "Edits reset", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("reset_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                    }

                    // Native Share button
                    IconButton(
                        onClick = {
                            val finalBmp = generateFinalBitmap()
                            BitmapUtils.shareBitmap(context, finalBmp, "Edited with Photo & Memes Maker")
                        },
                        modifier = Modifier.testTag("share_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }

                    // Save / Export button
                    IconButton(
                        onClick = {
                            scope.launch {
                                isProcessing = true
                                try {
                                    val finalBmp = generateFinalBitmap()
                                    val uri = BitmapUtils.saveBitmapToGallery(context, finalBmp)
                                    repository.saveCreation(finalBmp, "Edited Photo", "photo")
                                    Toast.makeText(
                                        context,
                                        if (uri != null) "Saved to Gallery and Creations" else "Saved to My Creations; Gallery save failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Couldn't save photo. Please try again.", Toast.LENGTH_LONG).show()
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        modifier = Modifier.testTag("save_button")
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
            // Main Canvas Preview Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                val displayBmp = if (isComparingOriginal) sourceBitmap else (previewBitmap ?: sourceBitmap)

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF18181B)),
                    contentAlignment = Alignment.Center
                ) {
                    val canvasWidth = constraints.maxWidth.toFloat()
                    val canvasHeight = constraints.maxHeight.toFloat()

                    if (displayBmp != null) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures { offset ->
                                        // Tap to select text box or place sticker
                                    }
                                }
                                .testTag("photo_editor_canvas")
                        ) {
                            val imgBmp = displayBmp.asImageBitmap()
                            val scale = minOf(
                                size.width / imgBmp.width.toFloat(),
                                size.height / imgBmp.height.toFloat()
                            )
                            val destW = imgBmp.width * scale
                            val destH = imgBmp.height * scale
                            val left = (size.width - destW) / 2f
                            val top = (size.height - destH) / 2f

                            drawImage(
                                image = imgBmp,
                                dstOffset = androidx.compose.ui.unit.IntOffset(left.toInt(), top.toInt()),
                                dstSize = androidx.compose.ui.unit.IntSize(destW.toInt(), destH.toInt())
                            )

                            // Render layers in stack order, skipping hidden layers
                            drawIntoCanvas { nativeCanvas ->
                                val stickerPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                    textSize = destW * 0.12f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                                for (layer in currentLayers) {
                                    if (!layer.isVisible) continue
                                    when (layer) {
                                        is LayerItem.Sticker -> {
                                            val stk = layer.sticker
                                            val sx = left + stk.xPercent * destW
                                            val sy = top + stk.yPercent * destH
                                            nativeCanvas.nativeCanvas.drawText(stk.emoji, sx, sy, stickerPaint)
                                        }
                                        is LayerItem.Text -> {
                                            val tb = layer.textBox
                                            val tPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                                color = tb.textColor.toInt()
                                                textSize = destW * (tb.fontSizeSp / 350f)
                                                textAlign = when (tb.textAlign) {
                                                    "left" -> android.graphics.Paint.Align.LEFT
                                                    "right" -> android.graphics.Paint.Align.RIGHT
                                                    else -> android.graphics.Paint.Align.CENTER
                                                }
                                                typeface = if (tb.isBold) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
                                            }
                                            val sPaint = android.graphics.Paint(tPaint).apply {
                                                style = android.graphics.Paint.Style.STROKE
                                                strokeWidth = tPaint.textSize * 0.12f
                                                color = tb.strokeColor.toInt()
                                            }
                                            val textStr = if (tb.isAllCaps) tb.text.uppercase() else tb.text
                                            val tx = left + tb.xPercent * destW
                                            val ty = top + tb.yPercent * destH
                                            nativeCanvas.nativeCanvas.drawText(textStr, tx, ty, sPaint)
                                            nativeCanvas.nativeCanvas.drawText(textStr, tx, ty, tPaint)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        CircularProgressIndicator()
                    }

                    // "Hold to View Original" Button
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isComparingOriginal = true
                                        tryAwaitRelease()
                                        isComparingOriginal = false
                                    }
                                )
                            }
                            .testTag("compare_original_button"),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Original",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isComparingOriginal) "Original" else "Hold for Original",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }

                    // Quick image replace buttons
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.clickable {
                                if (onLaunchCamera != null) onLaunchCamera() else cameraLauncher.launch(null)
                            }.testTag("studio_camera_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.clickable {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }.testTag("studio_gallery_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                    }
                }
            }

            // Studio Tool Tabs (Filters, Adjust, Transform, Text, Stickers)
            ScrollableTabRow(
                selectedTabIndex = currentTab.ordinal,
                edgePadding = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = currentTab == StudioTab.FILTERS,
                    onClick = { currentTab = StudioTab.FILTERS },
                    text = { Text("Filters") },
                    icon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = currentTab == StudioTab.ADJUST,
                    onClick = { currentTab = StudioTab.ADJUST },
                    text = { Text("Adjust") },
                    icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = currentTab == StudioTab.TRANSFORM,
                    onClick = { currentTab = StudioTab.TRANSFORM },
                    text = { Text("Transform") },
                    icon = { Icon(Icons.Default.Crop, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = currentTab == StudioTab.TEXT,
                    onClick = { currentTab = StudioTab.TEXT },
                    text = { Text("Text") },
                    icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = currentTab == StudioTab.STICKERS,
                    onClick = { currentTab = StudioTab.STICKERS },
                    text = { Text("Stickers") },
                    icon = { Icon(Icons.Default.EmojiEmotions, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            // Bottom Tool Control Tray
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    when (currentTab) {
                        StudioTab.FILTERS -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AVAILABLE_FILTER_PRESETS.forEach { preset ->
                                    val isSelected = adjustment.filterPreset == preset.id
                                    Card(
                                        modifier = Modifier
                                            .width(74.dp)
                                            .clickable {
                                                adjustment = adjustment.copy(filterPreset = preset.id)
                                                history = history.take(historyIndex + 1) + adjustment
                                                historyIndex++
                                            }
                                            .testTag("filter_preset_${preset.id}"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(preset.previewHue)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = preset.name.take(2).uppercase(),
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = preset.name,
                                                style = MaterialTheme.typography.labelSmall,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        StudioTab.ADJUST -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                ValueSliderRow(
                                    label = "Brightness",
                                    value = adjustment.brightness,
                                    range = -80f..80f,
                                    onValueChange = { adjustment = adjustment.copy(brightness = it) }
                                )
                                ValueSliderRow(
                                    label = "Contrast",
                                    value = adjustment.contrast,
                                    range = 0.5f..2.0f,
                                    onValueChange = { adjustment = adjustment.copy(contrast = it) }
                                )
                                ValueSliderRow(
                                    label = "Saturation",
                                    value = adjustment.saturation,
                                    range = 0.0f..2.2f,
                                    onValueChange = { adjustment = adjustment.copy(saturation = it) }
                                )
                                ValueSliderRow(
                                    label = "Warmth",
                                    value = adjustment.warmth,
                                    range = -50f..50f,
                                    onValueChange = { adjustment = adjustment.copy(warmth = it) }
                                )
                            }
                        }

                        StudioTab.TRANSFORM -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Rotation & Flips",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            val newRot = (adjustment.rotation + 90f) % 360f
                                            adjustment = adjustment.copy(rotation = newRot)
                                        },
                                        modifier = Modifier.weight(1f).testTag("rotate_90_btn")
                                    ) {
                                        Icon(Icons.Default.RotateRight, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rotate 90°")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            adjustment = adjustment.copy(flipH = !adjustment.flipH)
                                        },
                                        modifier = Modifier.weight(1f).testTag("flip_h_btn")
                                    ) {
                                        Icon(Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Flip H")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            adjustment = adjustment.copy(flipV = !adjustment.flipV)
                                        },
                                        modifier = Modifier.weight(1f).testTag("flip_v_btn")
                                    ) {
                                        Icon(Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Flip V")
                                    }
                                }
                            }
                        }

                        StudioTab.TEXT -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${textBoxes.size} Text Box(es)",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Button(
                                        onClick = {
                                            val newBox = MemeTextBox(
                                                text = "Tap to Edit Text",
                                                xPercent = 0.5f,
                                                yPercent = 0.4f + (textBoxes.size * 0.12f)
                                            )
                                            textBoxes.add(newBox)
                                            layerOrder.add(newBox.id)
                                            activeTextBox = newBox
                                            showTextSheet = true
                                        },
                                        modifier = Modifier.testTag("add_text_button")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add Text")
                                    }
                                }
                                if (textBoxes.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState())
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        textBoxes.forEachIndexed { idx, box ->
                                            FilterChip(
                                                selected = activeTextBox == box,
                                                onClick = {
                                                    activeTextBox = box
                                                    showTextSheet = true
                                                },
                                                label = { Text("Text ${idx + 1}: ${box.text.take(10)}") },
                                                trailingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Remove",
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clickable { textBoxes.remove(box) }
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        StudioTab.STICKERS -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Add Stickers & Emojis",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Button(
                                        onClick = { showStickerSheet = true },
                                        modifier = Modifier.testTag("open_stickers_btn")
                                    ) {
                                        Icon(Icons.Default.EmojiEmotions, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Browse Stickers")
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("✨", "❤️", "🔥", "👑", "🪔", "🎨", "🎉", "🕶️", "💯", "🎂").forEach { emoji ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clickable {
                                                    val newStk = PlacedSticker(emoji = emoji)
                                                    placedStickers.add(newStk)
                                                    layerOrder.add(newStk.id)
                                                }
                                                .testTag("quick_sticker_$emoji")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(emoji, fontSize = 22.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet for Editing Selected Text Box
    if (showTextSheet && activeTextBox != null) {
        val target = activeTextBox!!
        var textContent by remember { mutableStateOf(target.text) }
        var fontSize by remember { mutableFloatStateOf(target.fontSizeSp) }
        var isBold by remember { mutableStateOf(target.isBold) }
        var isCaps by remember { mutableStateOf(target.isAllCaps) }
        var textColor by remember { mutableStateOf(target.textColor) }
        var strokeColor by remember { mutableStateOf(target.strokeColor) }

        ModalBottomSheet(
            onDismissRequest = { showTextSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Edit Text Overlay", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = textContent,
                    onValueChange = {
                        textContent = it
                        target.text = it
                    },
                    label = { Text("Text message") },
                    modifier = Modifier.fillMaxWidth().testTag("text_input_field")
                )
                Spacer(modifier = Modifier.height(10.dp))
                ValueSliderRow(
                    label = "Font Size",
                    value = fontSize,
                    range = 14f..64f,
                    onValueChange = {
                        fontSize = it
                        target.fontSizeSp = it
                    }
                )
                Text("Text Color", style = MaterialTheme.typography.labelMedium)
                PaletteColorPicker(
                    selectedColor = textColor,
                    onColorSelected = {
                        textColor = it
                        target.textColor = it
                    }
                )
                Text("Stroke / Outline Color", style = MaterialTheme.typography.labelMedium)
                PaletteColorPicker(
                    selectedColor = strokeColor,
                    onColorSelected = {
                        strokeColor = it
                        target.strokeColor = it
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isBold,
                        onClick = {
                            isBold = !isBold
                            target.isBold = isBold
                        },
                        label = { Text("Bold") }
                    )
                    FilterChip(
                        selected = isCaps,
                        onClick = {
                            isCaps = !isCaps
                            target.isAllCaps = isCaps
                        },
                        label = { Text("ALL CAPS") }
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { showTextSheet = false },
                    modifier = Modifier.fillMaxWidth().testTag("text_sheet_done_btn")
                ) {
                    Text("Apply & Close")
                }
            }
        }
    }

    // Modal Sheet for Stickers
    if (showStickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showStickerSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Choose Sticker", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StickerLibrary.STICKERS.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .size(56.dp)
                                .clickable {
                                    val newSticker = PlacedSticker(emoji = item.emoji)
                                    placedStickers.add(newSticker)
                                    layerOrder.add(newSticker.id)
                                    showStickerSheet = false
                                }
                                .testTag("sticker_pick_${item.label}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(item.emoji, fontSize = 28.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Layer Management Sidebar
    LayerManagementSidebar(
        isOpen = showLayersSidebar,
        onClose = { showLayersSidebar = false },
        layers = currentLayers,
        selectedLayerId = activeTextBox?.id,
        onSelectLayer = { id ->
            val tb = textBoxes.firstOrNull { it.id == id }
            if (tb != null) {
                activeTextBox = tb
                showTextSheet = true
            }
        },
        onReorderLayer = { id, action ->
            val updated = LayerUtils.reorder(layerOrder, id, action)
            layerOrder.clear()
            layerOrder.addAll(updated)
        },
        onToggleVisibility = { id ->
            val tbIdx = textBoxes.indexOfFirst { it.id == id }
            if (tbIdx != -1) {
                textBoxes[tbIdx] = textBoxes[tbIdx].copy(isVisible = !textBoxes[tbIdx].isVisible)
            }
            val stkIdx = placedStickers.indexOfFirst { it.id == id }
            if (stkIdx != -1) {
                placedStickers[stkIdx] = placedStickers[stkIdx].copy(isVisible = !placedStickers[stkIdx].isVisible)
            }
        },
        onToggleLock = { id ->
            val tbIdx = textBoxes.indexOfFirst { it.id == id }
            if (tbIdx != -1) {
                textBoxes[tbIdx] = textBoxes[tbIdx].copy(isLocked = !textBoxes[tbIdx].isLocked)
            }
            val stkIdx = placedStickers.indexOfFirst { it.id == id }
            if (stkIdx != -1) {
                placedStickers[stkIdx] = placedStickers[stkIdx].copy(isLocked = !placedStickers[stkIdx].isLocked)
            }
        },
        onDeleteLayer = { id ->
            textBoxes.removeAll { it.id == id }
            placedStickers.removeAll { it.id == id }
            layerOrder.remove(id)
            if (activeTextBox?.id == id) {
                activeTextBox = null
            }
        },
        onAddText = {
            val newBox = MemeTextBox(
                text = "Tap to Edit Text",
                xPercent = 0.5f,
                yPercent = 0.5f
            )
            textBoxes.add(newBox)
            layerOrder.add(newBox.id)
            activeTextBox = newBox
            showTextSheet = true
            showLayersSidebar = false
        },
        onAddSticker = {
            showStickerSheet = true
            showLayersSidebar = false
        },
        onShowAll = {
            for (i in textBoxes.indices) {
                if (!textBoxes[i].isVisible) {
                    textBoxes[i] = textBoxes[i].copy(isVisible = true)
                }
            }
            for (i in placedStickers.indices) {
                if (!placedStickers[i].isVisible) {
                    placedStickers[i] = placedStickers[i].copy(isVisible = true)
                }
            }
        },
        onHideAll = {
            for (i in textBoxes.indices) {
                if (textBoxes[i].isVisible) {
                    textBoxes[i] = textBoxes[i].copy(isVisible = false)
                }
            }
            for (i in placedStickers.indices) {
                if (placedStickers[i].isVisible) {
                    placedStickers[i] = placedStickers[i].copy(isVisible = false)
                }
            }
        }
    )
}
