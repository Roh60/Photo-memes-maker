package com.example.ui.components

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.data.model.MemeTextBox
import com.example.ui.theme.AntonFontFamily
import com.example.ui.theme.KalamFontFamily
import com.example.ui.theme.PacificoFontFamily
import com.example.ui.theme.PoppinsFontFamily
import com.example.ui.theme.RozhaOneFontFamily

/**
 * Font metadata item with Compose FontFamily and optional Android Resource ID.
 */
data class FontOption(
    val id: String,
    val name: String,
    val styleDesc: String,
    val fontFamily: FontFamily,
    @DrawableRes val fontResId: Int? = null,
    val previewWord: String = "Design"
)

/**
 * Pre-defined Shadow Presets for instant high-impact text styling.
 */
data class ShadowPreset(
    val name: String,
    val color: Long,
    val radius: Float,
    val dx: Float,
    val dy: Float
)

object TextOverlayUtils {

    val FONTS: List<FontOption> = listOf(
        FontOption("Anton", "Anton", "Bold Meme Header", AntonFontFamily, R.font.anton, "IMPACT"),
        FontOption("Pacifico", "Pacifico", "Vintage Cursive", PacificoFontFamily, R.font.pacifico, "Script"),
        FontOption("Poppins", "Poppins", "Modern Sans", PoppinsFontFamily, R.font.poppins, "Modern"),
        FontOption("Kalam", "Kalam", "Casual Handwritten", KalamFontFamily, R.font.kalam, "Kalam"),
        FontOption("Rozha", "Rozha One", "Editorial Serif", RozhaOneFontFamily, R.font.rozha_one, "Classic"),
        FontOption("Impact", "Impact (System)", "Classic Meme", FontFamily.Default, null, "RETRO"),
        FontOption("Sans-Serif", "Sans Serif", "Clean & Legible", FontFamily.SansSerif, null, "Clean"),
        FontOption("Serif", "Serif", "Sophisticated", FontFamily.Serif, null, "Story"),
        FontOption("Monospace", "Monospace", "Typewriter / Tech", FontFamily.Monospace, null, "Code"),
        FontOption("Cursive", "Cursive (System)", "Casual Script", FontFamily.Cursive, null, "Elegance")
    )

    val SHADOW_PRESETS: List<ShadowPreset> = listOf(
        ShadowPreset("Soft Noir", 0xCC000000, 8f, 3f, 4f),
        ShadowPreset("Deep Drop", 0xEE000000, 4f, 5f, 6f),
        ShadowPreset("White Glow", 0xBBFFFFFF, 12f, 0f, 0f),
        ShadowPreset("Neon Cyan", 0xDD00F5FF, 10f, 0f, 0f),
        ShadowPreset("Warm Amber", 0xDDFB923C, 8f, 2f, 3f),
        ShadowPreset("Ruby Red", 0xDDE11D48, 8f, 3f, 3f),
        ShadowPreset("Pop 3D", 0xDD2563EB, 1f, 5f, 5f)
    )

    fun resolveFontFamily(fontId: String): FontFamily {
        return FONTS.firstOrNull { it.id.equals(fontId, ignoreCase = true) }?.fontFamily ?: AntonFontFamily
    }

    fun resolveTypeface(context: Context?, fontId: String, isBold: Boolean, isItalic: Boolean): Typeface {
        val option = FONTS.firstOrNull { it.id.equals(fontId, ignoreCase = true) }
        val style = when {
            isBold && isItalic -> Typeface.BOLD_ITALIC
            isBold -> Typeface.BOLD
            isItalic -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }

        if (context != null && option?.fontResId != null) {
            try {
                val resTf = ResourcesCompat.getFont(context, option.fontResId)
                if (resTf != null) {
                    return Typeface.create(resTf, style)
                }
            } catch (e: Exception) {
                // fallback
            }
        }

        return when (fontId.lowercase()) {
            "serif" -> Typeface.create(Typeface.SERIF, style)
            "monospace" -> Typeface.create(Typeface.MONOSPACE, style)
            "sans-serif" -> Typeface.create(Typeface.SANS_SERIF, style)
            else -> Typeface.create(Typeface.DEFAULT_BOLD, style)
        }
    }
}

/**
 * Material 3 Bottom Sheet for comprehensive Text Overlay customization
 * covering Text, Fonts, Fill Colors, Stroke Outlines, Shadows, Alignment, and Formatting.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextOverlayEditorSheet(
    textBox: MemeTextBox,
    onDismissRequest: () -> Unit,
    onApply: (MemeTextBox) -> Unit
) {
    var textContent by remember { mutableStateOf(textBox.text) }
    var fontId by remember { mutableStateOf(textBox.fontId) }
    var fontSize by remember { mutableFloatStateOf(textBox.fontSizeSp) }
    var textColor by remember { mutableStateOf(textBox.textColor) }

    var hasStroke by remember { mutableStateOf(textBox.hasStroke) }
    var strokeColor by remember { mutableStateOf(textBox.strokeColor) }
    var strokeWidth by remember { mutableFloatStateOf(textBox.strokeWidth) }

    var hasShadow by remember { mutableStateOf(textBox.hasShadow) }
    var shadowColor by remember { mutableStateOf(textBox.shadowColor) }
    var shadowRadius by remember { mutableFloatStateOf(textBox.shadowRadius) }
    var shadowDx by remember { mutableFloatStateOf(textBox.shadowDx) }
    var shadowDy by remember { mutableFloatStateOf(textBox.shadowDy) }

    var hasBackground by remember { mutableStateOf(textBox.hasBackground) }
    var backgroundColor by remember { mutableStateOf(textBox.backgroundColor) }

    var isBold by remember { mutableStateOf(textBox.isBold) }
    var isItalic by remember { mutableStateOf(textBox.isItalic) }
    var isAllCaps by remember { mutableStateOf(textBox.isAllCaps) }
    var textAlign by remember { mutableStateOf(textBox.textAlign) }
    var rotation by remember { mutableFloatStateOf(textBox.rotation) }

    var selectedTab by remember { mutableStateOf(0) } // 0: Font & Content, 1: Color & Stroke, 2: Shadows, 3: Layout

    var previewBackdropDark by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Text Overlay Tool",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Live Preview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("text_preview_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (previewBackdropDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val displayedText = if (isAllCaps) {
                        textContent.ifBlank { "Sample Text" }.uppercase()
                    } else {
                        textContent.ifBlank { "Sample Text" }
                    }

                    val chosenFontFamily = remember(fontId) {
                        TextOverlayUtils.resolveFontFamily(fontId)
                    }

                    val shadowSpec = if (hasShadow) {
                        Shadow(
                            color = Color(shadowColor),
                            offset = Offset(shadowDx, shadowDy),
                            blurRadius = shadowRadius
                        )
                    } else null

                    Box(
                        modifier = Modifier
                            .rotate(rotation)
                            .then(
                                if (hasBackground) {
                                    Modifier
                                        .background(
                                            Color(backgroundColor),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outline pass if enabled
                        if (hasStroke) {
                            Text(
                                text = displayedText,
                                fontSize = fontSize.coerceIn(16f, 36f).sp,
                                fontFamily = chosenFontFamily,
                                fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold,
                                fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                                textAlign = when (textAlign) {
                                    "left" -> TextAlign.Left
                                    "right" -> TextAlign.Right
                                    else -> TextAlign.Center
                                },
                                color = Color(strokeColor),
                                modifier = Modifier.offset(1.5.dp, 1.5.dp)
                            )
                        }

                        // Main Text Fill pass with Shadow
                        Text(
                            text = displayedText,
                            fontSize = fontSize.coerceIn(16f, 36f).sp,
                            fontFamily = chosenFontFamily,
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                            textAlign = when (textAlign) {
                                "left" -> TextAlign.Left
                                "right" -> TextAlign.Right
                                else -> TextAlign.Center
                            },
                            color = Color(textColor),
                            style = TextStyle(shadow = shadowSpec)
                        )
                    }

                    // Backdrop toggle pill (Dark / Light)
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clickable { previewBackdropDark = !previewBackdropDark }
                            .testTag("preview_backdrop_toggle"),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.45f)
                    ) {
                        Text(
                            text = if (previewBackdropDark) "☀️ Light BG" else "🌙 Dark BG",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Font & Text", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    modifier = Modifier.testTag("text_tab_font")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Colors", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.FormatColorFill, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    modifier = Modifier.testTag("text_tab_color")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Shadows", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.FormatColorText, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    modifier = Modifier.testTag("text_tab_shadow")
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Layout", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.FormatSize, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    modifier = Modifier.testTag("text_tab_layout")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                // Tab 0: Font & Content
                0 -> {
                    OutlinedTextField(
                        value = textContent,
                        onValueChange = { textContent = it },
                        label = { Text("Overlay Text Message") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("text_overlay_input_field"),
                        trailingIcon = {
                            if (textContent.isNotEmpty()) {
                                IconButton(onClick = { textContent = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Select Typography Font",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Horizontal selectable font cards
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextOverlayUtils.FONTS.forEach { opt ->
                            val isSelected = fontId.equals(opt.id, ignoreCase = true)
                            Card(
                                modifier = Modifier
                                    .width(110.dp)
                                    .clickable { fontId = opt.id }
                                    .testTag("font_opt_${opt.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = opt.previewWord,
                                        fontFamily = opt.fontFamily,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = opt.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = opt.styleDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // Tab 1: Colors & Stroke
                1 -> {
                    Text(
                        text = "Text Fill Color",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    PaletteColorPicker(
                        selectedColor = textColor,
                        onColorSelected = { textColor = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Outline Stroke Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Outline / Stroke",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Crisp borders around letters",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = hasStroke,
                            onCheckedChange = { hasStroke = it },
                            modifier = Modifier.testTag("toggle_stroke_switch")
                        )
                    }

                    if (hasStroke) {
                        PaletteColorPicker(
                            selectedColor = strokeColor,
                            onColorSelected = { strokeColor = it }
                        )
                        ValueSliderRow(
                            label = "Stroke Width",
                            value = strokeWidth,
                            range = 1f..10f,
                            onValueChange = { strokeWidth = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Background Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Background Banner / Pill",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Highlight box behind text",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = hasBackground,
                            onCheckedChange = { hasBackground = it },
                            modifier = Modifier.testTag("toggle_bg_switch")
                        )
                    }

                    if (hasBackground) {
                        PaletteColorPicker(
                            selectedColor = backgroundColor,
                            onColorSelected = { backgroundColor = it }
                        )
                    }
                }

                // Tab 2: Shadows
                2 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Drop Shadow Effect",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Depth, glow, and 3D shadow layers",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = hasShadow,
                            onCheckedChange = { hasShadow = it },
                            modifier = Modifier.testTag("toggle_shadow_switch")
                        )
                    }

                    if (hasShadow) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Quick Shadow Presets",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TextOverlayUtils.SHADOW_PRESETS.forEach { preset ->
                                FilterChip(
                                    selected = shadowColor == preset.color && shadowRadius == preset.radius,
                                    onClick = {
                                        shadowColor = preset.color
                                        shadowRadius = preset.radius
                                        shadowDx = preset.dx
                                        shadowDy = preset.dy
                                    },
                                    label = { Text(preset.name, fontSize = 11.sp) },
                                    modifier = Modifier.testTag("shadow_preset_${preset.name}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Shadow Color",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        PaletteColorPicker(
                            selectedColor = shadowColor,
                            onColorSelected = { shadowColor = it }
                        )

                        ValueSliderRow(
                            label = "Shadow Blur Radius",
                            value = shadowRadius,
                            range = 0f..24f,
                            onValueChange = { shadowRadius = it }
                        )

                        ValueSliderRow(
                            label = "Shadow Distance (X)",
                            value = shadowDx,
                            range = -15f..15f,
                            onValueChange = { shadowDx = it }
                        )

                        ValueSliderRow(
                            label = "Shadow Distance (Y)",
                            value = shadowDy,
                            range = -15f..15f,
                            onValueChange = { shadowDy = it }
                        )
                    }
                }

                // Tab 3: Layout & Spacing
                3 -> {
                    ValueSliderRow(
                        label = "Font Size",
                        value = fontSize,
                        range = 14f..72f,
                        onValueChange = { fontSize = it }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Text Styling",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isBold,
                            onClick = { isBold = !isBold },
                            label = { Text("Bold") },
                            leadingIcon = { Icon(Icons.Default.FormatBold, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.weight(1f).testTag("chip_bold")
                        )
                        FilterChip(
                            selected = isItalic,
                            onClick = { isItalic = !isItalic },
                            label = { Text("Italic") },
                            leadingIcon = { Icon(Icons.Default.FormatItalic, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.weight(1f).testTag("chip_italic")
                        )
                        FilterChip(
                            selected = isAllCaps,
                            onClick = { isAllCaps = !isAllCaps },
                            label = { Text("CAPS") },
                            modifier = Modifier.weight(1f).testTag("chip_all_caps")
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Alignment",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "left" to Icons.Default.FormatAlignLeft,
                            "center" to Icons.Default.FormatAlignCenter,
                            "right" to Icons.Default.FormatAlignRight
                        ).forEach { (alignKey, icon) ->
                            FilterChip(
                                selected = textAlign == alignKey,
                                onClick = { textAlign = alignKey },
                                label = { Text(alignKey.replaceFirstChar { it.uppercase() }) },
                                leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.weight(1f).testTag("chip_align_$alignKey")
                            )
                        }
                    }

                    ValueSliderRow(
                        label = "Rotation Angle",
                        value = rotation,
                        range = -180f..180f,
                        onValueChange = { rotation = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        fontId = "Anton"
                        fontSize = 28f
                        textColor = 0xFFFFFFFF
                        hasStroke = true
                        strokeColor = 0xFF000000
                        strokeWidth = 3.5f
                        hasShadow = true
                        shadowColor = 0xAA000000
                        shadowRadius = 6f
                        shadowDx = 3f
                        shadowDy = 3f
                        hasBackground = false
                        isBold = true
                        isItalic = false
                        isAllCaps = true
                        textAlign = "center"
                        rotation = 0f
                    },
                    modifier = Modifier.weight(1f).testTag("text_overlay_reset_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset")
                }

                Button(
                    onClick = {
                        val updated = textBox.copy(
                            text = textContent,
                            fontId = fontId,
                            fontSizeSp = fontSize,
                            textColor = textColor,
                            hasStroke = hasStroke,
                            strokeColor = strokeColor,
                            strokeWidth = strokeWidth,
                            hasShadow = hasShadow,
                            shadowColor = shadowColor,
                            shadowRadius = shadowRadius,
                            shadowDx = shadowDx,
                            shadowDy = shadowDy,
                            hasBackground = hasBackground,
                            backgroundColor = backgroundColor,
                            isBold = isBold,
                            isItalic = isItalic,
                            isAllCaps = isAllCaps,
                            textAlign = textAlign,
                            rotation = rotation
                        )
                        onApply(updated)
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f).testTag("text_overlay_done_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply Style")
                }
            }
        }
    }
}
