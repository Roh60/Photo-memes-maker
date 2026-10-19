package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.ColorMatrix as AndroidColorMatrix
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix as ComposeColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AVAILABLE_FILTER_PRESETS
import com.example.data.model.FilterPreset
import com.example.util.ImageProcessingUtils

/**
 * Filter Categories for high-level organization in carousel and bottom sheet.
 */
enum class FilterCategory(val label: String, val presetIds: Set<String>) {
    ALL("All", emptySet()),
    CINEMATIC("Cinematic & Film", setOf("cinematic", "vintage", "sepia")),
    AESTHETIC("Aesthetic & Glow", setOf("glow", "aesthetic", "warm", "cool")),
    MONOCHROME("B&W & Noir", setOf("bw", "sepia")),
    VIBRANT("Vivid & Neon", setOf("vivid", "neon", "hdr", "festive"))
}

/**
 * Helper extension to convert android.graphics.ColorMatrix to androidx.compose.ui.graphics.ColorMatrix
 */
fun AndroidColorMatrix.toComposeColorMatrix(): ComposeColorMatrix {
    return ComposeColorMatrix(this.array)
}

/**
 * High-performance Horizontal Carousel UI for selectable ColorMatrix image filters.
 *
 * Displays visual thumbnail cards with real-time ColorMatrix effect previews (rendered either
 * directly on a downscaled user image or on a stylized artistic scenic canvas), active preset badge,
 * mood subtitle, and optional live intensity slider.
 */
@Composable
fun ImageFilterCarousel(
    selectedPreset: String,
    onSelectPreset: (String) -> Unit,
    modifier: Modifier = Modifier,
    filterIntensity: Float = 1.0f,
    onIntensityChange: ((Float) -> Unit)? = null,
    previewBitmap: Bitmap? = null,
    presets: List<FilterPreset> = AVAILABLE_FILTER_PRESETS,
    showCategoryChips: Boolean = false,
    showIntensitySlider: Boolean = true,
    cardWidth: androidx.compose.ui.unit.Dp = 78.dp,
    cardHeight: androidx.compose.ui.unit.Dp = 112.dp
) {
    var selectedCategory by remember { mutableStateOf(FilterCategory.ALL) }

    val filteredPresets = remember(selectedCategory, presets) {
        if (selectedCategory == FilterCategory.ALL) {
            presets
        } else {
            presets.filter { it.id == "none" || selectedCategory.presetIds.contains(it.id) }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Optional Category Chips row
        if (showCategoryChips) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterCategory.values().forEach { category ->
                    val isCatSelected = selectedCategory == category
                    FilterChip(
                        selected = isCatSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("filter_category_chip_${category.name.lowercase()}")
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Horizontal Carousel of Filter Cards
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("image_filter_carousel"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(filteredPresets, key = { it.id }) { preset ->
                val isSelected = selectedPreset.equals(preset.id, ignoreCase = true)
                ImageFilterThumbnailCard(
                    preset = preset,
                    isSelected = isSelected,
                    intensity = if (isSelected) filterIntensity else 1.0f,
                    previewBitmap = previewBitmap,
                    onClick = { onSelectPreset(preset.id) },
                    cardWidth = cardWidth,
                    cardHeight = cardHeight
                )
            }
        }

        // Filter Intensity Slider (Animated when active preset != "none")
        if (showIntensitySlider && !selectedPreset.equals("none", ignoreCase = true) && onIntensityChange != null) {
            Spacer(modifier = Modifier.height(6.dp))
            FilterIntensityControl(
                intensity = filterIntensity,
                onIntensityChange = onIntensityChange,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * Individual filter preset thumbnail card with live ColorMatrix rendering.
 */
@Composable
fun ImageFilterThumbnailCard(
    preset: FilterPreset,
    isSelected: Boolean,
    intensity: Float,
    previewBitmap: Bitmap?,
    onClick: () -> Unit,
    cardWidth: androidx.compose.ui.unit.Dp = 78.dp,
    cardHeight: androidx.compose.ui.unit.Dp = 112.dp,
    modifier: Modifier = Modifier
) {
    // Calculate ColorMatrix for this preset thumbnail
    val composeMatrix = remember(preset.id, intensity, isSelected) {
        if (preset.id.equals("none", ignoreCase = true)) {
            ComposeColorMatrix() // Identity
        } else {
            val baseMatrix = ImageProcessingUtils.getPresetColorMatrix(preset.id)
            val effectiveIntensity = if (isSelected) intensity else 1.0f
            val interpolated = ImageProcessingUtils.interpolateColorMatrix(baseMatrix, effectiveIntensity)
            interpolated.toComposeColorMatrix()
        }
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "border_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1.0f,
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .width(cardWidth)
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("filter_preset_${preset.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
                brush = androidx.compose.ui.graphics.SolidColor(animatedBorderColor)
            )
        } else null
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Visual Preview Box with live ColorMatrix effect
            Box(
                modifier = Modifier
                    .size(cardWidth - 14.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                if (previewBitmap != null && !previewBitmap.isRecycled) {
                    Image(
                        bitmap = previewBitmap.asImageBitmap(),
                        contentDescription = "${preset.name} filter preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.colorMatrix(composeMatrix)
                    )
                } else {
                    // Stylized Scenic Canvas with the ColorMatrix applied live
                    ScenicThumbnailCanvas(
                        colorFilter = ColorFilter.colorMatrix(composeMatrix),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Selected Checkmark Badge
                if (isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(3.dp)
                            .size(16.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Preset Name
            Text(
                text = preset.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Mood Subtitle
            val moodTag = remember(preset.id) {
                when (preset.id) {
                    "none" -> "Original"
                    "cinematic" -> "Teal & Orange"
                    "glow" -> "Luminous"
                    "vintage" -> "70s Film"
                    "aesthetic" -> "Pastel"
                    "warm" -> "Golden"
                    "cool" -> "Cyan"
                    "bw" -> "Noir B&W"
                    "festive" -> "Party"
                    "vivid" -> "Punchy"
                    "hdr" -> "Crisp"
                    "neon" -> "Club"
                    "sepia" -> "Antique"
                    else -> "Creative"
                }
            }
            Text(
                text = moodTag,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Artistic landscape canvas used for realistic ColorMatrix filter previews
 * when no user photo is available.
 */
@Composable
fun ScenicThumbnailCanvas(
    colorFilter: ColorFilter,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Draw Sky gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF38BDF8), // Light Cyan Blue
                    Color(0xFFFB923C), // Warm Sunset Orange
                    Color(0xFFFDE047)  // Golden Glow
                ),
                startY = 0f,
                endY = h * 0.75f
            ),
            colorFilter = colorFilter
        )

        // Sun
        drawCircle(
            color = Color(0xFFFFFBEB),
            radius = w * 0.16f,
            center = Offset(w * 0.7f, h * 0.35f),
            colorFilter = colorFilter
        )

        // Far Mountains
        val farMountain = Path().apply {
            moveTo(0f, h * 0.55f)
            lineTo(w * 0.35f, h * 0.32f)
            lineTo(w * 0.7f, h * 0.50f)
            lineTo(w, h * 0.38f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = farMountain,
            color = Color(0xFF7C3AED), // Indigo violet mountain
            colorFilter = colorFilter
        )

        // Foreground hills
        val foreMountain = Path().apply {
            moveTo(0f, h * 0.7f)
            lineTo(w * 0.45f, h * 0.52f)
            lineTo(w * 0.85f, h * 0.65f)
            lineTo(w, h * 0.6f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = foreMountain,
            color = Color(0xFF0F766E), // Deep teal pine hill
            colorFilter = colorFilter
        )
    }
}

/**
 * Filter intensity slider with percentage label, quick snap chips (25%, 50%, 75%, 100%),
 * and reset capability.
 */
@Composable
fun FilterIntensityControl(
    intensity: Float,
    onIntensityChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Filter Intensity",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    text = "${(intensity * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Slider(
            value = intensity,
            onValueChange = onIntensityChange,
            valueRange = 0.0f..1.0f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("slider_filter_intensity")
        )

        // Quick snap percentage pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(0.25f to "25%", 0.50f to "50%", 0.75f to "75%", 1.0f to "100%").forEach { (v, label) ->
                AssistChip(
                    onClick = { onIntensityChange(v) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (kotlin.math.abs(intensity - v) < 0.05f) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            Color.Transparent
                        }
                    ),
                    border = AssistChipDefaults.assistChipBorder(enabled = true),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("snap_intensity_${label}")
                )
            }
        }
    }
}

/**
 * Full-featured Modal Bottom Sheet UI displaying selectable image filters using ColorMatrix effects.
 *
 * Includes:
 * - Clean M3 bottom sheet container with drag handle
 * - Interactive live preview box with "Hold to Compare" original button
 * - Filter category filter chips
 * - Horizontal carousel of filter presets with live thumbnail previews
 * - Real-time filter intensity slider
 * - Expandable fine-tuning sliders (Brightness, Contrast, Saturation, Warmth)
 * - Reset and Done actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageFilterBottomSheet(
    onDismissRequest: () -> Unit,
    activePreset: String,
    onPresetChange: (String) -> Unit,
    filterIntensity: Float,
    onFilterIntensityChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    previewBitmap: Bitmap? = null,
    brightness: Float = 0f,
    onBrightnessChange: (Float) -> Unit = {},
    contrast: Float = 1.0f,
    onContrastChange: (Float) -> Unit = {},
    saturation: Float = 1.0f,
    onSaturationChange: (Float) -> Unit = {},
    warmth: Float = 0f,
    onWarmthChange: (Float) -> Unit = {},
    onResetAll: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isHoldingCompare by remember { mutableStateOf(false) }
    var showFineTuning by remember { mutableStateOf(false) }

    // Live combined ColorMatrix for the preview box
    val liveComposeMatrix = remember(
        activePreset,
        filterIntensity,
        brightness,
        contrast,
        saturation,
        warmth,
        isHoldingCompare
    ) {
        if (isHoldingCompare) {
            ComposeColorMatrix() // Identity when holding compare
        } else {
            val cm = ImageProcessingUtils.buildAdjustedColorMatrix(
                preset = activePreset,
                intensity = filterIntensity,
                brightness = brightness,
                contrast = contrast,
                saturation = saturation,
                warmth = warmth
            )
            cm.toComposeColorMatrix()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 36.dp, height = 4.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            ) {}
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("image_filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 28.dp)
        ) {
            // Header: Title, Active Badge, Reset and Done buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ColorMatrix Filters",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (activePreset == "none") "Original" else "${AVAILABLE_FILTER_PRESETS.find { it.id == activePreset }?.name ?: activePreset} (${(filterIntensity * 100).toInt()}%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onResetAll,
                        modifier = Modifier.testTag("reset_filters_sheet_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset")
                    }

                    OutlinedButton(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .testTag("done_filters_sheet_btn")
                    ) {
                        Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Done")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Large Live Preview Viewport with "Hold to Compare" Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .testTag("sheet_preview_viewport"),
                contentAlignment = Alignment.Center
            ) {
                if (previewBitmap != null && !previewBitmap.isRecycled) {
                    Image(
                        bitmap = previewBitmap.asImageBitmap(),
                        contentDescription = "Live filtered preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.colorMatrix(liveComposeMatrix)
                    )
                } else {
                    ScenicThumbnailCanvas(
                        colorFilter = ColorFilter.colorMatrix(liveComposeMatrix),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // "Hold to Compare" Floating Pill Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isHoldingCompare) MaterialTheme.colorScheme.errorContainer else Color.Black.copy(alpha = 0.65f),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isHoldingCompare = true
                                    tryAwaitRelease()
                                    isHoldingCompare = false
                                }
                            )
                        }
                        .testTag("filter_compare_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = null,
                            tint = if (isHoldingCompare) MaterialTheme.colorScheme.onErrorContainer else Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHoldingCompare) "Showing Original" else "Hold to Compare",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                            color = if (isHoldingCompare) MaterialTheme.colorScheme.onErrorContainer else Color.White
                        )
                    }
                }

                // Active Preset Watermark Pill in Top-Left
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = if (isHoldingCompare) "ORIGINAL" else "${AVAILABLE_FILTER_PRESETS.find { it.id == activePreset }?.name?.uppercase() ?: "FILTER"}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = if (isHoldingCompare) Color(0xFFFBBF24) else Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // The Horizontal Carousel of Filter Presets
            ImageFilterCarousel(
                selectedPreset = activePreset,
                onSelectPreset = onPresetChange,
                filterIntensity = filterIntensity,
                onIntensityChange = onFilterIntensityChange,
                previewBitmap = previewBitmap,
                showCategoryChips = true,
                showIntensitySlider = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Expandable Advanced Fine-Tuning Drawer (Brightness, Contrast, Saturation, Warmth)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { showFineTuning = !showFineTuning },
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fine-Tune Color Matrix Adjustments",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = if (showFineTuning) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = showFineTuning) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Brightness slider
                    SheetSliderRow(
                        label = "Brightness (${brightness.toInt()})",
                        value = brightness,
                        range = -80f..80f,
                        onValueChange = onBrightnessChange,
                        testTag = "slider_brightness_tuning"
                    )

                    // Contrast slider
                    SheetSliderRow(
                        label = "Contrast (${String.format("%.2f", contrast)}x)",
                        value = contrast,
                        range = 0.5f..2.0f,
                        onValueChange = onContrastChange,
                        testTag = "slider_contrast_tuning"
                    )

                    // Saturation slider
                    SheetSliderRow(
                        label = "Saturation (${String.format("%.2f", saturation)}x)",
                        value = saturation,
                        range = 0.0f..2.5f,
                        onValueChange = onSaturationChange,
                        testTag = "slider_saturation_tuning"
                    )

                    // Warmth slider
                    SheetSliderRow(
                        label = "Warmth (${warmth.toInt()})",
                        value = warmth,
                        range = -50f..50f,
                        onValueChange = onWarmthChange,
                        testTag = "slider_warmth_tuning"
                    )
                }
            }
        }
    }
}

/**
 * Clean slider row for bottom sheet fine tuning.
 */
@Composable
private fun SheetSliderRow(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}
