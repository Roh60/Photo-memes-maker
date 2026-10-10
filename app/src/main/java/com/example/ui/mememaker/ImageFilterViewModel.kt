package com.example.ui.mememaker

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.util.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Low-resolution thumbnail preview item for an image filter preset.
 */
data class FilterThumbnail(
    val id: String,
    val name: String,
    val description: String = "",
    val bitmap: Bitmap? = null
)

/**
 * Filter preset configuration with default tuning parameters and custom matrix generator.
 */
data class FilterPresetDefinition(
    val id: String,
    val name: String,
    val description: String,
    val brightness: Float = 0f,
    val contrast: Float = 1.0f,
    val saturation: Float = 1.0f,
    val sepia: Float = 0.0f
)

val PRESET_DEFINITIONS = listOf(
    FilterPresetDefinition("none", "Original", "True to life colors", 0f, 1.0f, 1.0f, 0f),
    FilterPresetDefinition("sepia", "Sepia", "Warm antique brown tone", 5f, 1.1f, 0.8f, 1.0f),
    FilterPresetDefinition("grayscale", "Grayscale", "Classic noir monochrome", 0f, 1.2f, 0f, 0f),
    FilterPresetDefinition("vintage", "Vintage", "70s faded analog retro film", 8f, 1.15f, 0.85f, 0.45f),
    FilterPresetDefinition("vivid", "Vivid", "Rich punchy colors and pop", 10f, 1.25f, 1.6f, 0f),
    FilterPresetDefinition("warm", "Warm", "Golden sunset afternoon glow", 8f, 1.05f, 1.2f, 0.35f),
    FilterPresetDefinition("cinematic", "Cinematic", "Teal & orange moody film look", -4f, 1.2f, 1.25f, 0f),
    FilterPresetDefinition("hdr", "HDR", "High dynamic clarity & detail", 6f, 1.35f, 1.3f, 0f),
    FilterPresetDefinition("neon", "Neon", "Cyberpunk saturated vibrance", 12f, 1.4f, 1.8f, 0f)
)

data class ImageFilterUiState(
    val brightness: Float = 0f,       // -100f to 100f (default 0f)
    val contrast: Float = 1.0f,       // 0.5f to 2.0f (default 1.0f)
    val saturation: Float = 1.0f,     // 0.0f to 2.5f (default 1.0f)
    val sepia: Float = 0.0f,          // 0.0f to 1.0f (default 0.0f)
    val activePreset: String = "none",
    val isProcessing: Boolean = false,
    val thumbnails: List<FilterThumbnail> = emptyList(),
    val isGeneratingThumbnails: Boolean = false
) {
    val filterThumbnails: List<FilterThumbnail> get() = thumbnails
}

class ImageFilterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ImageFilterUiState())
    val uiState: StateFlow<ImageFilterUiState> = _uiState.asStateFlow()

    private val _thumbnails = MutableStateFlow<List<FilterThumbnail>>(emptyList())
    val thumbnails: StateFlow<List<FilterThumbnail>> = _thumbnails.asStateFlow()
    val filterThumbnails: StateFlow<List<FilterThumbnail>> = _thumbnails.asStateFlow()

    private var originalSourceBitmap: Bitmap? = null
    private var thumbnailJob: Job? = null
    private var filterJob: Job? = null

    private val _processedBitmap = MutableStateFlow<Bitmap?>(null)
    val processedBitmap: StateFlow<Bitmap?> = _processedBitmap.asStateFlow()

    fun setSourceBitmap(bitmap: Bitmap?) {
        originalSourceBitmap = bitmap
        applyFilters()
        generateThumbnails(bitmap)
    }

    fun setBrightness(brightness: Float) {
        _uiState.update { it.copy(brightness = brightness) }
        applyFilters()
    }

    fun setContrast(contrast: Float) {
        _uiState.update { it.copy(contrast = contrast) }
        applyFilters()
    }

    fun setSaturation(saturation: Float) {
        _uiState.update { it.copy(saturation = saturation) }
        applyFilters()
    }

    fun setSepia(sepia: Float) {
        _uiState.update { it.copy(sepia = sepia) }
        applyFilters()
    }

    fun applyPreset(preset: String) {
        val normalizedKey = when (preset.lowercase()) {
            "bw" -> "grayscale"
            "b&w" -> "grayscale"
            "retro" -> "vintage"
            "cool" -> "cinematic"
            else -> preset.lowercase()
        }

        val definition = PRESET_DEFINITIONS.firstOrNull { it.id == normalizedKey }
            ?: PRESET_DEFINITIONS.first()

        _uiState.update {
            it.copy(
                brightness = definition.brightness,
                contrast = definition.contrast,
                saturation = definition.saturation,
                sepia = definition.sepia,
                activePreset = definition.id
            )
        }
        applyFilters()
    }

    fun resetFilters() {
        val defaultDef = PRESET_DEFINITIONS.first()
        _uiState.update {
            it.copy(
                brightness = defaultDef.brightness,
                contrast = defaultDef.contrast,
                saturation = defaultDef.saturation,
                sepia = defaultDef.sepia,
                activePreset = "none"
            )
        }
        applyFilters()
    }

    /**
     * Generates low-resolution thumbnail bitmaps (e.g. max ~120px) synchronously or from a suspend context.
     */
    suspend fun generateThumbnailsSync(source: Bitmap? = originalSourceBitmap): List<FilterThumbnail> {
        if (source == null) {
            _thumbnails.value = emptyList()
            _uiState.update { it.copy(thumbnails = emptyList(), isGeneratingThumbnails = false) }
            return emptyList()
        }

        _uiState.update { it.copy(isGeneratingThumbnails = true) }

        val generatedList = withContext(Dispatchers.Default) {
            // Downscale to low-resolution thumbnail base (max 120px) for high performance
            val maxDimension = 120
            val srcWidth = source.width
            val srcHeight = source.height

            val scale = if (srcWidth > 0 && srcHeight > 0) {
                val maxOriginal = maxOf(srcWidth, srcHeight).toFloat()
                minOf(1.0f, maxDimension.toFloat() / maxOriginal)
            } else 1.0f

            val targetWidth = (srcWidth * scale).toInt().coerceAtLeast(1)
            val targetHeight = (srcHeight * scale).toInt().coerceAtLeast(1)

            val thumbnailBase = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)

            // Generate thumbnail for each available filter
            PRESET_DEFINITIONS.map { definition ->
                val colorMatrix = buildColorMatrix(
                    brightness = definition.brightness,
                    contrast = definition.contrast,
                    saturation = definition.saturation,
                    sepia = definition.sepia,
                    presetId = definition.id
                )

                val filteredThumbnail = BitmapUtils.processBitmap(
                    source = thumbnailBase,
                    colorMatrix = colorMatrix
                )

                FilterThumbnail(
                    id = definition.id,
                    name = definition.name,
                    description = definition.description,
                    bitmap = filteredThumbnail
                )
            }
        }

        _thumbnails.value = generatedList
        _uiState.update { it.copy(thumbnails = generatedList, isGeneratingThumbnails = false) }
        return generatedList
    }

    /**
     * Generates low-resolution thumbnail bitmaps asynchronously via viewModelScope.
     */
    fun generateThumbnails(source: Bitmap? = originalSourceBitmap): Job? {
        thumbnailJob?.cancel()
        if (source == null) {
            _thumbnails.value = emptyList()
            _uiState.update { it.copy(thumbnails = emptyList(), isGeneratingThumbnails = false) }
            return null
        }

        val job = viewModelScope.launch {
            generateThumbnailsSync(source)
        }
        thumbnailJob = job
        return job
    }

    private fun applyFilters() {
        val src = originalSourceBitmap
        if (src == null) {
            _processedBitmap.value = null
            return
        }

        val state = _uiState.value

        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val output = withContext(Dispatchers.Default) {
                val colorMatrix = buildColorMatrix(
                    brightness = state.brightness,
                    contrast = state.contrast,
                    saturation = state.saturation,
                    sepia = state.sepia,
                    presetId = state.activePreset
                )

                BitmapUtils.processBitmap(
                    source = src,
                    colorMatrix = colorMatrix
                )
            }

            _processedBitmap.value = output
            _uiState.update { it.copy(isProcessing = false) }
        }
    }

    companion object {
        /**
         * Construct ColorMatrix combining Sepia, Saturation, Contrast, Brightness, and Preset adjustments.
         */
        fun buildColorMatrix(
            brightness: Float,
            contrast: Float,
            saturation: Float,
            sepia: Float,
            presetId: String = "none"
        ): ColorMatrix {
            val colorMatrix = ColorMatrix()

            // 1. Sepia transformation
            if (sepia > 0f) {
                val s = sepia.coerceIn(0f, 1f)
                val rR = 1f + s * (0.393f - 1f)
                val rG = s * 0.769f
                val rB = s * 0.189f

                val gR = s * 0.349f
                val gG = 1f + s * (0.686f - 1f)
                val gB = s * 0.168f

                val bR = s * 0.272f
                val bG = s * 0.534f
                val bB = 1f + s * (0.131f - 1f)

                val sepiaMatrix = ColorMatrix(
                    floatArrayOf(
                        rR, rG, rB, 0f, 0f,
                        gR, gG, gB, 0f, 0f,
                        bR, bG, bB, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                colorMatrix.postConcat(sepiaMatrix)
            }

            // 2. Saturation
            if (saturation != 1.0f) {
                val satMatrix = ColorMatrix()
                satMatrix.setSaturation(saturation)
                colorMatrix.postConcat(satMatrix)
            }

            // 3. Contrast: offset = 128 * (1 - contrast)
            if (contrast != 1.0f) {
                val c = contrast
                val t = 128f * (1f - c)
                val contrastMatrix = ColorMatrix(
                    floatArrayOf(
                        c,  0f, 0f, 0f, t,
                        0f, c,  0f, 0f, t,
                        0f, 0f, c,  0f, t,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                colorMatrix.postConcat(contrastMatrix)
            }

            // 4. Brightness
            if (brightness != 0f) {
                val b = brightness
                val brightnessMatrix = ColorMatrix(
                    floatArrayOf(
                        1f, 0f, 0f, 0f, b,
                        0f, 1f, 0f, 0f, b,
                        0f, 0f, 1f, 0f, b,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                colorMatrix.postConcat(brightnessMatrix)
            }

            // 5. Preset-specific color balance enhancements
            when (presetId.lowercase()) {
                "vintage" -> {
                    val tint = ColorMatrix(
                        floatArrayOf(
                            1.05f, 0f, 0f, 0f, 12f,
                            0f, 0.98f, 0f, 0f, 8f,
                            0f, 0f, 0.88f, 0f, 4f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                    colorMatrix.postConcat(tint)
                }
                "cinematic" -> {
                    val cine = ColorMatrix(
                        floatArrayOf(
                            1.08f, 0f, 0f, 0f, 6f,
                            0f, 1.02f, 0f, 0f, 2f,
                            0f, 0f, 1.18f, 0f, 14f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                    colorMatrix.postConcat(cine)
                }
                "warm" -> {
                    val warm = ColorMatrix(
                        floatArrayOf(
                            1.10f, 0f, 0f, 0f, 15f,
                            0f, 1.03f, 0f, 0f, 5f,
                            0f, 0f, 0.90f, 0f, -8f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                    colorMatrix.postConcat(warm)
                }
            }

            return colorMatrix
        }
    }
}
