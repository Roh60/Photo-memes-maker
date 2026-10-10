package com.example.ui.mememaker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DrawableStickerItem
import com.example.data.model.PlacedSticker
import com.example.data.model.StickerItem
import com.example.data.model.StickerLibrary
import com.example.ui.components.ValueSliderRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerPickerModalBottomSheet(
    onDismissRequest: () -> Unit,
    onSelectDrawableSticker: (DrawableStickerItem) -> Unit,
    onSelectEmojiSticker: (StickerItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf(
        "All",
        "Trending",
        "Glasses & Props",
        "Badges & Stamps",
        "Bubbles & Bursts",
        "Emojis"
    )

    val filteredDrawables = remember(selectedCategory, searchQuery) {
        val list = if (selectedCategory == "All" || selectedCategory == "Emojis") {
            StickerLibrary.DRAWABLE_STICKERS
        } else {
            StickerLibrary.getDrawableStickersByCategory(selectedCategory)
        }
        if (searchQuery.isBlank()) list else {
            list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val filteredEmojis = remember(selectedCategory, searchQuery) {
        if (selectedCategory != "All" && selectedCategory != "Emojis") {
            emptyList()
        } else {
            val list = StickerLibrary.STICKERS
            if (searchQuery.isBlank()) list else {
                list.filter {
                    it.label.contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("sticker_picker_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🎨 Sticker Asset Library",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap to add overlay stickers to your meme",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.testTag("close_sticker_picker_btn")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search glasses, stamps, crown, fire...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sticker_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Categories horizontal chip row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        modifier = Modifier.testTag("category_chip_$category")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stickers Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .testTag("sticker_grid")
            ) {
                // Section: Drawable Asset Stickers
                items(filteredDrawables, key = { "drawable_${it.id}" }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.95f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectDrawableSticker(item) }
                            .testTag("sticker_item_${item.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = item.resId),
                                    contentDescription = item.name,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Section: Emoji items (when in All or Emojis category)
                if (selectedCategory == "All" || selectedCategory == "Emojis") {
                    items(filteredEmojis, key = { "emoji_${it.label}_${it.emoji}" }) { emojiItem ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.95f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectEmojiSticker(emojiItem) }
                                .testTag("emoji_item_${emojiItem.label}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = emojiItem.emoji,
                                    fontSize = 38.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = emojiItem.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
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
 * Controller tray for selected sticker with scale, rotation, and quick actions
 */
@Composable
fun StickerInspectorControls(
    selectedSticker: PlacedSticker,
    onUpdateSticker: (PlacedSticker) -> Unit,
    onDeselect: () -> Unit,
    onDelete: () -> Unit,
    onOpenPicker: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sticker_inspector_controls")
    ) {
        // Active sticker status bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (selectedSticker.drawableResId != null) {
                            Image(
                                painter = painterResource(id = selectedSticker.drawableResId),
                                contentDescription = selectedSticker.label,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(selectedSticker.emoji ?: "✨", fontSize = 18.sp)
                        }
                    }
                }
                Column {
                    Text(
                        text = selectedSticker.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Scale: ${(selectedSticker.scale * 100).toInt()}% • Angle: ${selectedSticker.rotation.toInt()}°",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = onDeselect,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp).testTag("deselect_sticker_btn")
                ) {
                    Text("Done", style = MaterialTheme.typography.labelSmall)
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_inspector_sticker_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Sticker",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Scale Slider
        ValueSliderRow(
            label = "Size / Scale",
            value = selectedSticker.scale,
            range = 0.3f..3.0f,
            format = "%.2fx",
            onValueChange = { newScale ->
                onUpdateSticker(selectedSticker.copy(scale = newScale))
            },
            testTag = "sticker_scale_slider"
        )

        // Rotation Slider
        ValueSliderRow(
            label = "Rotation Angle",
            value = selectedSticker.rotation,
            range = -180f..180f,
            format = "%.0f°",
            onValueChange = { newRot ->
                onUpdateSticker(selectedSticker.copy(rotation = newRot))
            },
            testTag = "sticker_rotation_slider"
        )

        // Quick Transformation Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val nextRot = (selectedSticker.rotation + 90f) % 360f
                    onUpdateSticker(selectedSticker.copy(rotation = if (nextRot > 180f) nextRot - 360f else nextRot))
                },
                modifier = Modifier.weight(1f).testTag("rotate_90_sticker_btn"),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                Icon(Icons.Default.Rotate90DegreesCw, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+90°", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    onUpdateSticker(selectedSticker.copy(rotation = 0f))
                },
                modifier = Modifier.weight(1f).testTag("reset_rot_sticker_btn"),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("0°", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    onUpdateSticker(selectedSticker.copy(xPercent = 0.5f, yPercent = 0.5f))
                },
                modifier = Modifier.weight(1f).testTag("center_sticker_btn"),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                Icon(Icons.Default.CenterFocusStrong, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Center", fontSize = 12.sp)
            }

            Button(
                onClick = onOpenPicker,
                modifier = Modifier.weight(1f).testTag("add_more_stickers_btn"),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("More", fontSize = 12.sp)
            }
        }
    }
}
