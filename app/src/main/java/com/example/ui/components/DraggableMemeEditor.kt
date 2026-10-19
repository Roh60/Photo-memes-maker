package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LayerItem
import com.example.data.model.LayerUtils
import com.example.data.model.MemeTextBox
import com.example.data.model.PlacedSticker
import kotlin.math.roundToInt

/**
 * Interactive Meme Editor component displaying an image with draggable and resizable text overlays,
 * and draggable, scalable, and rotatable sticker overlays.
 */
@Composable
fun DraggableMemeEditor(
    imageBitmap: Bitmap,
    textBoxes: List<MemeTextBox>,
    selectedTextBoxId: String?,
    onSelectTextBox: (String?) -> Unit,
    onUpdateTextBox: (MemeTextBox) -> Unit,
    onDeleteTextBox: (String) -> Unit,
    stickers: List<PlacedSticker> = emptyList(),
    selectedStickerId: String? = null,
    onSelectSticker: (String?) -> Unit = {},
    onUpdateSticker: (PlacedSticker) -> Unit = {},
    onDeleteSticker: (String) -> Unit = {},
    layerOrder: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures {
                    // Tap on canvas background deselects active text box and sticker
                    onSelectTextBox(null)
                    onSelectSticker(null)
                }
            }
            .testTag("draggable_meme_editor_container"),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val canvasWidthPx = constraints.maxWidth.toFloat()
        val canvasHeightPx = constraints.maxHeight.toFloat()

        // Calculate aspect fit bounds of the image
        val imgBmp = remember(imageBitmap) { imageBitmap.asImageBitmap() }
        val scale = minOf(
            canvasWidthPx / imgBmp.width.toFloat(),
            canvasHeightPx / imgBmp.height.toFloat()
        )
        val imageWidthPx = imgBmp.width * scale
        val imageHeightPx = imgBmp.height * scale
        val imageLeftPx = (canvasWidthPx - imageWidthPx) / 2f
        val imageTopPx = (canvasHeightPx - imageHeightPx) / 2f

        // Draw Base Image on Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawImage(
                image = imgBmp,
                dstOffset = IntOffset(imageLeftPx.toInt(), imageTopPx.toInt()),
                dstSize = androidx.compose.ui.unit.IntSize(imageWidthPx.toInt(), imageHeightPx.toInt())
            )
        }

        // Build and sort layers according to layerOrder (from back to front)
        val sortedLayers = remember(textBoxes, stickers, layerOrder) {
            LayerUtils.buildSortedLayers(textBoxes, stickers, layerOrder)
        }

        // Render each layer respecting visibility and layer stack order
        sortedLayers.forEach { layer ->
            when (layer) {
                is LayerItem.Text -> {
                    val textBox = layer.textBox
                    if (!textBox.isVisible) return@forEach
                    val isSelected = textBox.id == selectedTextBoxId

                    DraggableResizableTextOverlay(
                        textBox = textBox,
                        isSelected = isSelected,
                        imageLeftPx = imageLeftPx,
                        imageTopPx = imageTopPx,
                        imageWidthPx = imageWidthPx,
                        imageHeightPx = imageHeightPx,
                        onSelect = {
                            onSelectSticker(null)
                            onSelectTextBox(textBox.id)
                        },
                        onPositionChange = { newXPercent, newYPercent ->
                            onUpdateTextBox(
                                textBox.copy(
                                    xPercent = newXPercent.coerceIn(0.05f, 0.95f),
                                    yPercent = newYPercent.coerceIn(0.05f, 0.95f)
                                )
                            )
                        },
                        onSizeChange = { newFontSizeSp ->
                            onUpdateTextBox(
                                textBox.copy(
                                    fontSizeSp = newFontSizeSp.coerceIn(14f, 80f)
                                )
                            )
                        },
                        onDelete = { onDeleteTextBox(textBox.id) }
                    )
                }

                is LayerItem.Sticker -> {
                    val sticker = layer.sticker
                    if (!sticker.isVisible) return@forEach
                    val isSelected = sticker.id == selectedStickerId

                    DraggableScalableRotatableStickerOverlay(
                        sticker = sticker,
                        isSelected = isSelected,
                        imageLeftPx = imageLeftPx,
                        imageTopPx = imageTopPx,
                        imageWidthPx = imageWidthPx,
                        imageHeightPx = imageHeightPx,
                        onSelect = {
                            onSelectTextBox(null)
                            onSelectSticker(sticker.id)
                        },
                        onUpdate = onUpdateSticker,
                        onDelete = { onDeleteSticker(sticker.id) }
                    )
                }
            }
        }
    }
}

/**
 * Individual Text Overlay supporting Dragging to reposition and Resizing via gestures & corner handles.
 */
@Composable
private fun DraggableResizableTextOverlay(
    textBox: MemeTextBox,
    isSelected: Boolean,
    imageLeftPx: Float,
    imageTopPx: Float,
    imageWidthPx: Float,
    imageHeightPx: Float,
    onSelect: () -> Unit,
    onPositionChange: (newXPercent: Float, newYPercent: Float) -> Unit,
    onSizeChange: (newSizeSp: Float) -> Unit,
    onDelete: () -> Unit
) {
    val currentXPx = imageLeftPx + (textBox.xPercent * imageWidthPx)
    val currentYPx = imageTopPx + (textBox.yPercent * imageHeightPx)

    val dragModifier = if (!textBox.isLocked) {
        Modifier.pointerInput(textBox.id, imageWidthPx, imageHeightPx) {
            detectDragGestures(
                onDragStart = { onSelect() },
                onDrag = { change, dragAmount ->
                    change.consume()
                    val deltaXPercent = dragAmount.x / imageWidthPx
                    val deltaYPercent = dragAmount.y / imageHeightPx
                    onPositionChange(
                        textBox.xPercent + deltaXPercent,
                        textBox.yPercent + deltaYPercent
                    )
                }
            )
        }
    } else Modifier

    val pinchResizeModifier = if (!textBox.isLocked) {
        Modifier.pointerInput(textBox.id) {
            detectTransformGestures { _, _, zoom, _ ->
                if (zoom != 1f) {
                    val newSize = textBox.fontSizeSp * zoom
                    onSizeChange(newSize)
                }
            }
        }
    } else Modifier

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (currentXPx - (imageWidthPx * 0.45f)).roundToInt(),
                    y = (currentYPx - 30.dp.toPx()).roundToInt()
                )
            }
            .width((imageWidthPx * 0.9f / LocalDensity.current.density).dp)
            .then(dragModifier)
            .then(pinchResizeModifier)
            .pointerInput(textBox.id) {
                detectTapGestures(
                    onTap = { onSelect() }
                )
            }
            .testTag("meme_text_box_${textBox.id}"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .then(
                    if (isSelected) {
                        Modifier
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    } else {
                        Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            val textToRender = if (textBox.isAllCaps) textBox.text.uppercase() else textBox.text
            val chosenFontFamily = TextOverlayUtils.resolveFontFamily(textBox.fontId)
            val shadowSpec = if (textBox.hasShadow) {
                androidx.compose.ui.graphics.Shadow(
                    color = Color(textBox.shadowColor),
                    offset = androidx.compose.ui.geometry.Offset(textBox.shadowDx, textBox.shadowDy),
                    blurRadius = textBox.shadowRadius
                )
            } else null

            Box(
                modifier = Modifier
                    .rotate(textBox.rotation)
                    .then(
                        if (textBox.hasBackground) {
                            Modifier
                                .background(
                                    Color(textBox.backgroundColor),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (textBox.hasStroke) {
                    Text(
                        text = textToRender,
                        fontSize = textBox.fontSizeSp.sp,
                        fontFamily = chosenFontFamily,
                        fontWeight = if (textBox.isBold) FontWeight.Black else FontWeight.Bold,
                        fontStyle = if (textBox.isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                        textAlign = when (textBox.textAlign) {
                            "left" -> TextAlign.Left
                            "right" -> TextAlign.Right
                            else -> TextAlign.Center
                        },
                        color = Color(textBox.strokeColor),
                        modifier = Modifier.offset(1.5.dp, 1.5.dp)
                    )
                }

                Text(
                    text = textToRender,
                    fontSize = textBox.fontSizeSp.sp,
                    fontFamily = chosenFontFamily,
                    fontWeight = if (textBox.isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (textBox.isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                    textAlign = when (textBox.textAlign) {
                        "left" -> TextAlign.Left
                        "right" -> TextAlign.Right
                        else -> TextAlign.Center
                    },
                    color = Color(textBox.textColor),
                    style = androidx.compose.ui.text.TextStyle(shadow = shadowSpec)
                )
            }

            if (isSelected) {
                if (textBox.isLocked) {
                    // Locked Badge Indicator
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 10.dp, y = (-10).dp)
                            .size(26.dp)
                            .testTag("text_box_locked_badge_${textBox.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked Text",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 10.dp, y = (-10).dp)
                            .size(24.dp)
                            .clickable { onDelete() }
                            .testTag("delete_text_box_btn")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 10.dp, y = 10.dp)
                            .size(26.dp)
                            .pointerInput(textBox.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val delta = (dragAmount.x + dragAmount.y) * 0.25f
                                    onSizeChange(textBox.fontSizeSp + delta)
                                }
                            }
                            .testTag("resize_handle_${textBox.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.OpenInFull,
                                contentDescription = "Resize",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-10).dp, y = (-10).dp)
                            .size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DragHandle,
                                contentDescription = "Drag",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Sticker Overlay supporting Dragging to reposition, Pinch-to-Scale, and Two-Finger Rotation,
 * plus interactive corner handles for Scale, Rotation, and Deletion.
 */
@Composable
fun DraggableScalableRotatableStickerOverlay(
    sticker: PlacedSticker,
    isSelected: Boolean,
    imageLeftPx: Float,
    imageTopPx: Float,
    imageWidthPx: Float,
    imageHeightPx: Float,
    onSelect: () -> Unit,
    onUpdate: (PlacedSticker) -> Unit,
    onDelete: () -> Unit
) {
    val density = LocalDensity.current

    // Target center coordinates in container
    val currentCenterXPx = imageLeftPx + (sticker.xPercent * imageWidthPx)
    val currentCenterYPx = imageTopPx + (sticker.yPercent * imageHeightPx)

    // Base sticker size relative to canvas
    val baseSizePx = imageWidthPx * 0.22f
    val effectiveSizePx = baseSizePx * sticker.scale
    val effectiveSizeDp = with(density) { effectiveSizePx.toDp() }

    // Direct gestures on sticker: Drag to move, Pinch to zoom, Twist to rotate
    val dragModifier = if (!sticker.isLocked) {
        Modifier.pointerInput(sticker.id, imageWidthPx, imageHeightPx) {
            detectDragGestures(
                onDragStart = { onSelect() },
                onDrag = { change, dragAmount ->
                    change.consume()
                    val deltaXPercent = dragAmount.x / imageWidthPx
                    val deltaYPercent = dragAmount.y / imageHeightPx
                    onUpdate(
                        sticker.copy(
                            xPercent = (sticker.xPercent + deltaXPercent).coerceIn(0.05f, 0.95f),
                            yPercent = (sticker.yPercent + deltaYPercent).coerceIn(0.05f, 0.95f)
                        )
                    )
                }
            )
        }
    } else Modifier

    val transformModifier = if (!sticker.isLocked) {
        Modifier.pointerInput(sticker.id) {
            detectTransformGestures { _, pan, zoom, rotationDegrees ->
                val newScale = (sticker.scale * zoom).coerceIn(0.3f, 3.5f)
                val newRot = (sticker.rotation + rotationDegrees) % 360f
                val deltaXPercent = pan.x / imageWidthPx
                val deltaYPercent = pan.y / imageHeightPx
                onUpdate(
                    sticker.copy(
                        scale = newScale,
                        rotation = newRot,
                        xPercent = (sticker.xPercent + deltaXPercent).coerceIn(0.05f, 0.95f),
                        yPercent = (sticker.yPercent + deltaYPercent).coerceIn(0.05f, 0.95f)
                    )
                )
            }
        }
    } else Modifier

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (currentCenterXPx - (effectiveSizePx / 2f)).roundToInt(),
                    y = (currentCenterYPx - (effectiveSizePx / 2f)).roundToInt()
                )
            }
            .size(effectiveSizeDp)
            .rotate(sticker.rotation)
            .then(dragModifier)
            .then(transformModifier)
            .pointerInput(sticker.id) {
                detectTapGestures(onTap = { onSelect() })
            }
            .testTag("placed_sticker_${sticker.id}"),
        contentAlignment = Alignment.Center
    ) {
        // Selection bounding box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isSelected) {
                        Modifier
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp)
                    } else {
                        Modifier.padding(4.dp)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            // Sticker Artwork
            if (sticker.drawableResId != null) {
                Image(
                    painter = painterResource(id = sticker.drawableResId),
                    contentDescription = sticker.label,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (!sticker.emoji.isNullOrEmpty()) {
                Text(
                    text = sticker.emoji,
                    fontSize = with(density) { (effectiveSizePx * 0.72f).toSp() },
                    textAlign = TextAlign.Center
                )
            }

            // Interactive Handles when Selected
            if (isSelected) {
                if (sticker.isLocked) {
                    // Locked Badge Indicator
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 10.dp, y = (-10).dp)
                            .size(26.dp)
                            .testTag("sticker_locked_badge_${sticker.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked Sticker",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    // Top-Right: Delete Handle
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 10.dp, y = (-10).dp)
                            .size(26.dp)
                            .clickable { onDelete() }
                            .testTag("delete_sticker_handle_${sticker.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Bottom-Right: Scale Handle
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 10.dp, y = 10.dp)
                            .size(28.dp)
                            .pointerInput(sticker.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val delta = (dragAmount.x + dragAmount.y) / (baseSizePx * 1.2f)
                                    val newScale = (sticker.scale + delta).coerceIn(0.3f, 3.5f)
                                    onUpdate(sticker.copy(scale = newScale))
                                }
                            }
                            .testTag("scale_sticker_handle_${sticker.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.OpenInFull,
                                contentDescription = "Scale",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Top-Left: Rotate Handle
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-10).dp, y = (-10).dp)
                            .size(26.dp)
                            .pointerInput(sticker.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val rotDelta = dragAmount.x * 1.5f
                                    val nextRot = (sticker.rotation + rotDelta) % 360f
                                    onUpdate(sticker.copy(rotation = nextRot))
                                }
                            }
                            .testTag("rotate_sticker_handle_${sticker.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Rotate90DegreesCw,
                                contentDescription = "Rotate",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Bottom-Left: Reset Rotation Handle (0°)
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = (-10).dp, y = 10.dp)
                            .size(24.dp)
                            .clickable { onUpdate(sticker.copy(rotation = 0f)) }
                            .testTag("reset_sticker_rot_handle_${sticker.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Angle",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
