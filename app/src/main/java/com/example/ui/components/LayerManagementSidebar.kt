package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LayerItem
import com.example.data.model.LayerOrderAction

/**
 * Slide-in Layer Management Sidebar allowing users to:
 * - View all canvas layers (text elements and stickers) in their stack order
 * - Reorder layers (bring to front, send to back, move up, move down)
 * - Toggle visibility for individual elements
 * - Lock/unlock individual elements
 * - Delete individual elements
 * - Quickly tap to select and inspect elements on the canvas
 */
@Composable
fun LayerManagementSidebar(
    isOpen: Boolean,
    onClose: () -> Unit,
    layers: List<LayerItem>, // In drawing order: index 0 is Back, last is Front
    selectedLayerId: String?,
    onSelectLayer: (String) -> Unit,
    onReorderLayer: (layerId: String, action: LayerOrderAction) -> Unit,
    onToggleVisibility: (layerId: String) -> Unit,
    onToggleLock: (layerId: String) -> Unit,
    onDeleteLayer: (layerId: String) -> Unit,
    onAddText: () -> Unit = {},
    onAddSticker: () -> Unit = {},
    onShowAll: () -> Unit = {},
    onHideAll: () -> Unit = {},
    onUnlockAll: () -> Unit = {},
    onLockAll: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable { onClose() }
        ) {
            // Sidebar Surface sliding in from the right edge
            Surface(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .widthIn(min = 300.dp, max = 360.dp)
                    .fillMaxWidth(0.85f)
                    .clickable(enabled = false) {} // Prevent click-through to backdrop
                    .testTag("layer_management_sidebar"),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Layer Manager",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(start = 2.dp)
                            ) {
                                Text(
                                    text = "${layers.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.testTag("close_layer_sidebar_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Layers Sidebar"
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Batch Quick Actions
                    if (layers.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val allVisible = layers.all { it.isVisible }
                            val allLocked = layers.all { it.isLocked }

                            TextButton(
                                onClick = { if (allVisible) onHideAll() else onShowAll() }
                            ) {
                                Icon(
                                    imageVector = if (allVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (allVisible) "Hide All" else "Show All",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            TextButton(
                                onClick = { if (allLocked) onUnlockAll() else onLockAll() }
                            ) {
                                Icon(
                                    imageVector = if (allLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (allLocked) "Unlock All" else "Lock All",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    // Quick Add Layer Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onAddText,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sidebar_add_text_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Text", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = onAddSticker,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sidebar_add_sticker_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.EmojiEmotions, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Sticker", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    // Layers Stack
                    if (layers.isEmpty()) {
                        // Empty State
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Layers,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "No Layers Yet",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Add text captions or stickers to organize, reorder, and lock layers.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                    } else {
                        // Display list from Front (top of UI) to Back (bottom of UI)
                        // In drawing order, index 0 is Back, and lastIndex is Front.
                        // So the UI displays layers.reversed()
                        val displayLayers = layers.asReversed()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "▲ TOP (FRONT)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Stack Order",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(displayLayers, key = { _, item -> item.id }) { displayIndex, layer ->
                                val isSelected = layer.id == selectedLayerId
                                val isTopMost = displayIndex == 0 // Front-most
                                val isBottomMost = displayIndex == displayLayers.lastIndex // Back-most

                                LayerRowCard(
                                    layer = layer,
                                    isSelected = isSelected,
                                    isTopMost = isTopMost,
                                    isBottomMost = isBottomMost,
                                    onSelect = { onSelectLayer(layer.id) },
                                    onToggleVisibility = { onToggleVisibility(layer.id) },
                                    onToggleLock = { onToggleLock(layer.id) },
                                    onBringToFront = { onReorderLayer(layer.id, LayerOrderAction.BRING_TO_FRONT) },
                                    onSendToBack = { onReorderLayer(layer.id, LayerOrderAction.SEND_TO_BACK) },
                                    onMoveUp = { onReorderLayer(layer.id, LayerOrderAction.MOVE_UP) },
                                    onMoveDown = { onReorderLayer(layer.id, LayerOrderAction.MOVE_DOWN) },
                                    onDelete = { onDeleteLayer(layer.id) }
                                )
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp, bottom = 16.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "▼ BOTTOM (BACK)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline,
                                        fontWeight = FontWeight.Bold
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

/**
 * Individual Layer Card in the Sidebar.
 */
@Composable
private fun LayerRowCard(
    layer: LayerItem,
    isSelected: Boolean,
    isTopMost: Boolean,
    isBottomMost: Boolean,
    onSelect: () -> Unit,
    onToggleVisibility: () -> Unit,
    onToggleLock: () -> Unit,
    onBringToFront: () -> Unit,
    onSendToBack: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .testTag("sidebar_layer_item_${layer.id}"),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        } else if (!layer.isVisible) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Header: Icon + Name + Quick Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Layer Type Icon / Preview
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (layer) {
                        is LayerItem.Text -> MaterialTheme.colorScheme.secondaryContainer
                        is LayerItem.Sticker -> MaterialTheme.colorScheme.tertiaryContainer
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        when (layer) {
                            is LayerItem.Text -> {
                                Icon(
                                    imageVector = Icons.Default.TextFields,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            is LayerItem.Sticker -> {
                                val stk = layer.sticker
                                if (stk.drawableResId != null) {
                                    Image(
                                        painter = painterResource(id = stk.drawableResId),
                                        contentDescription = stk.label,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else if (stk.emoji.isNotBlank()) {
                                    Text(
                                        text = stk.emoji,
                                        fontSize = 18.sp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEmotions,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Title & Subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = layer.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        if (layer.isLocked) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "LOCKED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                        if (!layer.isVisible) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            ) {
                                Text(
                                    text = "HIDDEN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = when (layer) {
                            is LayerItem.Text -> "Text (${layer.textBox.fontSizeSp.toInt()}sp)"
                            is LayerItem.Sticker -> "Sticker (scale ${String.format("%.1fx", layer.sticker.scale)})"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Toolbar Row: Visibility | Lock | Bring to Front | Up | Down | Send to Back | Delete
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Visibility Toggle
                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("sidebar_visibility_${layer.id}")
                ) {
                    Icon(
                        imageVector = if (layer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (layer.isVisible) "Hide layer" else "Show layer",
                        tint = if (layer.isVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Lock Toggle
                IconButton(
                    onClick = onToggleLock,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("sidebar_lock_${layer.id}")
                ) {
                    Icon(
                        imageVector = if (layer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = if (layer.isLocked) "Unlock layer" else "Lock layer",
                        tint = if (layer.isLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Bring to Front (Move to top)
                IconButton(
                    onClick = onBringToFront,
                    enabled = !isTopMost,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("sidebar_front_${layer.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.VerticalAlignTop,
                        contentDescription = "Bring to front",
                        tint = if (!isTopMost) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Move Up (Bring forward 1 step)
                IconButton(
                    onClick = onMoveUp,
                    enabled = !isTopMost,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("sidebar_up_${layer.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Move up",
                        tint = if (!isTopMost) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Move Down (Send backward 1 step)
                IconButton(
                    onClick = onMoveDown,
                    enabled = !isBottomMost,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("sidebar_down_${layer.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Move down",
                        tint = if (!isBottomMost) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Send to Back (Move to bottom)
                IconButton(
                    onClick = onSendToBack,
                    enabled = !isBottomMost,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("sidebar_back_${layer.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.VerticalAlignBottom,
                        contentDescription = "Send to back",
                        tint = if (!isBottomMost) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Delete Layer
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("sidebar_delete_${layer.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete layer",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
