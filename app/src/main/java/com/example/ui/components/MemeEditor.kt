package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FlipToBack
import androidx.compose.material.icons.filled.FlipToFront
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
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
import com.example.util.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.roundToInt

/**
 * Supported typography fonts for Meme captions including classic Impact,
 * modern Poppins, handwritten Kalam (Hindi/Latin), Rozha One, Anton, and Pacifico.
 */
enum class MemeFontType(
    val label: String,
    val fontFamily: FontFamily,
    val defaultWeight: FontWeight = FontWeight.Bold,
    val fontResId: Int? = null
) {
    ANTON("Anton (Meme)", AntonFontFamily, FontWeight.Bold, R.font.anton),
    PACIFICO("Pacifico (Script)", PacificoFontFamily, FontWeight.Normal, R.font.pacifico),
    IMPACT("Impact", FontFamily.Default, FontWeight.Black, null),
    POPPINS("Poppins", PoppinsFontFamily, FontWeight.Bold, R.font.poppins),
    KALAM("Kalam (Desi)", KalamFontFamily, FontWeight.Normal, R.font.kalam),
    ROZHA("Rozha (Hindi)", RozhaOneFontFamily, FontWeight.Normal, R.font.rozha_one),
    SANS_SERIF("Modern Sans", FontFamily.SansSerif, FontWeight.Bold, null),
    SERIF("Classic Serif", FontFamily.Serif, FontWeight.Bold, null),
    MONOSPACE("Monospace", FontFamily.Monospace, FontWeight.Bold, null),
    CURSIVE("Cursive", FontFamily.Cursive, FontWeight.Normal, null)
}

/**
 * State model representing a single meme text object on the canvas.
 */
data class MemeTextObject(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "CAPTION TEXT",
    val xPercent: Float = 0.5f,
    val yPercent: Float = 0.5f,
    val fontSizeSp: Float = 32f,
    val fontType: MemeFontType = MemeFontType.ANTON,
    val textColor: Color = Color.White,
    val hasOutline: Boolean = true,
    val outlineColor: Color = Color.Black,
    val outlineWidth: Float = 3.5f,
    val hasShadow: Boolean = true,
    val shadowColor: Color = Color(0xAA000000),
    val shadowRadius: Float = 6f,
    val shadowDx: Float = 3f,
    val shadowDy: Float = 3f,
    val isBold: Boolean = true,
    val isItalic: Boolean = false,
    val isAllCaps: Boolean = true,
    val textAlign: TextAlign = TextAlign.Center,
    val rotation: Float = 0f,
    val backgroundColor: Color = Color.Transparent,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false
)

/**
 * State holder for MemeEditor supporting multiple text objects and active selection.
 */
@Stable
class MemeEditorState(
    initialBitmap: Bitmap? = null,
    initialTexts: List<MemeTextObject> = emptyList()
) {
    var imageBitmap by mutableStateOf<Bitmap?>(initialBitmap)
    val textObjects: SnapshotStateList<MemeTextObject> = mutableStateListOf<MemeTextObject>().apply {
        addAll(initialTexts)
    }

    var selectedTextId by mutableStateOf<String?>(null)

    val selectedText: MemeTextObject?
        get() = textObjects.firstOrNull { it.id == selectedTextId }

    fun addText(
        text: String = "NEW CAPTION",
        xPercent: Float = 0.5f,
        yPercent: Float = 0.5f,
        fontSizeSp: Float = 32f,
        fontType: MemeFontType = MemeFontType.IMPACT,
        textColor: Color = Color.White,
        hasOutline: Boolean = true,
        outlineColor: Color = Color.Black,
        outlineWidth: Float = 3.5f,
        isBold: Boolean = true,
        isAllCaps: Boolean = true
    ): MemeTextObject {
        val newObj = MemeTextObject(
            text = text,
            xPercent = xPercent,
            yPercent = yPercent,
            fontSizeSp = fontSizeSp,
            fontType = fontType,
            textColor = textColor,
            hasOutline = hasOutline,
            outlineColor = outlineColor,
            outlineWidth = outlineWidth,
            isBold = isBold,
            isAllCaps = isAllCaps
        )
        textObjects.add(newObj)
        selectedTextId = newObj.id
        return newObj
    }

    fun updateText(id: String, transform: (MemeTextObject) -> MemeTextObject) {
        val index = textObjects.indexOfFirst { it.id == id }
        if (index != -1) {
            textObjects[index] = transform(textObjects[index])
        }
    }

    fun removeText(id: String) {
        textObjects.removeAll { it.id == id }
        if (selectedTextId == id) {
            selectedTextId = null
        }
    }

    fun selectText(id: String?) {
        selectedTextId = id
    }

    fun clearSelection() {
        selectedTextId = null
    }

    fun duplicateText(id: String) {
        val original = textObjects.firstOrNull { it.id == id } ?: return
        val duplicate = original.copy(
            id = UUID.randomUUID().toString(),
            xPercent = (original.xPercent + 0.05f).coerceIn(0.1f, 0.9f),
            yPercent = (original.yPercent + 0.05f).coerceIn(0.1f, 0.9f)
        )
        textObjects.add(duplicate)
        selectedTextId = duplicate.id
    }

    fun bringToFront(id: String) {
        val index = textObjects.indexOfFirst { it.id == id }
        if (index != -1 && index < textObjects.size - 1) {
            val item = textObjects.removeAt(index)
            textObjects.add(item)
        }
    }

    fun sendToBack(id: String) {
        val index = textObjects.indexOfFirst { it.id == id }
        if (index > 0) {
            val item = textObjects.removeAt(index)
            textObjects.add(0, item)
        }
    }

    fun moveUp(id: String) {
        val index = textObjects.indexOfFirst { it.id == id }
        if (index != -1 && index < textObjects.size - 1) {
            val item = textObjects.removeAt(index)
            textObjects.add(index + 1, item)
        }
    }

    fun moveDown(id: String) {
        val index = textObjects.indexOfFirst { it.id == id }
        if (index > 0) {
            val item = textObjects.removeAt(index)
            textObjects.add(index - 1, item)
        }
    }

    fun setFont(id: String, font: MemeFontType) {
        updateText(id) { it.copy(fontType = font) }
    }

    fun setTextColor(id: String, color: Color) {
        updateText(id) { it.copy(textColor = color) }
    }

    fun setOutline(id: String, hasOutline: Boolean, outlineColor: Color = Color.Black, outlineWidth: Float = 3.5f) {
        updateText(id) {
            it.copy(
                hasOutline = hasOutline,
                outlineColor = outlineColor,
                outlineWidth = outlineWidth
            )
        }
    }

    fun setFontSize(id: String, sizeSp: Float) {
        updateText(id) { it.copy(fontSizeSp = sizeSp.coerceIn(12f, 96f)) }
    }

    fun setRotation(id: String, degrees: Float) {
        updateText(id) { it.copy(rotation = degrees.coerceIn(-180f, 180f)) }
    }

    fun setTextAlign(id: String, align: TextAlign) {
        updateText(id) { it.copy(textAlign = align) }
    }

    fun toggleLock(id: String) {
        updateText(id) { it.copy(isLocked = !it.isLocked) }
    }

    fun toggleVisibility(id: String) {
        updateText(id) { it.copy(isVisible = !it.isVisible) }
    }

    /**
     * Renders the base image and all visible text overlays onto a high-resolution Bitmap.
     */
    fun renderToBitmap(
        context: Context? = null,
        targetWidth: Int = 1080,
        targetHeight: Int = 1080
    ): Bitmap {
        val outputBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        // Draw base image or fallback background
        val baseBmp = imageBitmap
        if (baseBmp != null) {
            val srcRect = Rect(0, 0, baseBmp.width, baseBmp.height)
            val scale = minOf(
                targetWidth.toFloat() / baseBmp.width,
                targetHeight.toFloat() / baseBmp.height
            )
            val scaledW = baseBmp.width * scale
            val scaledH = baseBmp.height * scale
            val left = (targetWidth - scaledW) / 2f
            val top = (targetHeight - scaledH) / 2f
            val dstRect = android.graphics.RectF(left, top, left + scaledW, top + scaledH)
            canvas.drawBitmap(baseBmp, srcRect, dstRect, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        } else {
            // Gradient dark slate fallback canvas
            val bgPaint = Paint().apply { color = android.graphics.Color.DKGRAY }
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), bgPaint)
        }

        // Draw each text overlay in layer order
        for (item in textObjects) {
            if (!item.isVisible || item.text.isEmpty()) continue

            val textToDraw = if (item.isAllCaps) item.text.uppercase() else item.text
            val x = item.xPercent * targetWidth
            val y = item.yPercent * targetHeight

            // Scale font size proportionally to export width (reference 360dp)
            val scaledTextSize = targetWidth * (item.fontSizeSp / 360f)

            // Resolve Typeface
            val typeface: Typeface = when {
                context != null && item.fontType.fontResId != null -> {
                    try {
                        ResourcesCompat.getFont(context, item.fontType.fontResId) ?: Typeface.DEFAULT_BOLD
                    } catch (e: Exception) {
                        Typeface.DEFAULT_BOLD
                    }
                }
                item.fontType == MemeFontType.SERIF -> Typeface.SERIF
                item.fontType == MemeFontType.MONOSPACE -> Typeface.MONOSPACE
                item.fontType == MemeFontType.CURSIVE -> Typeface.DEFAULT
                else -> Typeface.DEFAULT_BOLD
            }

            val style = when {
                item.isBold && item.isItalic -> Typeface.BOLD_ITALIC
                item.isBold -> Typeface.BOLD
                item.isItalic -> Typeface.ITALIC
                else -> Typeface.NORMAL
            }

            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = item.textColor.toArgb()
                textSize = scaledTextSize
                setTypeface(Typeface.create(typeface, style))
                textAlign = when (item.textAlign) {
                    TextAlign.Left, TextAlign.Start -> Paint.Align.LEFT
                    TextAlign.Right, TextAlign.End -> Paint.Align.RIGHT
                    else -> Paint.Align.CENTER
                }
            }

            canvas.save()
            canvas.rotate(item.rotation, x, y)

            // Outline Pass
            if (item.hasOutline) {
                val strokePaint = Paint(paint).apply {
                    this.style = Paint.Style.STROKE
                    strokeWidth = scaledTextSize * (item.outlineWidth / 30f)
                    color = item.outlineColor.toArgb()
                    strokeJoin = Paint.Join.ROUND
                    strokeCap = Paint.Cap.ROUND
                }
                canvas.drawText(textToDraw, x, y, strokePaint)
            }

            // Text Fill Pass (with drop shadow if enabled)
            if (item.hasShadow) {
                paint.setShadowLayer(
                    item.shadowRadius,
                    item.shadowDx,
                    item.shadowDy,
                    item.shadowColor.toArgb()
                )
            }
            canvas.drawText(textToDraw, x, y, paint)
            canvas.restore()
        }

        return outputBitmap
    }

    /**
     * Renders and exports the current composition directly to the device's public gallery
     * using Android MediaStore API. Supports high-quality JPEG or lossless PNG format.
     */
    suspend fun exportToGallery(
        context: Context,
        title: String = "Meme_${System.currentTimeMillis()}",
        isPng: Boolean = false,
        targetWidth: Int = 1080,
        targetHeight: Int = 1080
    ): Uri? {
        val renderedBmp = renderToBitmap(context, targetWidth, targetHeight)
        return BitmapUtils.saveBitmapToGallery(
            context = context,
            bitmap = renderedBmp,
            title = title,
            isPng = isPng
        )
    }
}

/**
 * Remember helper for creating MemeEditorState.
 */
@Composable
fun rememberMemeEditorState(
    initialBitmap: Bitmap? = null,
    initialTexts: List<MemeTextObject> = emptyList()
): MemeEditorState {
    return remember(initialBitmap) {
        MemeEditorState(initialBitmap, initialTexts)
    }
}

/**
 * Convert legacy MemeTextBox to state-based MemeTextObject.
 */
fun MemeTextBox.toMemeTextObject(): MemeTextObject {
    return MemeTextObject(
        id = this.id,
        text = this.text,
        xPercent = this.xPercent,
        yPercent = this.yPercent,
        fontSizeSp = this.fontSizeSp,
        textColor = Color(this.textColor),
        hasOutline = true,
        outlineColor = Color(this.strokeColor),
        outlineWidth = 3.5f,
        isBold = this.isBold,
        isAllCaps = this.isAllCaps,
        textAlign = when (this.textAlign) {
            "left" -> TextAlign.Left
            "right" -> TextAlign.Right
            else -> TextAlign.Center
        },
        isVisible = this.isVisible,
        isLocked = this.isLocked
    )
}

/**
 * Convert MemeTextObject back to legacy MemeTextBox.
 */
fun MemeTextObject.toMemeTextBox(): MemeTextBox {
    return MemeTextBox(
        id = this.id,
        text = this.text,
        xPercent = this.xPercent,
        yPercent = this.yPercent,
        fontSizeSp = this.fontSizeSp,
        textColor = this.textColor.toArgb().toLong() and 0xFFFFFFFFL,
        strokeColor = this.outlineColor.toArgb().toLong() and 0xFFFFFFFFL,
        isBold = this.isBold,
        isAllCaps = this.isAllCaps,
        textAlign = when (this.textAlign) {
            TextAlign.Left, TextAlign.Start -> "left"
            TextAlign.Right, TextAlign.End -> "right"
            else -> "center"
        },
        isVisible = this.isVisible,
        isLocked = this.isLocked
    )
}

/**
 * Full MemeEditor component featuring:
 * - State-based multi-text objects management
 * - Interactive direct-manipulation canvas (drag, pinch-scale, rotate)
 * - Complete Font controls (Impact, Poppins, Kalam, Rozha, Sans, Serif, Mono, Cursive)
 * - Color controls with quick preset palettes
 * - Outline controls (toggle, color picker, width slider)
 * - Layer management (Bring to Front, Send to Back, Duplicate, Lock, Delete)
 * - Public Gallery Export using Android MediaStore API (High-Quality JPEG 95% or Lossless PNG)
 * - Native Android Sharing integration
 */
@Composable
fun MemeEditor(
    state: MemeEditorState,
    modifier: Modifier = Modifier,
    title: String = "Meme Editor",
    showTopBar: Boolean = true,
    showControls: Boolean = true,
    onBack: (() -> Unit)? = null,
    onPickImage: (() -> Unit)? = null,
    onExportRequested: ((Bitmap) -> Unit)? = null,
    onSavedToGallery: ((Uri) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showExportDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    var exportedUri by remember { mutableStateOf<Uri?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var exportErrorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("meme_editor_root")
    ) {
        // Optional Top Bar with Title, quick Add, and prominent Export button
        if (showTopBar) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onBack != null) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.testTag("meme_editor_back_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                        Column(modifier = Modifier.padding(start = if (onBack != null) 4.dp else 12.dp)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${state.textObjects.size} text layer${if (state.textObjects.size == 1) "" else "s"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick Add Text
                        FilledTonalButton(
                            onClick = {
                                val y = when (state.textObjects.size) {
                                    0 -> 0.15f
                                    1 -> 0.85f
                                    else -> 0.50f
                                }
                                state.addText(
                                    text = if (state.textObjects.isEmpty()) "TOP TEXT" else "CAPTION",
                                    xPercent = 0.5f,
                                    yPercent = y
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("meme_editor_top_add_text_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontSize = 13.sp)
                        }

                        // Export Button
                        Button(
                            onClick = { showExportDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("meme_editor_export_btn")
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Upper Canvas Viewport Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF121212))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            MemeEditorCanvas(
                state = state,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Lower Inspector / Controls Panel
        if (showControls) {
            MemeEditorControlsPanel(
                state = state,
                onPickImage = onPickImage,
                onExport = { showExportDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
            )
        }
    }

    // Export Dialog (JPEG / PNG format selection & MediaStore save)
    if (showExportDialog) {
        ExportMemeDialog(
            isExporting = isExporting,
            onDismiss = { if (!isExporting) showExportDialog = false },
            onConfirmExport = { isPng, customTitle ->
                coroutineScope.launch {
                    isExporting = true
                    exportErrorMessage = null
                    try {
                        val renderedBmp = state.renderToBitmap(context, targetWidth = 1080, targetHeight = 1080)
                        val fileName = if (customTitle.isBlank()) "Meme_${System.currentTimeMillis()}" else customTitle.trim()
                        val uri = BitmapUtils.saveBitmapToGallery(
                            context = context,
                            bitmap = renderedBmp,
                            title = fileName,
                            isPng = isPng
                        )
                        isExporting = false
                        showExportDialog = false

                        if (uri != null) {
                            exportedUri = uri
                            showSuccessDialog = true
                            onSavedToGallery?.invoke(uri)
                            onExportRequested?.invoke(renderedBmp)
                        } else {
                            exportErrorMessage = "Unable to save image to MediaStore gallery."
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        isExporting = false
                        exportErrorMessage = "Export failed: ${e.message}"
                    }
                }
            }
        )
    }

    // Success Dialog with Share & Open affordance
    if (showSuccessDialog && exportedUri != null) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Saved to Gallery!",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Your meme composition has been saved in high quality to your device's public gallery (Pictures/PhotoMemesMaker) via Android MediaStore.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = exportedUri.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = if (exportedUri.toString().endsWith(".png")) "image/png" else "image/jpeg"
                                putExtra(Intent.EXTRA_STREAM, exportedUri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Meme"))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.testTag("meme_editor_share_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSuccessDialog = false },
                    modifier = Modifier.testTag("meme_editor_dismiss_success_btn")
                ) {
                    Text("Done")
                }
            }
        )
    }

    // Error Dialog if failed
    if (exportErrorMessage != null) {
        AlertDialog(
            onDismissRequest = { exportErrorMessage = null },
            icon = {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Export Error") },
            text = { Text(exportErrorMessage ?: "Unknown error") },
            confirmButton = {
                Button(onClick = { exportErrorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}

/**
 * Dialog allowing users to configure and trigger export of the current meme composition
 * to the public device gallery via the MediaStore API as high-quality JPEG or lossless PNG.
 */
@Composable
fun ExportMemeDialog(
    isExporting: Boolean,
    onDismiss: () -> Unit,
    onConfirmExport: (isPng: Boolean, customTitle: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPng by remember { mutableStateOf(false) } // Default JPEG (95% high quality)
    var titleText by remember { mutableStateOf("Meme_${System.currentTimeMillis()}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("export_meme_dialog"),
        icon = {
            Icon(
                imageVector = Icons.Default.FileDownload,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Export Meme to Gallery",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Select output format to save into the device's public gallery (MediaStore):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Format Options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // JPEG Option
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isPng) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPng = false }
                            .testTag("meme_format_jpeg_radio")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !isPng,
                                onClick = { isPng = false }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "High-Quality JPEG (.jpg)",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text(
                                            text = "95% Q",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Best for Instagram, WhatsApp, Reddit. Smaller file size with vibrant color detail.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // PNG Option
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isPng) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPng = true }
                            .testTag("meme_format_png_radio")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isPng,
                                onClick = { isPng = true }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Lossless PNG (.png)",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.secondary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary
                                    ) {
                                        Text(
                                            text = "Lossless",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Maximum pixel sharpness, text outline clarity, and transparent channel preservation.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // File title input
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Meme File Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("meme_export_filename_input")
                )

                // Output specs badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Resolution: 1080 × 1080 px (FHD)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isPng) "Format: PNG" else "Format: JPEG (95%)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isExporting) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Saving to MediaStore gallery...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmExport(isPng, titleText) },
                enabled = !isExporting,
                modifier = Modifier.testTag("meme_editor_confirm_export_btn")
            ) {
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Exporting...")
                } else {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save to Gallery")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isExporting,
                modifier = Modifier.testTag("meme_editor_cancel_export_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Interactive canvas overlay rendering the image and all draggable text overlays.
 */
@Composable
fun MemeEditorCanvas(
    state: MemeEditorState,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E))
            .pointerInput(Unit) {
                detectTapGestures {
                    // Tap on canvas background deselects active text box
                    state.clearSelection()
                }
            }
            .testTag("meme_editor_canvas"),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val canvasWidthPx = constraints.maxWidth.toFloat()
        val canvasHeightPx = constraints.maxHeight.toFloat()

        val imgBmp = state.imageBitmap
        val hasImage = imgBmp != null

        val imageWidthPx: Float
        val imageHeightPx: Float
        val imageLeftPx: Float
        val imageTopPx: Float

        if (hasImage && imgBmp != null) {
            val bmpImage = remember(imgBmp) { imgBmp.asImageBitmap() }
            val scale = minOf(
                canvasWidthPx / bmpImage.width.toFloat(),
                canvasHeightPx / bmpImage.height.toFloat()
            )
            imageWidthPx = bmpImage.width * scale
            imageHeightPx = bmpImage.height * scale
            imageLeftPx = (canvasWidthPx - imageWidthPx) / 2f
            imageTopPx = (canvasHeightPx - imageHeightPx) / 2f

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawImage(
                    image = bmpImage,
                    dstOffset = IntOffset(imageLeftPx.toInt(), imageTopPx.toInt()),
                    dstSize = IntSize(imageWidthPx.toInt(), imageHeightPx.toInt())
                )
            }
        } else {
            // Default blank aspect canvas 1:1
            val minDim = minOf(canvasWidthPx, canvasHeightPx) * 0.95f
            imageWidthPx = minDim
            imageHeightPx = minDim
            imageLeftPx = (canvasWidthPx - imageWidthPx) / 2f
            imageTopPx = (canvasHeightPx - imageHeightPx) / 2f

            Box(
                modifier = Modifier
                    .size((imageWidthPx / density.density).dp, (imageHeightPx / density.density).dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2A2A38))
                    .border(1.dp, Color(0xFF444455), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No image selected • Tap Gallery to upload",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        // Render each text item overlay in current list z-order
        for (item in state.textObjects) {
            if (!item.isVisible) continue
            val isSelected = item.id == state.selectedTextId

            MemeTextOverlayItem(
                textItem = item,
                isSelected = isSelected,
                imageLeftPx = imageLeftPx,
                imageTopPx = imageTopPx,
                imageWidthPx = imageWidthPx,
                imageHeightPx = imageHeightPx,
                onSelect = { state.selectText(item.id) },
                onPositionChange = { newXPercent, newYPercent ->
                    state.updateText(item.id) {
                        it.copy(
                            xPercent = newXPercent.coerceIn(0.05f, 0.95f),
                            yPercent = newYPercent.coerceIn(0.05f, 0.95f)
                        )
                    }
                },
                onSizeChange = { newSize ->
                    state.setFontSize(item.id, newSize)
                },
                onRotateChange = { deltaDegrees ->
                    state.setRotation(item.id, item.rotation + deltaDegrees)
                },
                onDelete = { state.removeText(item.id) },
                onToggleLock = { state.toggleLock(item.id) }
            )
        }
    }
}

/**
 * Individual movable, resizable, and rotatable text item on the MemeEditor canvas.
 */
@Composable
private fun MemeTextOverlayItem(
    textItem: MemeTextObject,
    isSelected: Boolean,
    imageLeftPx: Float,
    imageTopPx: Float,
    imageWidthPx: Float,
    imageHeightPx: Float,
    onSelect: () -> Unit,
    onPositionChange: (Float, Float) -> Unit,
    onSizeChange: (Float) -> Unit,
    onRotateChange: (Float) -> Unit,
    onDelete: () -> Unit,
    onToggleLock: () -> Unit
) {
    val density = LocalDensity.current
    val currentXPx = imageLeftPx + (textItem.xPercent * imageWidthPx)
    val currentYPx = imageTopPx + (textItem.yPercent * imageHeightPx)

    val dragModifier = if (!textItem.isLocked) {
        Modifier.pointerInput(textItem.id, imageWidthPx, imageHeightPx) {
            detectDragGestures(
                onDragStart = { onSelect() },
                onDrag = { change, dragAmount ->
                    change.consume()
                    val deltaXPercent = dragAmount.x / imageWidthPx
                    val deltaYPercent = dragAmount.y / imageHeightPx
                    onPositionChange(
                        textItem.xPercent + deltaXPercent,
                        textItem.yPercent + deltaYPercent
                    )
                }
            )
        }
    } else Modifier

    val transformModifier = if (!textItem.isLocked) {
        Modifier.pointerInput(textItem.id) {
            detectTransformGestures { _, _, zoom, rotationDelta ->
                if (zoom != 1f) {
                    onSizeChange(textItem.fontSizeSp * zoom)
                }
                if (rotationDelta != 0f) {
                    onRotateChange(rotationDelta)
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
            .width((imageWidthPx * 0.9f / density.density).dp)
            .rotate(textItem.rotation)
            .then(dragModifier)
            .then(transformModifier)
            .pointerInput(textItem.id) {
                detectTapGestures(onTap = { onSelect() })
            }
            .testTag("meme_text_item_${textItem.id}"),
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
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    } else {
                        Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            val textToRender = if (textItem.isAllCaps) textItem.text.uppercase() else textItem.text
            val strokeWidthDp = textItem.outlineWidth.dp

            Box(contentAlignment = Alignment.Center) {
                // Outline Rendering passes (8 directions for clean, dense stroke)
                if (textItem.hasOutline && textItem.outlineWidth > 0f) {
                    val outlineColor = textItem.outlineColor
                    val offsets = listOf(
                        Pair(strokeWidthDp, 0.dp),
                        Pair(-strokeWidthDp, 0.dp),
                        Pair(0.dp, strokeWidthDp),
                        Pair(0.dp, -strokeWidthDp),
                        Pair(strokeWidthDp * 0.7f, strokeWidthDp * 0.7f),
                        Pair(-strokeWidthDp * 0.7f, strokeWidthDp * 0.7f),
                        Pair(strokeWidthDp * 0.7f, -strokeWidthDp * 0.7f),
                        Pair(-strokeWidthDp * 0.7f, -strokeWidthDp * 0.7f)
                    )

                    for ((dx, dy) in offsets) {
                        Text(
                            text = textToRender,
                            fontFamily = textItem.fontType.fontFamily,
                            fontSize = textItem.fontSizeSp.sp,
                            fontWeight = if (textItem.isBold) FontWeight.Black else textItem.fontType.defaultWeight,
                            fontStyle = if (textItem.isItalic) FontStyle.Italic else FontStyle.Normal,
                            textAlign = textItem.textAlign,
                            color = outlineColor,
                            modifier = Modifier.offset(dx, dy)
                        )
                    }
                }

                // Core Fill Text Pass
                Text(
                    text = textToRender,
                    fontFamily = textItem.fontType.fontFamily,
                    fontSize = textItem.fontSizeSp.sp,
                    fontWeight = if (textItem.isBold) FontWeight.Black else textItem.fontType.defaultWeight,
                    fontStyle = if (textItem.isItalic) FontStyle.Italic else FontStyle.Normal,
                    textAlign = textItem.textAlign,
                    color = textItem.textColor
                )
            }

            // Selection Controls overlay (Delete, Lock, Size handles)
            if (isSelected) {
                // Top-Left: Lock toggle button
                Surface(
                    shape = CircleShape,
                    color = if (textItem.isLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-10).dp, y = (-10).dp)
                        .size(28.dp)
                        .clickable(onClick = onToggleLock)
                        .testTag("btn_lock_text_${textItem.id}")
                ) {
                    Icon(
                        imageVector = if (textItem.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Lock",
                        tint = if (textItem.isLocked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(6.dp)
                    )
                }

                // Top-Right: Delete button
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(28.dp)
                        .clickable(onClick = onDelete)
                        .testTag("btn_delete_text_${textItem.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.padding(6.dp)
                    )
                }

                // Bottom-Right: Scale corner handle
                if (!textItem.isLocked) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 10.dp, y = 10.dp)
                            .size(28.dp)
                            .pointerInput(textItem.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val delta = (dragAmount.x + dragAmount.y) * 0.25f
                                    onSizeChange(textItem.fontSizeSp + delta)
                                }
                            }
                            .testTag("handle_scale_text_${textItem.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Scale",
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom controls panel providing comprehensive font, color, outline, and layout inspectors.
 */
@Composable
fun MemeEditorControlsPanel(
    state: MemeEditorState,
    onPickImage: (() -> Unit)? = null,
    onExport: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val selected = state.selectedText
    var activeTab by remember { mutableStateOf(0) } // 0: Text, 1: Font, 2: Colors & Outline, 3: Arrange

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (selected != null) {
                // Tab Header for selected text object
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Caption") },
                        icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_caption")
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Font") },
                        icon = { Icon(Icons.Default.FormatSize, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_font")
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("Outline & Color") },
                        icon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_color_outline")
                    )
                    Tab(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        text = { Text("Arrange") },
                        icon = { Icon(Icons.Default.FlipToFront, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_arrange")
                    )
                }

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    when (activeTab) {
                        0 -> TextCaptionTabContent(text = selected, state = state)
                        1 -> FontTabContent(text = selected, state = state)
                        2 -> ColorAndOutlineTabContent(text = selected, state = state)
                        3 -> ArrangeTabContent(text = selected, state = state)
                    }
                }
            } else {
                // Empty state or General Action Tray when no text item is active
                GeneralActionTray(
                    state = state,
                    onPickImage = onPickImage,
                    onExport = onExport,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Tab 0: Caption Text editing and common presets
 */
@Composable
private fun TextCaptionTabContent(
    text: MemeTextObject,
    state: MemeEditorState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = text.text,
            onValueChange = { newText ->
                state.updateText(text.id) { it.copy(text = newText) }
            },
            label = { Text("Caption Text") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_caption_text"),
            singleLine = false,
            maxLines = 3,
            trailingIcon = {
                if (text.text.isNotEmpty()) {
                    IconButton(onClick = { state.updateText(text.id) { it.copy(text = "") } }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            }
        )

        // Text Styles (All-Caps, Bold, Italic, Alignment)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = text.isAllCaps,
                onClick = { state.updateText(text.id) { it.copy(isAllCaps = !it.isAllCaps) } },
                label = { Text("ALL CAPS") },
                modifier = Modifier.testTag("chip_all_caps")
            )

            FilterChip(
                selected = text.isBold,
                onClick = { state.updateText(text.id) { it.copy(isBold = !it.isBold) } },
                label = { Text("Bold") },
                leadingIcon = { Icon(Icons.Default.FormatBold, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("chip_bold")
            )

            FilterChip(
                selected = text.isItalic,
                onClick = { state.updateText(text.id) { it.copy(isItalic = !it.isItalic) } },
                label = { Text("Italic") },
                leadingIcon = { Icon(Icons.Default.FormatItalic, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("chip_italic")
            )
        }

        // Text Alignment Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Align:", style = MaterialTheme.typography.labelMedium)
            IconButton(
                onClick = { state.setTextAlign(text.id, TextAlign.Left) },
                modifier = Modifier.testTag("btn_align_left")
            ) {
                Icon(
                    Icons.Default.FormatAlignLeft,
                    contentDescription = "Left",
                    tint = if (text.textAlign == TextAlign.Left) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { state.setTextAlign(text.id, TextAlign.Center) },
                modifier = Modifier.testTag("btn_align_center")
            ) {
                Icon(
                    Icons.Default.FormatAlignCenter,
                    contentDescription = "Center",
                    tint = if (text.textAlign == TextAlign.Center) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { state.setTextAlign(text.id, TextAlign.Right) },
                modifier = Modifier.testTag("btn_align_right")
            ) {
                Icon(
                    Icons.Default.FormatAlignRight,
                    contentDescription = "Right",
                    tint = if (text.textAlign == TextAlign.Right) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Quick punchline suggestions (Hindi & English)
        Text("Quick Ideas:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val suggestions = listOf(
                "गजब बेइज्जती है!",
                "ये क्या बात हुई!",
                "वाह क्या सीन है!",
                "BRUH MOMENT",
                "NOBODY:",
                "ME IN 2026:",
                "EXPECTATION VS REALITY"
            )
            for (item in suggestions) {
                OutlinedButton(
                    onClick = { state.updateText(text.id) { it.copy(text = item) } },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(item, fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * Tab 1: Font Family selector and Font Size slider
 */
@Composable
private fun FontTabContent(
    text: MemeTextObject,
    state: MemeEditorState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Choose Typography Font", style = MaterialTheme.typography.labelMedium)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MemeFontType.values().forEach { fontType ->
                val isSelected = text.fontType == fontType
                Card(
                    modifier = Modifier
                        .width(108.dp)
                        .clickable { state.setFont(text.id, fontType) }
                        .testTag("font_card_${fontType.name}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (isSelected) CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ag",
                            fontFamily = fontType.fontFamily,
                            fontWeight = fontType.defaultWeight,
                            fontSize = 22.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = fontType.label,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Font Size Slider with direct controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Font Size: ${text.fontSizeSp.toInt()} sp", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = { state.setFontSize(text.id, text.fontSizeSp - 4f) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("-4")
                }
                OutlinedButton(
                    onClick = { state.setFontSize(text.id, text.fontSizeSp + 4f) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("+4")
                }
            }
        }

        Slider(
            value = text.fontSizeSp,
            onValueChange = { state.setFontSize(text.id, it) },
            valueRange = 14f..80f,
            modifier = Modifier.testTag("slider_font_size")
        )
    }
}

/**
 * Tab 2: Color picker and Outline / Stroke controls
 */
@Composable
private fun ColorAndOutlineTabContent(
    text: MemeTextObject,
    state: MemeEditorState
) {
    val presetTextColors = listOf(
        Color(0xFFFFFFFF) to "White",
        Color(0xFFFFEB3B) to "Yellow",
        Color(0xFF00E676) to "Green",
        Color(0xFF00E5FF) to "Cyan",
        Color(0xFFFF5252) to "Red",
        Color(0xFFFF9100) to "Orange",
        Color(0xFFF50057) to "Pink",
        Color(0xFFE040FB) to "Purple",
        Color(0xFF000000) to "Black",
        Color(0xFFB0BEC5) to "Silver"
    )

    val presetOutlineColors = listOf(
        Color(0xFF000000) to "Black",
        Color(0xFFFFFFFF) to "White",
        Color(0xFF1A237E) to "Navy",
        Color(0xFFB71C1C) to "Crimson",
        Color(0xFFFF6F00) to "Amber",
        Color(0xFF263238) to "Charcoal"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Text Color Header
        Text("Text Color", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presetTextColors.forEach { (color, name) ->
                val isSelected = text.textColor == color
                Surface(
                    shape = CircleShape,
                    color = color,
                    border = CardDefaults.outlinedCardBorder().copy(
                        width = if (isSelected) 3.dp else 1.dp
                    ),
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { state.setTextColor(text.id, color) }
                        .testTag("color_text_${name.lowercase()}")
                ) {
                    if (isSelected) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Selected",
                                tint = if (color == Color.White || color == Color(0xFFFFEB3B)) Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Outline Stroke Header & Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Text Outline (Meme Stroke)", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = if (text.hasOutline) "High readability contrast enabled" else "Outline disabled",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = text.hasOutline,
                onCheckedChange = { checked ->
                    state.setOutline(text.id, hasOutline = checked, text.outlineColor, text.outlineWidth)
                },
                modifier = Modifier.testTag("switch_outline_toggle")
            )
        }

        if (text.hasOutline) {
            // Outline Color Presets
            Text("Outline Color", style = MaterialTheme.typography.labelSmall)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetOutlineColors.forEach { (color, name) ->
                    val isSelected = text.outlineColor == color
                    Surface(
                        shape = CircleShape,
                        color = color,
                        border = CardDefaults.outlinedCardBorder().copy(
                            width = if (isSelected) 3.dp else 1.dp
                        ),
                        modifier = Modifier
                            .size(34.dp)
                            .clickable {
                                state.setOutline(text.id, true, outlineColor = color, text.outlineWidth)
                            }
                            .testTag("color_outline_${name.lowercase()}")
                    ) {
                        if (isSelected) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Selected",
                                    tint = if (color == Color.White) Color.Black else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Outline Width / Thickness Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Outline Thickness: ${text.outlineWidth.toInt()} dp", style = MaterialTheme.typography.labelSmall)
            }

            Slider(
                value = text.outlineWidth,
                onValueChange = { newWidth ->
                    state.setOutline(text.id, true, text.outlineColor, newWidth)
                },
                valueRange = 1.0f..8.0f,
                modifier = Modifier.testTag("slider_outline_thickness")
            )
        }
    }
}

/**
 * Tab 3: Arrange, Rotation, and Layer actions
 */
@Composable
private fun ArrangeTabContent(
    text: MemeTextObject,
    state: MemeEditorState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Rotation Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Rotation: ${text.rotation.toInt()}°", style = MaterialTheme.typography.labelMedium)
            if (text.rotation != 0f) {
                OutlinedButton(
                    onClick = { state.setRotation(text.id, 0f) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset 0°")
                }
            }
        }

        Slider(
            value = text.rotation,
            onValueChange = { state.setRotation(text.id, it) },
            valueRange = -180f..180f,
            modifier = Modifier.testTag("slider_rotation")
        )

        // Layer ordering buttons
        Text("Layer Ordering", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { state.bringToFront(text.id) },
                modifier = Modifier.weight(1f).testTag("btn_bring_front")
            ) {
                Icon(Icons.Default.FlipToFront, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bring Front", fontSize = 12.sp)
            }
            Button(
                onClick = { state.sendToBack(text.id) },
                modifier = Modifier.weight(1f).testTag("btn_send_back")
            ) {
                Icon(Icons.Default.FlipToBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Send Back", fontSize = 12.sp)
            }
        }

        // Secondary operations (Duplicate, Lock, Delete)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { state.duplicateText(text.id) },
                modifier = Modifier.weight(1f).testTag("btn_duplicate_text")
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Duplicate", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { state.toggleLock(text.id) },
                modifier = Modifier.weight(1f).testTag("btn_lock_toggle")
            ) {
                Icon(
                    imageVector = if (text.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (text.isLocked) "Unlock" else "Lock", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { state.removeText(text.id) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f).testTag("btn_delete_active_text")
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete", fontSize = 12.sp)
            }
        }
    }
}

/**
 * General action tray displayed when no text object is actively selected.
 */
@Composable
private fun GeneralActionTray(
    state: MemeEditorState,
    onPickImage: (() -> Unit)?,
    onExport: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Meme Text Layers (${state.textObjects.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap any text on canvas or button below to customize",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (onExport != null) {
                Button(
                    onClick = onExport,
                    modifier = Modifier.testTag("meme_editor_export_btn"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Export")
                }
            }
        }

        // Prominent Add Text Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val y = when (state.textObjects.size) {
                        0 -> 0.15f // Top text
                        1 -> 0.85f // Bottom text
                        else -> 0.50f // Middle text
                    }
                    state.addText(
                        text = if (state.textObjects.isEmpty()) "TOP TEXT" else "BOTTOM TEXT",
                        xPercent = 0.5f,
                        yPercent = y
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("meme_editor_add_text_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Text Box", fontWeight = FontWeight.Bold)
            }

            if (onPickImage != null) {
                OutlinedButton(
                    onClick = onPickImage,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("meme_editor_pick_image_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Change Photo")
                }
            }
        }

        // Horizontal List of Existing Text Layers for quick selection
        if (state.textObjects.isNotEmpty()) {
            Text("Select Text Layer:", style = MaterialTheme.typography.labelSmall)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.textObjects.forEachIndexed { index, item ->
                    Card(
                        modifier = Modifier
                            .clickable { state.selectText(item.id) }
                            .testTag("layer_chip_${item.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${index + 1}: ${item.text.ifEmpty { "Empty" }.take(14)}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
