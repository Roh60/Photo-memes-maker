package com.example.ui.mememaker

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.CaptionLibrary
import com.example.data.model.MemeLayoutType
import com.example.data.model.MemeTextBox
import com.example.data.model.MemeTemplateRepository
import com.example.data.model.PlacedSticker
import com.example.data.model.StickerLibrary
import com.example.data.repository.CreationsRepository
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import com.example.data.model.LayerItem
import com.example.data.model.LayerOrderAction
import com.example.data.model.LayerUtils
import com.example.ui.components.DraggableMemeEditor
import com.example.ui.components.DraggableScalableRotatableStickerOverlay
import com.example.ui.components.LayerManagementSidebar
import com.example.ui.components.MemeFilterControls
import com.example.ui.components.PaletteColorPicker
import com.example.ui.components.StudioTopBar
import com.example.ui.components.ValueSliderRow
import com.example.util.BitmapUtils
import com.example.util.SampleImages
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeStudioScreen(
    templateId: String? = null,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { CreationsRepository(context) }

    val presetTemplate = remember(templateId) {
        if (templateId != null) MemeTemplateRepository.TEMPLATES.firstOrNull { it.id == templateId } else null
    }

    var memeLayout by remember {
        mutableStateOf(presetTemplate?.layoutType ?: MemeLayoutType.CLASSIC_TOP_BOTTOM)
    }

    // Texts
    var topText by remember { mutableStateOf(presetTemplate?.defaultTopText ?: "WHEN YOU TRY TO STUDY") }
    var bottomText by remember { mutableStateOf(presetTemplate?.defaultBottomText ?: "AND END UP MAKING MEMES") }
    var topHeaderPanel by remember { mutableStateOf(presetTemplate?.panel1Header ?: "EXPECTATION") }
    var bottomHeaderPanel by remember { mutableStateOf(presetTemplate?.panel2Header ?: "REALITY") }

    // Dynamic positioning and sizing for Draggable & Resizable Meme Editor
    var topTextX by remember { mutableFloatStateOf(0.5f) }
    var topTextY by remember { mutableFloatStateOf(0.12f) }
    var topTextSize by remember { mutableFloatStateOf(28f) }

    var bottomTextX by remember { mutableFloatStateOf(0.5f) }
    var bottomTextY by remember { mutableFloatStateOf(0.90f) }
    var bottomTextSize by remember { mutableFloatStateOf(28f) }

    var selectedTextBoxId by remember { mutableStateOf<String?>("top_text") }

    var isAllCaps by remember { mutableStateOf(true) }
    var fontSizeSp by remember { mutableFloatStateOf(28f) }
    var textColor by remember { mutableStateOf(0xFFFFFFFF) }
    var strokeColor by remember { mutableStateOf(0xFF000000) }

    // Multi-panel images
    var panel1Bitmap by remember {
        mutableStateOf<Bitmap?>(SampleImages.createMemeGraphic(800, 800, presetTemplate?.sampleGraphicType ?: "smirk"))
    }
    var panel2Bitmap by remember {
        mutableStateOf<Bitmap?>(SampleImages.createMemeGraphic(800, 800, "shock"))
    }

    val filterViewModel: ImageFilterViewModel = viewModel()
    val filterUiState by filterViewModel.uiState.collectAsStateWithLifecycle()
    val filteredPanel1Bitmap by filterViewModel.processedBitmap.collectAsStateWithLifecycle()

    LaunchedEffect(panel1Bitmap) {
        filterViewModel.setSourceBitmap(panel1Bitmap)
    }

    var activeImagePanel by remember { mutableIntStateOf(1) }
    var isProcessing by remember { mutableStateOf(false) }

    // Extra Floating Text Boxes & Placed Stickers
    val extraTextBoxes = remember { mutableStateListOf<MemeTextBox>() }
    val placedStickers = remember { mutableStateListOf<PlacedSticker>() }
    var selectedStickerId by remember { mutableStateOf<String?>(null) }

    // Layer Visibility & Lock States for primary top/bottom captions
    var topTextVisible by remember { mutableStateOf(true) }
    var topTextLocked by remember { mutableStateOf(false) }
    var bottomTextVisible by remember { mutableStateOf(true) }
    var bottomTextLocked by remember { mutableStateOf(false) }

    // Layer Management Sidebar & Stack Order (from index 0 = back to lastIndex = front)
    var showLayersSidebar by remember { mutableStateOf(false) }
    val layerOrder = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        if (!layerOrder.contains("top_text")) layerOrder.add("top_text")
        if (!layerOrder.contains("bottom_text")) layerOrder.add("bottom_text")
    }

    val allMemeTextBoxes = remember(
        topText, topTextX, topTextY, topTextSize, topTextVisible, topTextLocked,
        bottomText, bottomTextX, bottomTextY, bottomTextSize, bottomTextVisible, bottomTextLocked,
        textColor, strokeColor, isAllCaps, extraTextBoxes.toList()
    ) {
        val list = mutableListOf<MemeTextBox>()
        if (topText.isNotEmpty()) {
            list.add(
                MemeTextBox(
                    id = "top_text",
                    text = topText,
                    xPercent = topTextX,
                    yPercent = topTextY,
                    fontSizeSp = topTextSize,
                    textColor = textColor,
                    strokeColor = strokeColor,
                    isAllCaps = isAllCaps,
                    isVisible = topTextVisible,
                    isLocked = topTextLocked
                )
            )
        }
        if (bottomText.isNotEmpty()) {
            list.add(
                MemeTextBox(
                    id = "bottom_text",
                    text = bottomText,
                    xPercent = bottomTextX,
                    yPercent = bottomTextY,
                    fontSizeSp = bottomTextSize,
                    textColor = textColor,
                    strokeColor = strokeColor,
                    isAllCaps = isAllCaps,
                    isVisible = bottomTextVisible,
                    isLocked = bottomTextLocked
                )
            )
        }
        list.addAll(extraTextBoxes)
        list
    }

    // Unified sorted layers in stack order (index 0 is Back, last is Front)
    val currentLayers = remember(
        allMemeTextBoxes, placedStickers.toList(), layerOrder.toList()
    ) {
        LayerUtils.buildSortedLayers(allMemeTextBoxes, placedStickers, layerOrder)
    }

    // Bottom Sheets
    var showCaptionSheet by remember { mutableStateOf(false) }
    var showStickerPickerSheet by remember { mutableStateOf(false) }
    var selectedCaptionCategory by remember { mutableStateOf("All") }

    // Bottom Tool Tray Mode: "text", "layout", "font", "stickers"
    var bottomMode by remember { mutableStateOf("text") }

    // Media pickers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isProcessing = true
                val bmp = BitmapUtils.decodeUriToBitmap(context, uri)
                if (bmp != null) {
                    if (activeImagePanel == 1) panel1Bitmap = bmp else panel2Bitmap = bmp
                }
                isProcessing = false
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            if (activeImagePanel == 1) panel1Bitmap = bmp else panel2Bitmap = bmp
        }
    }

    // High quality export bitmap renderer with exact sticker positions, scale, and rotation
    fun generateMemeExport(): Bitmap {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        val p1 = filteredPanel1Bitmap ?: panel1Bitmap ?: SampleImages.createMemeGraphic(width, height, "smirk")
        val p2 = panel2Bitmap ?: SampleImages.createMemeGraphic(width, height, "shock")

        // Helper to draw impact text at exact coordinates
        fun drawImpactText(txt: String, xPercent: Float, yPercent: Float, sizeSp: Float) {
            if (txt.isEmpty()) return
            val textToDraw = if (isAllCaps) txt.uppercase() else txt
            val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = textColor.toInt()
                textSize = width * (sizeSp / 350f)
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            val stroke = android.graphics.Paint(paint).apply {
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = paint.textSize * 0.16f
                color = strokeColor.toInt()
            }
            val x = width * xPercent
            val y = height * yPercent
            canvas.drawText(textToDraw, x, y, stroke)
            canvas.drawText(textToDraw, x, y, paint)
        }

        // Helper to draw sticker overlay
        fun drawExportSticker(stk: PlacedSticker) {
            val centerX = stk.xPercent * width
            val centerY = stk.yPercent * height
            canvas.save()
            canvas.translate(centerX, centerY)
            canvas.rotate(stk.rotation)

            if (stk.drawableResId != null) {
                val drawable = ContextCompat.getDrawable(context, stk.drawableResId)
                if (drawable != null) {
                    val baseSizePx = width * 0.22f
                    val w = (baseSizePx * stk.scale).toInt()
                    val h = (baseSizePx * stk.scale).toInt()
                    drawable.setBounds(-w / 2, -h / 2, w / 2, h / 2)
                    drawable.draw(canvas)
                }
            } else if (!stk.emoji.isNullOrEmpty()) {
                val stkPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = (width * 0.14f) * stk.scale
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                canvas.drawText(stk.emoji, 0f, stkPaint.textSize * 0.35f, stkPaint)
            }
            canvas.restore()
        }

        // Draw based on layout
        when (memeLayout) {
            MemeLayoutType.CLASSIC_TOP_BOTTOM -> {
                // Single image full canvas
                val rect = android.graphics.Rect(0, 0, width, height)
                canvas.drawBitmap(p1, null, rect, null)

                // Draw all layers in their exact stack order (skipping hidden layers)
                for (layer in currentLayers) {
                    if (!layer.isVisible) continue
                    when (layer) {
                        is LayerItem.Text -> {
                            drawImpactText(layer.textBox.text, layer.textBox.xPercent, layer.textBox.yPercent, layer.textBox.fontSizeSp)
                        }
                        is LayerItem.Sticker -> {
                            drawExportSticker(layer.sticker)
                        }
                    }
                }
            }

            MemeLayoutType.HEADER_CAPTION -> {
                // White header on top with black tweet-style text, image below
                val headerH = height * 0.28f
                val bgPaint = android.graphics.Paint().apply { color = android.graphics.Color.WHITE }
                canvas.drawRect(0f, 0f, width.toFloat(), headerH, bgPaint)

                // Draw header text if visible
                if (topTextVisible) {
                    val textPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.BLACK
                        textSize = 42f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    val lines = topText.split("\n")
                    var curY = 60f
                    for (line in lines) {
                        canvas.drawText(line, 40f, curY, textPaint)
                        curY += 50f
                    }
                }

                // Image in bottom 72%
                val imgRect = android.graphics.RectF(0f, headerH, width.toFloat(), height.toFloat())
                canvas.drawBitmap(p1, null, imgRect, null)
            }

            MemeLayoutType.TWO_PANEL_VERTICAL -> {
                val halfH = height / 2f
                val rect1 = android.graphics.RectF(0f, 0f, width.toFloat(), halfH)
                val rect2 = android.graphics.RectF(0f, halfH, width.toFloat(), height.toFloat())

                canvas.drawBitmap(p1, null, rect1, null)
                canvas.drawBitmap(p2, null, rect2, null)

                // Draw header banners
                val barPaint = android.graphics.Paint().apply { color = android.graphics.Color.BLACK; alpha = 190 }
                canvas.drawRect(0f, 0f, width.toFloat(), 70f, barPaint)
                canvas.drawRect(0f, halfH, width.toFloat(), halfH + 70f, barPaint)

                val txtPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.WHITE
                    textSize = 36f
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                canvas.drawText(topHeaderPanel, width * 0.5f, 50f, txtPaint)
                canvas.drawText(bottomHeaderPanel, width * 0.5f, halfH + 50f, txtPaint)

                // Captions
                val capPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.WHITE
                    textSize = 40f
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setShadowLayer(8f, 0f, 3f, android.graphics.Color.BLACK)
                }
                if (topTextVisible) canvas.drawText(topText, width * 0.5f, halfH - 24f, capPaint)
                if (bottomTextVisible) canvas.drawText(bottomText, width * 0.5f, height - 24f, capPaint)
            }

            else -> {
                // DRAKE / COMPARISON: Left side emoji / reaction, right side text
                val halfH = height / 2f
                val leftW = width * 0.45f
                val rect1 = android.graphics.RectF(0f, 0f, leftW, halfH)
                val rect2 = android.graphics.RectF(0f, halfH, leftW, height.toFloat())
                canvas.drawBitmap(p1, null, rect1, null)
                canvas.drawBitmap(p2, null, rect2, null)

                // Right panels: white card backgrounds
                val cardPaint = android.graphics.Paint().apply { color = android.graphics.Color.WHITE }
                canvas.drawRect(leftW, 0f, width.toFloat(), halfH, cardPaint)
                canvas.drawRect(leftW, halfH, width.toFloat(), height.toFloat(), cardPaint)

                val linePaint = android.graphics.Paint().apply { color = android.graphics.Color.DKGRAY; strokeWidth = 6f }
                canvas.drawLine(leftW, 0f, leftW, height.toFloat(), linePaint)
                canvas.drawLine(0f, halfH, width.toFloat(), halfH, linePaint)

                val tPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.BLACK
                    textSize = 44f
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                if (topTextVisible) canvas.drawText(topText, leftW + (width - leftW) / 2f, halfH * 0.5f, tPaint)
                if (bottomTextVisible) canvas.drawText(bottomText, leftW + (width - leftW) / 2f, halfH + halfH * 0.5f, tPaint)
            }
        }

        // For non-classic layouts, draw placed stickers and extra floating text respecting layer order
        if (memeLayout != MemeLayoutType.CLASSIC_TOP_BOTTOM) {
            for (layer in currentLayers) {
                if (!layer.isVisible) continue
                when (layer) {
                    is LayerItem.Sticker -> drawExportSticker(layer.sticker)
                    is LayerItem.Text -> {
                        if (layer.textBox.id != "top_text" && layer.textBox.id != "bottom_text") {
                            drawImpactText(layer.textBox.text, layer.textBox.xPercent, layer.textBox.yPercent, layer.textBox.fontSizeSp)
                        }
                    }
                }
            }
        }

        return bitmap
    }

    Scaffold(
        topBar = {
            StudioTopBar(
                title = "Meme Creator",
                onBack = onNavigateBack,
                actions = {
                    // Layer Management Sidebar button in top bar
                    IconButton(
                        onClick = { showLayersSidebar = !showLayersSidebar },
                        modifier = Modifier.testTag("meme_layers_btn")
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
                                contentDescription = "Layer Manager",
                                tint = if (showLayersSidebar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Quick Stickers button in top bar
                    IconButton(
                        onClick = { showStickerPickerSheet = true },
                        modifier = Modifier.testTag("meme_top_stickers_btn")
                    ) {
                        Icon(imageVector = Icons.Default.EmojiEmotions, contentDescription = "Add Stickers", tint = MaterialTheme.colorScheme.primary)
                    }

                    // Caption Ideas button
                    IconButton(
                        onClick = { showCaptionSheet = true },
                        modifier = Modifier.testTag("meme_ideas_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Caption Ideas")
                    }

                    // Save draft button
                    IconButton(
                        onClick = {
                            scope.launch {
                                val bmp = generateMemeExport()
                                repository.saveCreation(bmp, topText.take(20), "meme", isDraft = true)
                                Toast.makeText(context, "Draft Saved to My Creations!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("meme_draft_btn")
                    ) {
                        Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = "Save Draft")
                    }

                    // Share
                    IconButton(
                        onClick = {
                            val bmp = generateMemeExport()
                            BitmapUtils.shareBitmap(context, bmp, "Created with Photo & Memes Maker")
                        },
                        modifier = Modifier.testTag("meme_share_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }

                    // Save
                    IconButton(
                        onClick = {
                            scope.launch {
                                isProcessing = true
                                try {
                                    val bmp = generateMemeExport()
                                    val uri = BitmapUtils.saveBitmapToGallery(context, bmp, "Meme")
                                    repository.saveCreation(bmp, topText.take(20).ifBlank { "My Meme" }, "meme")
                                    Toast.makeText(
                                        context,
                                        if (uri != null) "Meme saved to Gallery and Creations" else "Meme saved to My Creations; Gallery save failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Couldn't save meme. Please try again.", Toast.LENGTH_LONG).show()
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        modifier = Modifier.testTag("meme_save_btn")
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
            // Live Meme Preview Canvas
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    val canvasSide = minOf(constraints.maxWidth, constraints.maxHeight)
                    val canvasSideDp = (canvasSide / LocalContext.current.resources.displayMetrics.density).dp
                    val p1 = filteredPanel1Bitmap ?: panel1Bitmap ?: SampleImages.createMemeGraphic(600, 600, "smirk")
                    val p2 = panel2Bitmap ?: SampleImages.createMemeGraphic(600, 600, "shock")

                    if (memeLayout == MemeLayoutType.CLASSIC_TOP_BOTTOM) {
                        DraggableMemeEditor(
                            imageBitmap = p1,
                            textBoxes = allMemeTextBoxes,
                            selectedTextBoxId = selectedTextBoxId,
                            onSelectTextBox = {
                                selectedTextBoxId = it
                                if (it != null) selectedStickerId = null
                            },
                            onUpdateTextBox = { updated ->
                                when (updated.id) {
                                    "top_text" -> {
                                        topText = updated.text
                                        topTextX = updated.xPercent
                                        topTextY = updated.yPercent
                                        topTextSize = updated.fontSizeSp
                                    }
                                    "bottom_text" -> {
                                        bottomText = updated.text
                                        bottomTextX = updated.xPercent
                                        bottomTextY = updated.yPercent
                                        bottomTextSize = updated.fontSizeSp
                                    }
                                    else -> {
                                        val idx = extraTextBoxes.indexOfFirst { it.id == updated.id }
                                        if (idx != -1) {
                                            extraTextBoxes[idx] = updated
                                        }
                                    }
                                }
                            },
                            onDeleteTextBox = { id ->
                                when (id) {
                                    "top_text" -> topText = ""
                                    "bottom_text" -> bottomText = ""
                                    else -> extraTextBoxes.removeAll { it.id == id }
                                }
                                layerOrder.remove(id)
                                if (selectedTextBoxId == id) selectedTextBoxId = null
                            },
                            stickers = placedStickers,
                            selectedStickerId = selectedStickerId,
                            layerOrder = layerOrder,
                            onSelectSticker = {
                                selectedStickerId = it
                                if (it != null) {
                                    selectedTextBoxId = null
                                    bottomMode = "stickers"
                                }
                            },
                            onUpdateSticker = { updated ->
                                val idx = placedStickers.indexOfFirst { it.id == updated.id }
                                if (idx != -1) {
                                    placedStickers[idx] = updated
                                }
                            },
                            onDeleteSticker = { id ->
                                placedStickers.removeAll { it.id == id }
                                layerOrder.remove(id)
                                if (selectedStickerId == id) selectedStickerId = null
                            },
                            modifier = Modifier.size(canvasSideDp)
                        )
                    } else {
                        // Multi-panel layouts with interactive sticker overlays
                        Box(
                            modifier = Modifier.size(canvasSideDp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("meme_live_canvas")
                            ) {
                                when (memeLayout) {
                                    MemeLayoutType.CLASSIC_TOP_BOTTOM -> {}

                                    MemeLayoutType.HEADER_CAPTION -> {
                                        val hH = size.height * 0.28f
                                        drawRect(color = Color.White, size = androidx.compose.ui.geometry.Size(size.width, hH))

                                        drawIntoCanvas {
                                            val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                                color = android.graphics.Color.BLACK
                                                textSize = size.width * 0.046f
                                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                            }
                                            var y = size.height * 0.08f
                                            for (line in topText.split("\n")) {
                                                it.nativeCanvas.drawText(line, size.width * 0.04f, y, paint)
                                                y += size.width * 0.055f
                                            }
                                        }

                                        drawImage(
                                            image = p1.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(0, hH.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(size.width.toInt(), (size.height - hH).toInt())
                                        )
                                    }

                                    MemeLayoutType.TWO_PANEL_VERTICAL -> {
                                        val halfH = size.height / 2f
                                        drawImage(
                                            image = p1.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(0, 0),
                                            dstSize = androidx.compose.ui.unit.IntSize(size.width.toInt(), halfH.toInt())
                                        )
                                        drawImage(
                                            image = p2.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(0, halfH.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(size.width.toInt(), halfH.toInt())
                                        )

                                        drawIntoCanvas {
                                            val bp = android.graphics.Paint().apply { color = android.graphics.Color.BLACK; alpha = 180 }
                                            it.nativeCanvas.drawRect(0f, 0f, size.width, size.height * 0.08f, bp)
                                            it.nativeCanvas.drawRect(0f, halfH, size.width, halfH + size.height * 0.08f, bp)

                                            val hp = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                                color = android.graphics.Color.WHITE
                                                textSize = size.width * 0.045f
                                                textAlign = android.graphics.Paint.Align.CENTER
                                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                            }
                                            it.nativeCanvas.drawText(topHeaderPanel, size.width * 0.5f, size.height * 0.055f, hp)
                                            it.nativeCanvas.drawText(bottomHeaderPanel, size.width * 0.5f, halfH + size.height * 0.055f, hp)

                                            val cp = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                                color = android.graphics.Color.WHITE
                                                textSize = size.width * 0.042f
                                                textAlign = android.graphics.Paint.Align.CENTER
                                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                                setShadowLayer(6f, 0f, 2f, android.graphics.Color.BLACK)
                                            }
                                            it.nativeCanvas.drawText(topText, size.width * 0.5f, halfH - 14f, cp)
                                            it.nativeCanvas.drawText(bottomText, size.width * 0.5f, size.height - 14f, cp)
                                        }
                                    }

                                    else -> {
                                        val halfH = size.height / 2f
                                        val leftW = size.width * 0.45f
                                        drawImage(
                                            image = p1.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(0, 0),
                                            dstSize = androidx.compose.ui.unit.IntSize(leftW.toInt(), halfH.toInt())
                                        )
                                        drawImage(
                                            image = p2.asImageBitmap(),
                                            dstOffset = androidx.compose.ui.unit.IntOffset(0, halfH.toInt()),
                                            dstSize = androidx.compose.ui.unit.IntSize(leftW.toInt(), halfH.toInt())
                                        )

                                        drawRect(
                                            color = Color.White,
                                            topLeft = Offset(leftW, 0f),
                                            size = androidx.compose.ui.geometry.Size(size.width - leftW, size.height)
                                        )

                                        drawLine(color = Color.DarkGray, start = Offset(leftW, 0f), end = Offset(leftW, size.height), strokeWidth = 3f)
                                        drawLine(color = Color.DarkGray, start = Offset(0f, halfH), end = Offset(size.width, halfH), strokeWidth = 3f)

                                        drawIntoCanvas {
                                            val tp = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                                color = android.graphics.Color.BLACK
                                                textSize = size.width * 0.042f
                                                textAlign = android.graphics.Paint.Align.CENTER
                                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                            }
                                            it.nativeCanvas.drawText(topText, leftW + (size.width - leftW) / 2f, halfH * 0.5f, tp)
                                            it.nativeCanvas.drawText(bottomText, leftW + (size.width - leftW) / 2f, halfH + halfH * 0.5f, tp)
                                        }
                                    }
                                }
                            }

                            // Interactive Placed Stickers Over Multi-panel Layouts
                            val sortedMultiPanelStickers = remember(placedStickers.toList(), layerOrder.toList()) {
                                if (layerOrder.isNotEmpty()) {
                                    placedStickers.sortedBy { s ->
                                        val idx = layerOrder.indexOf(s.id)
                                        if (idx != -1) idx else Int.MAX_VALUE
                                    }
                                } else placedStickers
                            }
                            sortedMultiPanelStickers.forEach { sticker ->
                                if (!sticker.isVisible) return@forEach
                                DraggableScalableRotatableStickerOverlay(
                                    sticker = sticker,
                                    isSelected = sticker.id == selectedStickerId,
                                    imageLeftPx = 0f,
                                    imageTopPx = 0f,
                                    imageWidthPx = canvasSide.toFloat(),
                                    imageHeightPx = canvasSide.toFloat(),
                                    onSelect = {
                                        selectedTextBoxId = null
                                        selectedStickerId = sticker.id
                                        bottomMode = "stickers"
                                    },
                                    onUpdate = { updated ->
                                        val idx = placedStickers.indexOfFirst { it.id == updated.id }
                                        if (idx != -1) placedStickers[idx] = updated
                                    },
                                    onDelete = {
                                        placedStickers.removeAll { it.id == sticker.id }
                                        layerOrder.remove(sticker.id)
                                        if (selectedStickerId == sticker.id) selectedStickerId = null
                                    }
                                )
                            }
                        }
                    }

                    // Panel photo replace controls
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier.clickable { cameraLauncher.launch(null) }.testTag("meme_cam_btn")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White, modifier = Modifier.padding(6.dp).size(18.dp))
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier.clickable {
                                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }.testTag("meme_gallery_btn")
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.padding(6.dp).size(18.dp))
                        }
                    }
                }
            }

            // Sub Tab Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = bottomMode == "text",
                    onClick = { bottomMode = "text" },
                    label = { Text("Captions") },
                    leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_captions")
                )
                FilterChip(
                    selected = bottomMode == "filters",
                    onClick = { bottomMode = "filters" },
                    label = { Text("Filters") },
                    leadingIcon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_filters")
                )
                FilterChip(
                    selected = bottomMode == "layout",
                    onClick = { bottomMode = "layout" },
                    label = { Text("Layout") },
                    leadingIcon = { Icon(Icons.Default.ViewAgenda, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_layout")
                )
                FilterChip(
                    selected = bottomMode == "font",
                    onClick = { bottomMode = "font" },
                    label = { Text("Style") },
                    modifier = Modifier.testTag("tab_style")
                )
                FilterChip(
                    selected = bottomMode == "stickers",
                    onClick = { bottomMode = "stickers" },
                    label = { Text("Stickers") },
                    leadingIcon = { Icon(Icons.Default.EmojiEmotions, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_stickers")
                )
                FilterChip(
                    selected = bottomMode == "layers" || showLayersSidebar,
                    onClick = {
                        bottomMode = "layers"
                        showLayersSidebar = true
                    },
                    label = { Text("Layers (${currentLayers.size})") },
                    leadingIcon = { Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_layers")
                )
            }

            // Bottom Controller Tray
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (bottomMode) {
                        "text" -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            ) {
                                Text(
                                    text = "👆 Drag text to position. Drag bottom-right corner to resize!",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            OutlinedTextField(
                                value = topText,
                                onValueChange = { topText = it },
                                label = { Text("Top Text / Setup") },
                                modifier = Modifier.fillMaxWidth().testTag("meme_top_text_input")
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = bottomText,
                                onValueChange = { bottomText = it },
                                label = { Text("Bottom Text / Punchline") },
                                modifier = Modifier.fillMaxWidth().testTag("meme_bottom_text_input")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val newBox = MemeTextBox(
                                            text = "EXTRA PUNCHLINE",
                                            xPercent = 0.5f,
                                            yPercent = 0.5f,
                                            fontSizeSp = 24f,
                                            textColor = textColor,
                                            strokeColor = strokeColor,
                                            isAllCaps = isAllCaps
                                        )
                                        extraTextBoxes.add(newBox)
                                        layerOrder.add(newBox.id)
                                        selectedTextBoxId = newBox.id
                                        selectedStickerId = null
                                    },
                                    modifier = Modifier.weight(1f).testTag("add_floating_text_btn")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Add Text")
                                }

                                OutlinedButton(
                                    onClick = {
                                        topTextX = 0.5f
                                        topTextY = 0.12f
                                        topTextSize = 28f
                                        bottomTextX = 0.5f
                                        bottomTextY = 0.90f
                                        bottomTextSize = 28f
                                    },
                                    modifier = Modifier.weight(1f).testTag("reset_text_positions_btn")
                                ) {
                                    Text("Reset Positions")
                                }
                            }
                        }

                        "filters" -> {
                            MemeFilterControls(
                                uiState = filterUiState,
                                onBrightnessChange = { filterViewModel.setBrightness(it) },
                                onContrastChange = { filterViewModel.setContrast(it) },
                                onSaturationChange = { filterViewModel.setSaturation(it) },
                                onSepiaChange = { filterViewModel.setSepia(it) },
                                onApplyPreset = { filterViewModel.applyPreset(it) },
                                onReset = { filterViewModel.resetFilters() }
                            )
                        }

                        "layers" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Layer Stack (${currentLayers.size})",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Reorder, toggle visibility, & lock elements",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Button(
                                        onClick = { showLayersSidebar = true },
                                        modifier = Modifier.testTag("open_layers_sidebar_btn"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Open Manager")
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            topTextVisible = true
                                            bottomTextVisible = true
                                            for (i in extraTextBoxes.indices) {
                                                extraTextBoxes[i] = extraTextBoxes[i].copy(isVisible = true)
                                            }
                                            for (i in placedStickers.indices) {
                                                placedStickers[i] = placedStickers[i].copy(isVisible = true)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Show All")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            topTextLocked = false
                                            bottomTextLocked = false
                                            for (i in extraTextBoxes.indices) {
                                                extraTextBoxes[i] = extraTextBoxes[i].copy(isLocked = false)
                                            }
                                            for (i in placedStickers.indices) {
                                                placedStickers[i] = placedStickers[i].copy(isLocked = false)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Unlock All")
                                    }
                                }
                            }
                        }

                        "layout" -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                listOf(
                                    MemeLayoutType.CLASSIC_TOP_BOTTOM to "Classic Impact",
                                    MemeLayoutType.HEADER_CAPTION to "Header Tweet",
                                    MemeLayoutType.TWO_PANEL_VERTICAL to "2-Panel Vertical",
                                    MemeLayoutType.DRAKE_COMPARISON to "Nah vs Yeah"
                                ).forEach { (layout, label) ->
                                    val isSelected = memeLayout == layout
                                    Card(
                                        modifier = Modifier
                                            .width(100.dp)
                                            .clickable { memeLayout = layout }
                                            .testTag("layout_btn_${layout.name}"),
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
                                                    MemeLayoutType.CLASSIC_TOP_BOTTOM -> "🔤"
                                                    MemeLayoutType.HEADER_CAPTION -> "💬"
                                                    MemeLayoutType.TWO_PANEL_VERTICAL -> "☰"
                                                    else -> "⚖️"
                                                },
                                                fontSize = 24.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }

                        "font" -> {
                            ValueSliderRow(
                                label = "Font Size",
                                value = fontSizeSp,
                                range = 18f..60f,
                                onValueChange = { fontSizeSp = it }
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = isAllCaps,
                                    onClick = { isAllCaps = !isAllCaps },
                                    label = { Text("ALL CAPS (Impact)") }
                                )
                            }
                            Text("Text Color", style = MaterialTheme.typography.labelMedium)
                            PaletteColorPicker(
                                selectedColor = textColor,
                                onColorSelected = { textColor = it }
                            )
                            Text("Outline Stroke Color", style = MaterialTheme.typography.labelMedium)
                            PaletteColorPicker(
                                selectedColor = strokeColor,
                                onColorSelected = { strokeColor = it }
                            )
                        }

                        "stickers" -> {
                            val activeSticker = placedStickers.firstOrNull { it.id == selectedStickerId }
                            if (activeSticker != null) {
                                // Sticker Inspector with real-time sliders and transforms
                                StickerInspectorControls(
                                    selectedSticker = activeSticker,
                                    onUpdateSticker = { updated ->
                                        val idx = placedStickers.indexOfFirst { it.id == updated.id }
                                        if (idx != -1) placedStickers[idx] = updated
                                    },
                                    onDeselect = { selectedStickerId = null },
                                    onDelete = {
                                        placedStickers.removeAll { it.id == activeSticker.id }
                                        selectedStickerId = null
                                    },
                                    onOpenPicker = { showStickerPickerSheet = true }
                                )
                            } else {
                                // Default Stickers Tray
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Overlay Stickers",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Button(
                                            onClick = { showStickerPickerSheet = true },
                                            modifier = Modifier.testTag("open_sticker_picker_btn"),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Open Library")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Meme Drawable Assets (Tap to Add)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Quick drawable stickers horizontal list
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        StickerLibrary.DRAWABLE_STICKERS.forEach { item ->
                                            Card(
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clickable {
                                                        val newStk = PlacedSticker(
                                                            drawableResId = item.resId,
                                                            label = item.name,
                                                            xPercent = 0.5f,
                                                            yPercent = 0.5f,
                                                            scale = 1.0f,
                                                            rotation = 0f
                                                        )
                                                        placedStickers.add(newStk)
                                                        layerOrder.add(newStk.id)
                                                        selectedStickerId = newStk.id
                                                        selectedTextBoxId = null
                                                    }
                                                    .testTag("quick_sticker_${item.id}"),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize().padding(6.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Image(
                                                        painter = painterResource(id = item.resId),
                                                        contentDescription = item.name,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Meme Emojis",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Quick emojis row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf("😂", "💀", "🤡", "👀", "🔥", "🤯", "🤦‍♂️", "🗿", "📈", "📉", "💯", "👑").forEach { emoji ->
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clickable {
                                                        val newStk = PlacedSticker(
                                                            emoji = emoji,
                                                            label = emoji,
                                                            xPercent = 0.5f,
                                                            yPercent = 0.5f,
                                                            scale = 1.0f,
                                                            rotation = 0f
                                                        )
                                                        placedStickers.add(newStk)
                                                        layerOrder.add(newStk.id)
                                                        selectedStickerId = newStk.id
                                                        selectedTextBoxId = null
                                                    }
                                                    .testTag("meme_sticker_$emoji")
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(emoji, fontSize = 24.sp)
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
    }

    // Caption Ideas Library Bottom Sheet
    if (showCaptionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCaptionSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "💡 Tap a Punchline to Apply",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Funny", "Relatable", "Work", "Exams", "Desi", "Sarcastic").forEach { cat ->
                        FilterChip(
                            selected = selectedCaptionCategory == cat,
                            onClick = { selectedCaptionCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val ideas = CaptionLibrary.getIdeasByCategory(selectedCaptionCategory)
                    ideas.forEach { idea ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    topText = idea.top
                                    bottomText = idea.bottom
                                    showCaptionSheet = false
                                    Toast.makeText(context, "Punchline Applied!", Toast.LENGTH_SHORT).show()
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = idea.top,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (idea.bottom.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = idea.bottom,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Sticker Picker Modal Bottom Sheet
    if (showStickerPickerSheet) {
        StickerPickerModalBottomSheet(
            onDismissRequest = { showStickerPickerSheet = false },
            onSelectDrawableSticker = { item ->
                val newStk = PlacedSticker(
                    drawableResId = item.resId,
                    label = item.name,
                    xPercent = 0.5f,
                    yPercent = 0.5f,
                    scale = 1.0f,
                    rotation = 0f
                )
                placedStickers.add(newStk)
                layerOrder.add(newStk.id)
                selectedStickerId = newStk.id
                selectedTextBoxId = null
                bottomMode = "stickers"
                showStickerPickerSheet = false
                Toast.makeText(context, "Added ${item.name} to meme!", Toast.LENGTH_SHORT).show()
            },
            onSelectEmojiSticker = { emojiItem ->
                val newStk = PlacedSticker(
                    emoji = emojiItem.emoji,
                    label = emojiItem.label,
                    xPercent = 0.5f,
                    yPercent = 0.5f,
                    scale = 1.0f,
                    rotation = 0f
                )
                placedStickers.add(newStk)
                layerOrder.add(newStk.id)
                selectedStickerId = newStk.id
                selectedTextBoxId = null
                bottomMode = "stickers"
                showStickerPickerSheet = false
                Toast.makeText(context, "Added ${emojiItem.label} to meme!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Slide-in Layer Management Sidebar
    LayerManagementSidebar(
        isOpen = showLayersSidebar,
        onClose = { showLayersSidebar = false },
        layers = currentLayers,
        selectedLayerId = selectedTextBoxId ?: selectedStickerId,
        onSelectLayer = { id ->
            if (allMemeTextBoxes.any { it.id == id }) {
                selectedTextBoxId = id
                selectedStickerId = null
                bottomMode = "text"
            } else if (placedStickers.any { it.id == id }) {
                selectedStickerId = id
                selectedTextBoxId = null
                bottomMode = "stickers"
            }
        },
        onReorderLayer = { id, action ->
            val updated = LayerUtils.reorder(layerOrder, id, action)
            layerOrder.clear()
            layerOrder.addAll(updated)
        },
        onToggleVisibility = { id ->
            when (id) {
                "top_text" -> topTextVisible = !topTextVisible
                "bottom_text" -> bottomTextVisible = !bottomTextVisible
                else -> {
                    val tbIdx = extraTextBoxes.indexOfFirst { it.id == id }
                    if (tbIdx != -1) {
                        extraTextBoxes[tbIdx] = extraTextBoxes[tbIdx].copy(isVisible = !extraTextBoxes[tbIdx].isVisible)
                    }
                    val stkIdx = placedStickers.indexOfFirst { it.id == id }
                    if (stkIdx != -1) {
                        placedStickers[stkIdx] = placedStickers[stkIdx].copy(isVisible = !placedStickers[stkIdx].isVisible)
                    }
                }
            }
        },
        onToggleLock = { id ->
            when (id) {
                "top_text" -> topTextLocked = !topTextLocked
                "bottom_text" -> bottomTextLocked = !bottomTextLocked
                else -> {
                    val tbIdx = extraTextBoxes.indexOfFirst { it.id == id }
                    if (tbIdx != -1) {
                        extraTextBoxes[tbIdx] = extraTextBoxes[tbIdx].copy(isLocked = !extraTextBoxes[tbIdx].isLocked)
                    }
                    val stkIdx = placedStickers.indexOfFirst { it.id == id }
                    if (stkIdx != -1) {
                        placedStickers[stkIdx] = placedStickers[stkIdx].copy(isLocked = !placedStickers[stkIdx].isLocked)
                    }
                }
            }
        },
        onDeleteLayer = { id ->
            when (id) {
                "top_text" -> topText = ""
                "bottom_text" -> bottomText = ""
                else -> {
                    extraTextBoxes.removeAll { it.id == id }
                    placedStickers.removeAll { it.id == id }
                }
            }
            layerOrder.remove(id)
            if (selectedTextBoxId == id) selectedTextBoxId = null
            if (selectedStickerId == id) selectedStickerId = null
        },
        onAddText = {
            val newBox = MemeTextBox(
                text = "New Caption",
                xPercent = 0.5f,
                yPercent = 0.5f,
                fontSizeSp = 28f,
                textColor = textColor,
                strokeColor = strokeColor,
                isAllCaps = isAllCaps
            )
            extraTextBoxes.add(newBox)
            layerOrder.add(newBox.id)
            selectedTextBoxId = newBox.id
            selectedStickerId = null
            bottomMode = "text"
            showLayersSidebar = false
        },
        onAddSticker = {
            showLayersSidebar = false
            showStickerPickerSheet = true
        },
        onShowAll = {
            topTextVisible = true
            bottomTextVisible = true
            for (i in extraTextBoxes.indices) {
                extraTextBoxes[i] = extraTextBoxes[i].copy(isVisible = true)
            }
            for (i in placedStickers.indices) {
                placedStickers[i] = placedStickers[i].copy(isVisible = true)
            }
        },
        onHideAll = {
            topTextVisible = false
            bottomTextVisible = false
            for (i in extraTextBoxes.indices) {
                extraTextBoxes[i] = extraTextBoxes[i].copy(isVisible = false)
            }
            for (i in placedStickers.indices) {
                placedStickers[i] = placedStickers[i].copy(isVisible = false)
            }
        },
        onUnlockAll = {
            topTextLocked = false
            bottomTextLocked = false
            for (i in extraTextBoxes.indices) {
                extraTextBoxes[i] = extraTextBoxes[i].copy(isLocked = false)
            }
            for (i in placedStickers.indices) {
                placedStickers[i] = placedStickers[i].copy(isLocked = false)
            }
        },
        onLockAll = {
            topTextLocked = true
            bottomTextLocked = true
            for (i in extraTextBoxes.indices) {
                extraTextBoxes[i] = extraTextBoxes[i].copy(isLocked = true)
            }
            for (i in placedStickers.indices) {
                placedStickers[i] = placedStickers[i].copy(isLocked = true)
            }
        }
    )
}
