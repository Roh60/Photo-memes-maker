package com.example.data.model

data class PhotoAdjustment(
    val brightness: Float = 0f,     // -100f to 100f
    val contrast: Float = 1.0f,     // 0.5f to 2.0f
    val saturation: Float = 1.0f,   // 0.0f to 2.5f
    val warmth: Float = 0f,         // -50f to 50f
    val filterPreset: String = "none",
    val filterIntensity: Float = 1.0f, // 0.0f to 1.0f intensity
    val rotation: Float = 0f,       // 0, 90, 180, 270
    val flipH: Boolean = false,
    val flipV: Boolean = false,
    val vignette: Float = 0f        // 0.0f to 1.0f
)

data class FilterPreset(
    val id: String,
    val name: String,
    val description: String,
    val previewHue: Long
)

val AVAILABLE_FILTER_PRESETS = listOf(
    FilterPreset("none", "Original", "True to life colors", 0xFF6366F1),
    FilterPreset("cinematic", "Cinematic", "Teal & orange film grade", 0xFF0D9488),
    FilterPreset("glow", "Glow", "Dreamy luminous radiance", 0xFFF472B6),
    FilterPreset("vintage", "Vintage", "70s retro analog nostalgia", 0xFFD97706),
    FilterPreset("aesthetic", "Aesthetic", "Soft pastel dreamy tones", 0xFFA855F7),
    FilterPreset("warm", "Warm", "Golden sunlit sunset glow", 0xFFF59E0B),
    FilterPreset("cool", "Cool", "Refreshing crisp cyan tones", 0xFF06B6D4),
    FilterPreset("bw", "Black & White", "Classic deep contrast monochrome", 0xFF475569),
    FilterPreset("festive", "Festive", "Sparkling festive celebration", 0xFFEF4444),
    FilterPreset("vivid", "Vivid", "Punchy saturated colors", 0xFFEC4899),
    FilterPreset("hdr", "HDR Clarity", "Crisp detailed dynamic range", 0xFF3B82F6),
    FilterPreset("neon", "Cyber Neon", "Electric saturated club pop", 0xFF8B5CF6),
    FilterPreset("sepia", "Sepia", "Antique timeless parchment", 0xFFB45309)
)
