package com.example.ui.photomaker

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CollageSlotState
import com.example.data.model.CollageTemplate
import com.example.data.model.CollageTemplateRepository
import com.example.data.repository.CreationsRepository
import com.example.ui.components.CollageGridCanvas
import com.example.ui.components.CollageSlotInspector
import com.example.ui.components.CollageTemplateThumbnail
import com.example.ui.components.PaletteColorPicker
import com.example.ui.components.StudioTopBar
import com.example.ui.components.ValueSliderRow
import com.example.util.BitmapUtils
import com.example.util.SampleImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Full Collage Studio Screen providing:
 * - Pre-defined collage template selection (2 to 6 photo layouts)
 * - Placeholders with drag/drop/pick capabilities
 * - Independent adjustments for each image in its frame (pan, zoom, rotation, flip, filters)
 * - Frame styling (aspect ratios, borders, margins, gutters, background colors)
 * - High-resolution export and system sharing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollageStudioScreen(
    initialTemplateId: String? = null,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { CreationsRepository(context) }

    // Selected collage template
    var currentTemplate by remember {
        mutableStateOf(
            if (initialTemplateId != null) {
                CollageTemplateRepository.getTemplateById(initialTemplateId)
            } else {
                CollageTemplateRepository.TEMPLATES.first() // 2 side by side
            }
        )
    }

    // State per slot (slotId -> CollageSlotState)
    val slotStates = remember { mutableStateMapOf<Int, CollageSlotState>() }

    // Initialize initial default sample photos for slots 1 and 2
    LaunchedEffect(Unit) {
        if (slotStates.isEmpty()) {
            slotStates[1] = CollageSlotState(
                slotId = 1,
                bitmap = SampleImages.createScenicSample(600, 600, "Left Photo", 0xFF6366F1.toInt(), 0xFF8B5CF6.toInt())
            )
            slotStates[2] = CollageSlotState(
                slotId = 2,
                bitmap = SampleImages.createScenicSample(600, 600, "Right Photo", 0xFFEC4899.toInt(), 0xFFF43F5E.toInt())
            )
        }
    }

    // Currently focused/active slot ID for adjustment
    var activeSlotId by remember { mutableStateOf<Int?>(1) }

    // Frame layout & styling settings
    var selectedAspectRatio by remember { mutableFloatStateOf(1.0f) } // 1:1, 4:5, 9:16, 16:9, 3:4
    var outerPaddingDp by remember { mutableFloatStateOf(12f) }
    var gutterSpacingDp by remember { mutableFloatStateOf(6f) }
    var cornerRadiusDp by remember { mutableFloatStateOf(8f) }
    var frameBgColor by remember { mutableStateOf(0xFFFFFFFF) }
    var borderWidthDp by remember { mutableFloatStateOf(0f) }
    var borderColor by remember { mutableStateOf(0xFF000000) }

    // Bottom studio tab: "templates", "adjust", "frame"
    var bottomTab by remember { mutableStateOf("templates") }
    // Category filter for templates ("All", "2 Photos", "3 Photos", "4 Photos", "5 Photos", "6 Photos")
    var templateCategoryFilter by remember { mutableStateOf("All") }

    var isExporting by remember { mutableStateOf(false) }

    // Target slot awaiting image from photo picker / camera
    var pendingSlotTarget by remember { mutableIntStateOf(1) }

    // Single photo picker
    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val bmp = BitmapUtils.decodeUriToBitmap(context, uri)
                if (bmp != null) {
                    val current = slotStates[pendingSlotTarget] ?: CollageSlotState(slotId = pendingSlotTarget)
                    slotStates[pendingSlotTarget] = current.copy(bitmap = bmp)
                    activeSlotId = pendingSlotTarget
                    bottomTab = "adjust"
                }
            }
        }
    }

    // Camera snapshot launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            val current = slotStates[pendingSlotTarget] ?: CollageSlotState(slotId = pendingSlotTarget)
            slotStates[pendingSlotTarget] = current.copy(bitmap = bmp)
            activeSlotId = pendingSlotTarget
            bottomTab = "adjust"
        }
    }

    // Function to populate all slots with vibrant sample images
    fun fillAllSlotsWithSamples() {
        val colors = listOf(
            0xFF6366F1.toInt() to 0xFF8B5CF6.toInt(),
            0xFFEC4899.toInt() to 0xFFF43F5E.toInt(),
            0xFF10B981.toInt() to 0xFF059669.toInt(),
            0xFFF59E0B.toInt() to 0xFFD97706.toInt(),
            0xFF3B82F6.toInt() to 0xFF1D4ED8.toInt(),
            0xFF8B5CF6.toInt() to 0xFF6D28D9.toInt()
        )
        currentTemplate.slots.forEachIndexed { index, slot ->
            val (c1, c2) = colors[index % colors.size]
            val existing = slotStates[slot.id]
            if (existing?.bitmap == null) {
                slotStates[slot.id] = (existing ?: CollageSlotState(slotId = slot.id)).copy(
                    bitmap = SampleImages.createScenicSample(600, 600, "Photo ${slot.id}", c1, c2)
                )
            }
        }
        Toast.makeText(context, "Filled empty slots with sample photos", Toast.LENGTH_SHORT).show()
    }

    // Export renderer: Renders the entire collage to a pristine high-resolution bitmap
    suspend fun exportCollageBitmap(): Bitmap = withContext(Dispatchers.Default) {
        val baseWidth = 1080
        val baseHeight = (baseWidth / selectedAspectRatio).toInt()
        val exportBitmap = Bitmap.createBitmap(baseWidth, baseHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(exportBitmap)

        // Draw background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = frameBgColor.toInt()
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, baseWidth.toFloat(), baseHeight.toFloat(), bgPaint)

        // Scaling factors for resolution
        val scaleMultiplier = baseWidth / 360f
        val outerPadPx = outerPaddingDp * scaleMultiplier
        val gutterPx = gutterSpacingDp * scaleMultiplier
        val cornerRadPx = cornerRadiusDp * scaleMultiplier

        val availW = (baseWidth - outerPadPx * 2f).coerceAtLeast(10f)
        val availH = (baseHeight - outerPadPx * 2f).coerceAtLeast(10f)

        currentTemplate.slots.forEach { slot ->
            val state = slotStates[slot.id]
            val bmp = state?.bitmap

            val rawLeft = outerPadPx + slot.left * availW
            val rawTop = outerPadPx + slot.top * availH
            val rawRight = outerPadPx + slot.right * availW
            val rawBottom = outerPadPx + slot.bottom * availH

            val left = rawLeft + if (slot.left > 0f) gutterPx / 2f else 0f
            val top = rawTop + if (slot.top > 0f) gutterPx / 2f else 0f
            val right = rawRight - if (slot.right < 1f) gutterPx / 2f else 0f
            val bottom = rawBottom - if (slot.bottom < 1f) gutterPx / 2f else 0f

            val slotRect = RectF(left, top, right, bottom)
            val slotW = slotRect.width()
            val slotH = slotRect.height()

            if (bmp != null) {
                val clipPath = Path().apply {
                    addRoundRect(slotRect, cornerRadPx, cornerRadPx, Path.Direction.CW)
                }

                canvas.save()
                canvas.clipPath(clipPath)

                // Fill slot background
                val slotBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.DKGRAY
                }
                canvas.drawRect(slotRect, slotBgPaint)

                // Base matrix: Center-Crop the bitmap to fill the slot rectangle
                val matrix = Matrix()
                val bmpW = bmp.width.toFloat()
                val bmpH = bmp.height.toFloat()

                val baseScale = maxOf(slotW / bmpW, slotH / bmpH)
                val scaledW = bmpW * baseScale
                val scaledH = bmpH * baseScale

                val baseDx = slotRect.left + (slotW - scaledW) / 2f
                val baseDy = slotRect.top + (slotH - scaledH) / 2f

                matrix.postScale(baseScale, baseScale)
                matrix.postTranslate(baseDx, baseDy)

                // Apply independent user transformations:
                val centerX = slotRect.centerX()
                val centerY = slotRect.centerY()

                // User pan
                matrix.postTranslate(state.panX * slotW, state.panY * slotH)

                // User zoom
                matrix.postScale(state.scale, state.scale, centerX, centerY)

                // User flips
                val flipX = if (state.isFlippedHorizontal) -1f else 1f
                val flipY = if (state.isFlippedVertical) -1f else 1f
                if (flipX != 1f || flipY != 1f) {
                    matrix.postScale(flipX, flipY, centerX, centerY)
                }

                // User rotation
                if (state.rotation != 0f) {
                    matrix.postRotate(state.rotation, centerX, centerY)
                }

                // Paint with independent filter if any
                val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                if (!state.filter.equals("none", ignoreCase = true)) {
                    val cm = BitmapUtils.createFilterColorMatrix(0f, 1f, 1f, 0f, state.filter)
                    paint.colorFilter = android.graphics.ColorMatrixColorFilter(cm)
                }

                canvas.drawBitmap(bmp, matrix, paint)
                canvas.restore()
            } else {
                // Empty slot background
                val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.parseColor("#1E293B")
                }
                canvas.drawRoundRect(slotRect, cornerRadPx, cornerRadPx, emptyPaint)
            }
        }

        // Draw outer border if enabled
        if (borderWidthDp > 0f) {
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = borderColor.toInt()
                style = Paint.Style.STROKE
                strokeWidth = borderWidthDp * scaleMultiplier
            }
            canvas.drawRoundRect(
                RectF(0f, 0f, baseWidth.toFloat(), baseHeight.toFloat()),
                cornerRadPx,
                cornerRadPx,
                borderPaint
            )
        }

        exportBitmap
    }

    Scaffold(
        topBar = {
            StudioTopBar(
                title = "Collage: ${currentTemplate.name}",
                onBack = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = { fillAllSlotsWithSamples() },
                        modifier = Modifier.testTag("collage_fill_samples_btn")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Fill Samples")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                isExporting = true
                                try {
                                    val finalBmp = exportCollageBitmap()
                                    val uri = BitmapUtils.saveBitmapToGallery(
                                        context,
                                        finalBmp,
                                        "Collage_${System.currentTimeMillis()}"
                                    )
                                    repository.saveCreation(
                                        bitmap = finalBmp,
                                        title = "${currentTemplate.name} Collage",
                                        type = "collage"
                                    )
                                    if (uri != null) {
                                        Toast.makeText(context, "Saved to Pictures!", Toast.LENGTH_SHORT).show()
                                    }
                                    BitmapUtils.shareBitmap(context, finalBmp)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isExporting = false
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("collage_export_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !isExporting
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main Canvas viewport (occupies top weighted space)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                CollageGridCanvas(
                    template = currentTemplate,
                    slotStates = slotStates,
                    activeSlotId = activeSlotId,
                    aspectRatio = selectedAspectRatio,
                    outerPaddingDp = outerPaddingDp,
                    gutterSpacingDp = gutterSpacingDp,
                    cornerRadiusDp = cornerRadiusDp,
                    frameBgColor = frameBgColor,
                    borderWidthDp = borderWidthDp,
                    borderColor = borderColor,
                    onSelectSlot = { id ->
                        activeSlotId = id
                        bottomTab = "adjust"
                    },
                    onUpdateSlotState = { newState ->
                        slotStates[newState.slotId] = newState
                    },
                    onEmptySlotClick = { id ->
                        pendingSlotTarget = id
                        singlePhotoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }

            // Bottom Control Center
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    // Navigation Tab Row
                    TabRow(
                        selectedTabIndex = when (bottomTab) {
                            "templates" -> 0
                            "adjust" -> 1
                            else -> 2
                        },
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Tab(
                            selected = bottomTab == "templates",
                            onClick = { bottomTab = "templates" },
                            text = { Text("Layouts (${CollageTemplateRepository.TEMPLATES.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.testTag("tab_collage_templates")
                        )
                        Tab(
                            selected = bottomTab == "adjust",
                            onClick = { bottomTab = "adjust" },
                            text = { Text("Adjust ${activeSlotId?.let { "#$it" } ?: ""}", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.testTag("tab_collage_adjust")
                        )
                        Tab(
                            selected = bottomTab == "frame",
                            onClick = { bottomTab = "frame" },
                            text = { Text("Frame & Style", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.testTag("tab_collage_frame")
                        )
                    }

                    // Tab Content Body
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        when (bottomTab) {
                            "templates" -> {
                                TemplatesSelectorTab(
                                    currentTemplate = currentTemplate,
                                    categoryFilter = templateCategoryFilter,
                                    onSelectCategory = { templateCategoryFilter = it },
                                    onSelectTemplate = { newTpl ->
                                        currentTemplate = newTpl
                                        // Ensure activeSlotId is within valid range
                                        if (activeSlotId != null && activeSlotId!! > newTpl.photoCount) {
                                            activeSlotId = 1
                                        }
                                    }
                                )
                            }
                            "adjust" -> {
                                val currentSlotId = activeSlotId ?: 1
                                val slotState = slotStates[currentSlotId] ?: CollageSlotState(slotId = currentSlotId)
                                if (slotState.bitmap != null) {
                                    CollageSlotInspector(
                                        slotState = slotState,
                                        totalSlots = currentTemplate.photoCount,
                                        onUpdateState = { updated ->
                                            slotStates[updated.slotId] = updated
                                        },
                                        onReplacePhoto = {
                                            pendingSlotTarget = currentSlotId
                                            singlePhotoPicker.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        onSwapWithSlot = { otherSlotId ->
                                            val stateA = slotStates[currentSlotId]
                                            val stateB = slotStates[otherSlotId]

                                            slotStates[currentSlotId] = (stateB ?: CollageSlotState(slotId = currentSlotId)).copy(slotId = currentSlotId)
                                            slotStates[otherSlotId] = (stateA ?: CollageSlotState(slotId = otherSlotId)).copy(slotId = otherSlotId)
                                            activeSlotId = otherSlotId
                                            Toast.makeText(context, "Swapped Slot $currentSlotId with Slot $otherSlotId", Toast.LENGTH_SHORT).show()
                                        },
                                        onClearPhoto = {
                                            slotStates[currentSlotId] = CollageSlotState(slotId = currentSlotId, bitmap = null)
                                            Toast.makeText(context, "Slot $currentSlotId cleared", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    // Empty slot actions
                                    EmptySlotActionsCard(
                                        slotId = currentSlotId,
                                        onPickGallery = {
                                            pendingSlotTarget = currentSlotId
                                            singlePhotoPicker.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        onTakePhoto = {
                                            pendingSlotTarget = currentSlotId
                                            cameraLauncher.launch(null)
                                        },
                                        onUseSample = {
                                            slotStates[currentSlotId] = CollageSlotState(
                                                slotId = currentSlotId,
                                                bitmap = SampleImages.createScenicSample(600, 600, "Photo $currentSlotId")
                                            )
                                        }
                                    )
                                }
                            }
                            "frame" -> {
                                FrameStylingTab(
                                    selectedAspectRatio = selectedAspectRatio,
                                    onSelectAspectRatio = { selectedAspectRatio = it },
                                    outerPaddingDp = outerPaddingDp,
                                    onOuterPaddingChange = { outerPaddingDp = it },
                                    gutterSpacingDp = gutterSpacingDp,
                                    onGutterSpacingChange = { gutterSpacingDp = it },
                                    cornerRadiusDp = cornerRadiusDp,
                                    onCornerRadiusChange = { cornerRadiusDp = it },
                                    frameBgColor = frameBgColor,
                                    onFrameBgColorChange = { frameBgColor = it },
                                    borderWidthDp = borderWidthDp,
                                    onBorderWidthChange = { borderWidthDp = it },
                                    borderColor = borderColor,
                                    onBorderColorChange = { borderColor = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab for browsing and selecting collage grid templates with category chips.
 */
@Composable
private fun TemplatesSelectorTab(
    currentTemplate: CollageTemplate,
    categoryFilter: String,
    onSelectCategory: (String) -> Unit,
    onSelectTemplate: (CollageTemplate) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Category chips: All, 2 Photos, 3 Photos, 4 Photos, 5 Photos, 6 Photos
        val categories = listOf("All", "2 Photos", "3 Photos", "4 Photos", "5 Photos", "6 Photos")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = categoryFilter == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectCategory(cat) },
                    label = { Text(cat, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("filter_cat_$cat")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal list of visual collage layout cards
        val filteredTemplates = remember(categoryFilter) {
            if (categoryFilter == "All") {
                CollageTemplateRepository.TEMPLATES
            } else {
                CollageTemplateRepository.TEMPLATES.filter { it.category == categoryFilter }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filteredTemplates.forEach { template ->
                CollageTemplateThumbnail(
                    template = template,
                    isSelected = template.id == currentTemplate.id,
                    onClick = { onSelectTemplate(template) }
                )
            }
        }
    }
}

/**
 * Tab for styling the collage frame (Aspect Ratio, Padding, Gutter, Corners, Colors, Borders).
 */
@Composable
private fun FrameStylingTab(
    selectedAspectRatio: Float,
    onSelectAspectRatio: (Float) -> Unit,
    outerPaddingDp: Float,
    onOuterPaddingChange: (Float) -> Unit,
    gutterSpacingDp: Float,
    onGutterSpacingChange: (Float) -> Unit,
    cornerRadiusDp: Float,
    onCornerRadiusChange: (Float) -> Unit,
    frameBgColor: Long,
    onFrameBgColorChange: (Long) -> Unit,
    borderWidthDp: Float,
    onBorderWidthChange: (Float) -> Unit,
    borderColor: Long,
    onBorderColorChange: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Aspect Ratio Selector
        Text("Aspect Ratio:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        val ratios = listOf(
            1.0f to "1:1 Square",
            0.8f to "4:5 Insta",
            0.5625f to "9:16 Reel",
            1.777f to "16:9 Wide",
            0.75f to "3:4 Classic"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ratios.forEach { (ratio, label) ->
                val isSelected = Math.abs(selectedAspectRatio - ratio) < 0.05f
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectAspectRatio(ratio) },
                    label = { Text(label, fontSize = 11.sp) },
                    modifier = Modifier.testTag("ratio_chip_$label")
                )
            }
        }

        // Sliders
        ValueSliderRow(
            label = "Outer Margin",
            value = outerPaddingDp,
            range = 0f..28f,
            onValueChange = onOuterPaddingChange,
            format = "%.0f dp",
            testTag = "slider_outer_margin"
        )

        ValueSliderRow(
            label = "Spacing / Gutter",
            value = gutterSpacingDp,
            range = 0f..24f,
            onValueChange = onGutterSpacingChange,
            format = "%.0f dp",
            testTag = "slider_gutter"
        )

        ValueSliderRow(
            label = "Corner Radius",
            value = cornerRadiusDp,
            range = 0f..28f,
            onValueChange = onCornerRadiusChange,
            format = "%.0f dp",
            testTag = "slider_corner_radius"
        )

        ValueSliderRow(
            label = "Outer Border Width",
            value = borderWidthDp,
            range = 0f..8f,
            onValueChange = onBorderWidthChange,
            format = "%.0f dp",
            testTag = "slider_border_width"
        )

        // Background Color
        Text("Background Color:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        PaletteColorPicker(
            selectedColor = frameBgColor,
            onColorSelected = onFrameBgColorChange,
            colors = listOf(
                0xFFFFFFFF, 0xFF000000, 0xFF0F172A, 0xFFF1F5F9,
                0xFFFEE2E2, 0xFFFEF3C7, 0xFFE0E7FF, 0xFFFCE7F3,
                0xFF6366F1, 0xFFEC4899, 0xFF10B981, 0xFFF59E0B
            )
        )

        if (borderWidthDp > 0f) {
            Text("Border Color:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            PaletteColorPicker(
                selectedColor = borderColor,
                onColorSelected = onBorderColorChange,
                colors = listOf(0xFF000000, 0xFFFFFFFF, 0xFF6366F1, 0xFFEC4899, 0xFFF59E0B)
            )
        }
    }
}

/**
 * Card prompt when an empty slot is active, letting the user add a photo from Gallery, Camera, or Presets.
 */
@Composable
private fun EmptySlotActionsCard(
    slotId: Int,
    onPickGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onUseSample: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("empty_slot_actions_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Slot #$slotId is Empty",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Choose an image to drop into this collage placeholder",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPickGallery,
                    modifier = Modifier.weight(1f).testTag("empty_slot_gallery_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onTakePhoto,
                    modifier = Modifier.weight(1f).testTag("empty_slot_camera_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Camera", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onUseSample,
                    modifier = Modifier.weight(1f).testTag("empty_slot_sample_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sample", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
