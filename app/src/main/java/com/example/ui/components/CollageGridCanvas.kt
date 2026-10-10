package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CollageSlot
import com.example.data.model.CollageSlotState
import com.example.data.model.CollageTemplate
import com.example.util.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Interactive Collage Grid Canvas.
 * Renders the chosen collage template with independent touch gestures (pan, zoom),
 * active slot highlighting, and inviting empty placeholders.
 */
@Composable
fun CollageGridCanvas(
    template: CollageTemplate,
    slotStates: Map<Int, CollageSlotState>,
    activeSlotId: Int?,
    onSelectSlot: (Int) -> Unit,
    onUpdateSlotState: (CollageSlotState) -> Unit,
    onEmptySlotClick: (Int) -> Unit,
    aspectRatio: Float = 1.0f,
    outerPaddingDp: Float = 12f,
    gutterSpacingDp: Float = 6f,
    cornerRadiusDp: Float = 8f,
    frameBgColor: Long = 0xFFFFFFFF,
    borderWidthDp: Float = 0f,
    borderColor: Long = 0xFF000000,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(cornerRadiusDp.coerceAtLeast(4f).dp))
            .background(Color(frameBgColor))
            .then(
                if (borderWidthDp > 0f) {
                    Modifier.border(
                        borderWidthDp.dp,
                        Color(borderColor),
                        RoundedCornerShape(cornerRadiusDp.dp)
                    )
                } else Modifier
            )
            .testTag("collage_grid_canvas")
    ) {
        val totalWidthPx = constraints.maxWidth.toFloat()
        val totalHeightPx = constraints.maxHeight.toFloat()

        val outerPadPx = with(density) { outerPaddingDp.dp.toPx() }
        val gutterPx = with(density) { gutterSpacingDp.dp.toPx() }
        val cornerRadPx = with(density) { cornerRadiusDp.dp.toPx() }

        val availWidthPx = (totalWidthPx - outerPadPx * 2f).coerceAtLeast(10f)
        val availHeightPx = (totalHeightPx - outerPadPx * 2f).coerceAtLeast(10f)

        // Render each slot according to template layout
        template.slots.forEach { slot ->
            val slotState = slotStates[slot.id] ?: CollageSlotState(slotId = slot.id)
            val isSelected = activeSlotId == slot.id

            // Calculate pixel bounds with outer padding and gutter
            val rawLeft = outerPadPx + slot.left * availWidthPx
            val rawTop = outerPadPx + slot.top * availHeightPx
            val rawRight = outerPadPx + slot.right * availWidthPx
            val rawBottom = outerPadPx + slot.bottom * availHeightPx

            // Apply gutter adjustments for internal borders
            val left = rawLeft + if (slot.left > 0f) gutterPx / 2f else 0f
            val top = rawTop + if (slot.top > 0f) gutterPx / 2f else 0f
            val right = rawRight - if (slot.right < 1f) gutterPx / 2f else 0f
            val bottom = rawBottom - if (slot.bottom < 1f) gutterPx / 2f else 0f

            val slotWidthPx = (right - left).coerceAtLeast(20f)
            val slotHeightPx = (bottom - top).coerceAtLeast(20f)

            val slotWidthDp = with(density) { slotWidthPx.toDp() }
            val slotHeightDp = with(density) { slotHeightPx.toDp() }
            val leftDp = with(density) { left.toDp() }
            val topDp = with(density) { top.toDp() }

            Box(
                modifier = Modifier
                    .offset(x = leftDp, y = topDp)
                    .size(width = slotWidthDp, height = slotHeightDp)
                    .clip(RoundedCornerShape(cornerRadiusDp.dp))
                    .clickable {
                        onSelectSlot(slot.id)
                        if (slotState.bitmap == null) {
                            onEmptySlotClick(slot.id)
                        }
                    }
                    .testTag("collage_slot_${slot.id}")
            ) {
                if (slotState.bitmap != null) {
                    // Filled slot with independent image adjustments
                    FilledSlotView(
                        slot = slot,
                        state = slotState,
                        slotWidthPx = slotWidthPx,
                        slotHeightPx = slotHeightPx,
                        isSelected = isSelected,
                        cornerRadiusDp = cornerRadiusDp,
                        onUpdateState = onUpdateSlotState,
                        onSelect = { onSelectSlot(slot.id) }
                    )
                } else {
                    // Empty placeholder slot
                    EmptySlotPlaceholder(
                        slot = slot,
                        isSelected = isSelected,
                        cornerRadiusDp = cornerRadiusDp,
                        onClick = {
                            onSelectSlot(slot.id)
                            onEmptySlotClick(slot.id)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Filled slot rendering the image with independent pan, zoom, rotation, flip, and filter.
 */
@Composable
private fun FilledSlotView(
    slot: CollageSlot,
    state: CollageSlotState,
    slotWidthPx: Float,
    slotHeightPx: Float,
    isSelected: Boolean,
    cornerRadiusDp: Float,
    onUpdateState: (CollageSlotState) -> Unit,
    onSelect: () -> Unit
) {
    val bmp = state.bitmap ?: return

    // Compose ColorMatrix for independent filter
    val colorFilter = remember(state.filter) {
        if (state.filter.equals("none", ignoreCase = true)) {
            null
        } else {
            val cm = BitmapUtils.createFilterColorMatrix(0f, 1f, 1f, 0f, state.filter)
            ColorFilter.colorMatrix(ColorMatrix(cm.array))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .pointerInput(slot.id) {
                detectTransformGestures { _, pan, zoom, _ ->
                    onSelect()
                    val newScale = (state.scale * zoom).coerceIn(1.0f, 4.0f)
                    val newPanX = state.panX + (pan.x / slotWidthPx)
                    val newPanY = state.panY + (pan.y / slotHeightPx)
                    onUpdateState(state.copy(scale = newScale, panX = newPanX, panY = newPanY))
                }
            }
            .then(
                if (isSelected) {
                    Modifier.border(
                        2.5.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(cornerRadiusDp.dp)
                    )
                } else {
                    Modifier.border(
                        0.5.dp,
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(cornerRadiusDp.dp)
                    )
                }
            )
    ) {
        // Image layer with independent transform matrix
        androidx.compose.foundation.Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = "Photo ${slot.id}",
            contentScale = ContentScale.Crop,
            colorFilter = colorFilter,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = state.scale * (if (state.isFlippedHorizontal) -1f else 1f)
                    scaleY = state.scale * (if (state.isFlippedVertical) -1f else 1f)
                    rotationZ = state.rotation
                    translationX = state.panX * slotWidthPx
                    translationY = state.panY * slotHeightPx
                }
        )

        // Slot Badge Indicator
        Surface(
            shape = RoundedCornerShape(bottomEnd = 8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(
                text = if (state.scale > 1.05f) "${slot.id} • ${String.format("%.1f", state.scale)}x" else "${slot.id}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        // Active selection corner badge
        if (isSelected) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(18.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Active",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

/**
 * Placeholder view for an empty collage frame slot.
 */
@Composable
private fun EmptySlotPlaceholder(
    slot: CollageSlot,
    isSelected: Boolean,
    cornerRadiusDp: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF475569),
                shape = RoundedCornerShape(cornerRadiusDp.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF334155),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add image to slot ${slot.id}",
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = slot.label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0)
            )
            Text(
                text = "Tap to add",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = Color(0xFF94A3B8)
            )
        }
    }
}
