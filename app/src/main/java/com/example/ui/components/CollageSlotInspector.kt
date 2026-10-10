package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CollageSlotState

/**
 * Inspector toolbar allowing independent adjustment of the selected image within its frame slot.
 * Controls: Pan, Zoom/Scale, Rotation, Flip Horiz/Vert, Filters, Replace, Swap, and Clear.
 */
@Composable
fun CollageSlotInspector(
    slotState: CollageSlotState,
    totalSlots: Int,
    onUpdateState: (CollageSlotState) -> Unit,
    onReplacePhoto: () -> Unit,
    onSwapWithSlot: (Int) -> Unit,
    onClearPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("slot_inspector_card"),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Slot title & Primary actions (Replace, Swap, Clear)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${slotState.slotId}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Adjust Photo #${slotState.slotId}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onReplacePhoto,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("slot_replace_photo_btn"),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Replace", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onClearPhoto,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("slot_clear_photo_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Zoom / Scale Row with Slider & -/+ buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zoom: ${String.format("%.1f", slotState.scale)}x",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.width(76.dp)
                )

                IconButton(
                    onClick = {
                        val newScale = (slotState.scale - 0.1f).coerceIn(1.0f, 4.0f)
                        onUpdateState(slotState.copy(scale = newScale))
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom out", modifier = Modifier.size(18.dp))
                }

                Slider(
                    value = slotState.scale,
                    onValueChange = { newScale ->
                        onUpdateState(slotState.copy(scale = newScale))
                    },
                    valueRange = 1.0f..4.0f,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("slot_zoom_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                IconButton(
                    onClick = {
                        val newScale = (slotState.scale + 0.1f).coerceIn(1.0f, 4.0f)
                        onUpdateState(slotState.copy(scale = newScale))
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom in", modifier = Modifier.size(18.dp))
                }
            }

            // Pan Position & Orientation Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 4-Way Pan Nudge Pad + Center
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                    text = "Pan:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                    IconButton(
                        onClick = {
                            onUpdateState(slotState.copy(panX = slotState.panX - 0.08f))
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("slot_pan_left_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Pan Left",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            onUpdateState(slotState.copy(panY = slotState.panY - 0.08f))
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("slot_pan_up_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Pan Up",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            onUpdateState(slotState.copy(panY = slotState.panY + 0.08f))
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("slot_pan_down_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Pan Down",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            onUpdateState(slotState.copy(panX = slotState.panX + 0.08f))
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("slot_pan_right_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Pan Right",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            onUpdateState(slotState.copy(panX = 0f, panY = 0f))
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("slot_pan_center_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = "Center Image",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Rotate & Flip buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            val newRot = (slotState.rotation - 90f) % 360f
                            onUpdateState(slotState.copy(rotation = newRot))
                        },
                        modifier = Modifier.size(32.dp).testTag("slot_rot_ccw_btn")
                    ) {
                        Icon(Icons.Default.RotateLeft, contentDescription = "Rotate CCW", modifier = Modifier.size(20.dp))
                    }

                    IconButton(
                        onClick = {
                            val newRot = (slotState.rotation + 90f) % 360f
                            onUpdateState(slotState.copy(rotation = newRot))
                        },
                        modifier = Modifier.size(32.dp).testTag("slot_rot_cw_btn")
                    ) {
                        Icon(Icons.Default.RotateRight, contentDescription = "Rotate CW", modifier = Modifier.size(20.dp))
                    }

                    IconButton(
                        onClick = {
                            onUpdateState(slotState.copy(isFlippedHorizontal = !slotState.isFlippedHorizontal))
                        },
                        modifier = Modifier.size(32.dp).testTag("slot_flip_h_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flip,
                            contentDescription = "Flip Horizontal",
                            tint = if (slotState.isFlippedHorizontal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            onUpdateState(slotState.copy(panX = 0f, panY = 0f, scale = 1.0f, rotation = 0f, isFlippedHorizontal = false, isFlippedVertical = false, filter = "none"))
                        },
                        modifier = Modifier.size(32.dp).testTag("slot_reset_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Independent Photo Filter for this slot
            Text(
                text = "Photo Filter:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val filters = listOf(
                "none" to "Normal",
                "vivid" to "Vivid",
                "warm" to "Warm",
                "cool" to "Cool",
                "bw" to "B&W",
                "sepia" to "Sepia",
                "vintage" to "Vintage",
                "bright" to "Bright"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filters.forEach { (key, label) ->
                    val isSelected = slotState.filter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdateState(slotState.copy(filter = key)) },
                        label = { Text(label, fontSize = 11.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        } else null,
                        modifier = Modifier.testTag("slot_filter_$key"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Quick Swap with other slots (if totalSlots > 1)
            if (totalSlots > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Swap with:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 1..totalSlots) {
                            if (i != slotState.slotId) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clickable { onSwapWithSlot(i) }
                                        .testTag("swap_slot_$i")
                                ) {
                                    Text(
                                        text = "Slot $i",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
