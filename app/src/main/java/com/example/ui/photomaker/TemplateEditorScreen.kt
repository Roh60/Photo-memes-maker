package com.example.ui.photomaker

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhotoTemplate
import com.example.data.model.TemplateRepository
import com.example.data.repository.CreationsRepository
import com.example.ui.components.PaletteColorPicker
import com.example.ui.components.StudioTopBar
import com.example.ui.components.ValueSliderRow
import com.example.util.BitmapUtils
import com.example.util.SampleImages
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateEditorScreen(
    templateId: String,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { CreationsRepository(context) }

    val template = remember(templateId) {
        TemplateRepository.getTemplateById(templateId) ?: TemplateRepository.FESTIVAL_TEMPLATES.first()
    }

    // Editable text contents
    var headerTitle by remember { mutableStateOf(template.headerTitle) }
    var greetingText by remember { mutableStateOf(template.defaultGreeting) }
    var footerQuote by remember { mutableStateOf(template.footerQuote) }

    // User photo bitmap
    var userPhoto by remember {
        mutableStateOf<Bitmap?>(SampleImages.createPortraitSample(600, 600, "My Photo"))
    }
    var isProcessing by remember { mutableStateOf(false) }

    // Frame configuration
    var frameShape by remember { mutableStateOf(template.frameShape) }
    var borderWidth by remember { mutableFloatStateOf(template.frameBorderWidthDp) }
    var borderColor by remember { mutableStateOf(template.frameBorderColor) }
    var gradientStart by remember { mutableStateOf(template.gradientColors.first()) }
    var gradientEnd by remember { mutableStateOf(template.gradientColors.last()) }

    // Placed Stickers
    val extraStickers = remember { mutableStateListOf<String>() }

    // Active bottom edit section: "text", "frame", "colors", "stickers"
    var editSection by remember { mutableStateOf("text") }

    // Media pickers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isProcessing = true
                val bmp = BitmapUtils.decodeUriToBitmap(context, uri)
                if (bmp != null) userPhoto = bmp
                isProcessing = false
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) userPhoto = bmp
    }

    // Render complete design onto high quality export bitmap
    fun generateExportBitmap(): Bitmap {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // Draw gradient background
        val bgPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            shader = android.graphics.LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                intArrayOf(gradientStart.toInt(), gradientEnd.toInt()),
                null,
                android.graphics.Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Draw corner sparkles
        val decorPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 54f
            textAlign = android.graphics.Paint.Align.CENTER
        }
        canvas.drawText(template.decorativeEmoji, 80f, 90f, decorPaint)
        canvas.drawText(template.cornerDecorEmoji, width - 80f, 90f, decorPaint)

        // Draw Header Title
        val titlePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 52f
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setShadowLayer(14f, 0f, 4f, android.graphics.Color.BLACK)
        }
        canvas.drawText(headerTitle, width * 0.5f, 130f, titlePaint)

        // Draw User Photo in frame
        val photo = userPhoto
        if (photo != null) {
            val frameSize = width * 0.58f
            val cx = width * 0.5f
            val cy = height * 0.44f
            val left = cx - frameSize / 2f
            val top = cy - frameSize / 2f
            val rect = android.graphics.RectF(left, top, left + frameSize, top + frameSize)

            val framePath = android.graphics.Path()
            if (frameShape == "circle") {
                framePath.addCircle(cx, cy, frameSize / 2f, android.graphics.Path.Direction.CW)
            } else {
                framePath.addRoundRect(rect, 48f, 48f, android.graphics.Path.Direction.CW)
            }

            canvas.save()
            canvas.clipPath(framePath)
            // Scale and draw photo
            val srcRect = android.graphics.Rect(0, 0, photo.width, photo.height)
            canvas.drawBitmap(photo, srcRect, rect, null)
            canvas.restore()

            // Draw border
            val borderPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = borderColor.toInt()
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = borderWidth * 3.5f
            }
            if (frameShape == "circle") {
                canvas.drawCircle(cx, cy, frameSize / 2f, borderPaint)
            } else {
                canvas.drawRoundRect(rect, 48f, 48f, borderPaint)
            }
        }

        // Draw Greeting
        val greetingPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 38f
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setShadowLayer(10f, 0f, 3f, android.graphics.Color.BLACK)
        }
        canvas.drawText(greetingText, width * 0.5f, height * 0.82f, greetingPaint)

        // Draw Footer Quote
        val footerPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb(220, 255, 255, 255)
            textSize = 32f
            textAlign = android.graphics.Paint.Align.CENTER
        }
        canvas.drawText(footerQuote, width * 0.5f, height * 0.90f, footerPaint)

        return bitmap
    }

    Scaffold(
        topBar = {
            StudioTopBar(
                title = template.name,
                onBack = onNavigateBack,
                actions = {
                    // Share
                    IconButton(
                        onClick = {
                            val bmp = generateExportBitmap()
                            BitmapUtils.shareBitmap(context, bmp, "$headerTitle - Created with Photo & Memes Maker")
                        },
                        modifier = Modifier.testTag("template_share_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }

                    // Save
                    IconButton(
                        onClick = {
                            scope.launch {
                                isProcessing = true
                                val bmp = generateExportBitmap()
                                val uri = BitmapUtils.saveBitmapToGallery(context, bmp, "Template_${template.subCategory}")
                                repository.saveCreation(bmp, template.name, "festival")
                                isProcessing = false
                                Toast.makeText(context, "Design Saved Successfully!", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.testTag("template_save_btn")
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
            // Live Preview Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val canvasSide = minOf(constraints.maxWidth, constraints.maxHeight)

                    Canvas(
                        modifier = Modifier
                            .size((canvasSide / LocalContext.current.resources.displayMetrics.density).dp)
                            .testTag("template_preview_canvas")
                    ) {
                        // Background gradient
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(gradientStart), Color(gradientEnd))
                            )
                        )

                        // Draw Corner Emojis & Header Text
                        drawIntoCanvas { nativeCanvas ->
                            val decorPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                textSize = size.width * 0.08f
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            nativeCanvas.nativeCanvas.drawText(template.decorativeEmoji, size.width * 0.1f, size.height * 0.11f, decorPaint)
                            nativeCanvas.nativeCanvas.drawText(template.cornerDecorEmoji, size.width * 0.9f, size.height * 0.11f, decorPaint)

                            // Title
                            val titlePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                color = android.graphics.Color.WHITE
                                textSize = size.width * 0.052f
                                textAlign = android.graphics.Paint.Align.CENTER
                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                setShadowLayer(10f, 0f, 3f, android.graphics.Color.BLACK)
                            }
                            nativeCanvas.nativeCanvas.drawText(headerTitle, size.width * 0.5f, size.height * 0.14f, titlePaint)
                        }

                        // Photo slot in center
                        val photoBmp = userPhoto
                        val frameSide = size.width * 0.58f
                        val cx = size.width * 0.5f
                        val cy = size.height * 0.45f
                        val left = cx - frameSide / 2f
                        val top = cy - frameSide / 2f

                        if (photoBmp != null) {
                            val imgBmp = photoBmp.asImageBitmap()
                            // Clip path
                            val path = Path().apply {
                                if (frameShape == "circle") {
                                    addOval(androidx.compose.ui.geometry.Rect(left, top, left + frameSide, top + frameSide))
                                } else {
                                    addRoundRect(androidx.compose.ui.geometry.RoundRect(
                                        left, top, left + frameSide, top + frameSide,
                                        CornerRadius(28f, 28f)
                                    ))
                                }
                            }

                            clipPath(path) {
                                drawImage(
                                    image = imgBmp,
                                    dstOffset = androidx.compose.ui.unit.IntOffset(left.toInt(), top.toInt()),
                                    dstSize = androidx.compose.ui.unit.IntSize(frameSide.toInt(), frameSide.toInt())
                                )
                            }

                            // Draw border
                            if (frameShape == "circle") {
                                drawCircle(
                                    color = Color(borderColor),
                                    radius = frameSide / 2f,
                                    center = Offset(cx, cy),
                                    style = Stroke(width = borderWidth * 2.5f)
                                )
                            } else {
                                drawRoundRect(
                                    color = Color(borderColor),
                                    topLeft = Offset(left, top),
                                    size = Size(frameSide, frameSide),
                                    cornerRadius = CornerRadius(28f, 28f),
                                    style = Stroke(width = borderWidth * 2.5f)
                                )
                            }
                        }

                        // Draw Greeting Text & Footer
                        drawIntoCanvas { nativeCanvas ->
                            val gPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                color = android.graphics.Color.WHITE
                                textSize = size.width * 0.040f
                                textAlign = android.graphics.Paint.Align.CENTER
                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                setShadowLayer(8f, 0f, 2f, android.graphics.Color.BLACK)
                            }
                            nativeCanvas.nativeCanvas.drawText(greetingText, size.width * 0.5f, size.height * 0.82f, gPaint)

                            val fPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                color = android.graphics.Color.argb(230, 255, 255, 255)
                                textSize = size.width * 0.034f
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            nativeCanvas.nativeCanvas.drawText(footerQuote, size.width * 0.5f, size.height * 0.90f, fPaint)
                        }
                    }

                    // Photo replace buttons overlay
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier.clickable { cameraLauncher.launch(null) }.testTag("tpl_cam_btn")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White, modifier = Modifier.padding(8.dp).size(20.dp))
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier.clickable {
                                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }.testTag("tpl_gallery_btn")
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.padding(8.dp).size(20.dp))
                        }
                    }
                }
            }

            // Edit Sections Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = editSection == "text",
                    onClick = { editSection = "text" },
                    label = { Text("Edit Text") },
                    leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = editSection == "frame",
                    onClick = { editSection = "frame" },
                    label = { Text("Frame") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = editSection == "colors",
                    onClick = { editSection = "colors" },
                    label = { Text("Colors") },
                    leadingIcon = { Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Bottom Edit Controller Card
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
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (editSection) {
                        "text" -> {
                            OutlinedTextField(
                                value = headerTitle,
                                onValueChange = { headerTitle = it },
                                label = { Text("Festival / Header Title") },
                                modifier = Modifier.fillMaxWidth().testTag("tpl_title_field")
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = greetingText,
                                onValueChange = { greetingText = it },
                                label = { Text("Greeting Message") },
                                modifier = Modifier.fillMaxWidth().testTag("tpl_greeting_field")
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = footerQuote,
                                onValueChange = { footerQuote = it },
                                label = { Text("Footer Hashtags / Quote") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        "frame" -> {
                            Text("Frame Shape", style = MaterialTheme.typography.labelMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = frameShape == "rounded",
                                    onClick = { frameShape = "rounded" },
                                    label = { Text("Rounded") }
                                )
                                FilterChip(
                                    selected = frameShape == "circle",
                                    onClick = { frameShape = "circle" },
                                    label = { Text("Circle") }
                                )
                                FilterChip(
                                    selected = frameShape == "rect",
                                    onClick = { frameShape = "rect" },
                                    label = { Text("Square") }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            ValueSliderRow(
                                label = "Border Width",
                                value = borderWidth,
                                range = 0f..12f,
                                onValueChange = { borderWidth = it }
                            )
                            Text("Border Color", style = MaterialTheme.typography.labelMedium)
                            PaletteColorPicker(
                                selectedColor = borderColor,
                                onColorSelected = { borderColor = it }
                            )
                        }

                        "colors" -> {
                            Text("Gradient Top Color", style = MaterialTheme.typography.labelMedium)
                            PaletteColorPicker(
                                selectedColor = gradientStart,
                                onColorSelected = { gradientStart = it }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Gradient Bottom Color", style = MaterialTheme.typography.labelMedium)
                            PaletteColorPicker(
                                selectedColor = gradientEnd,
                                onColorSelected = { gradientEnd = it }
                            )
                        }
                    }
                }
            }
        }
    }
}
