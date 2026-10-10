package com.example.data.model

import android.graphics.Bitmap

/**
 * Normalized rectangular slot within a collage layout (0f..1f coordinate space).
 */
data class CollageSlot(
    val id: Int,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val label: String = "Slot $id"
)

/**
 * Pre-defined collage template layout.
 */
data class CollageTemplate(
    val id: String,
    val name: String,
    val category: String, // e.g. "2 Photos", "3 Photos", "4 Photos", "5 Photos", "6 Photos"
    val photoCount: Int,
    val slots: List<CollageSlot>,
    val defaultAspectRatio: Float = 1f, // 1f = 1:1 square
    val description: String = ""
)

/**
 * State and adjustment properties for an image within a collage slot.
 */
data class CollageSlotState(
    val slotId: Int,
    var bitmap: Bitmap? = null,
    var panX: Float = 0f, // pan offset in normalized fraction or pixels
    var panY: Float = 0f,
    var scale: Float = 1.0f, // 1.0f to 4.0f
    var rotation: Float = 0f, // degrees (0, 90, 180, 270, etc.)
    var isFlippedHorizontal: Boolean = false,
    var isFlippedVertical: Boolean = false,
    var filter: String = "none" // "none", "vivid", "warm", "cool", "bw", "sepia", "vintage", "bright"
)

object CollageTemplateRepository {

    val TEMPLATES: List<CollageTemplate> = listOf(
        // === 2 PHOTOS ===
        CollageTemplate(
            id = "2_side_by_side",
            name = "Side-by-Side (1x2)",
            category = "2 Photos",
            photoCount = 2,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.5f, 1.0f, "Left"),
                CollageSlot(2, 0.5f, 0.0f, 1.0f, 1.0f, "Right")
            ),
            description = "Classic vertical split"
        ),
        CollageTemplate(
            id = "2_top_bottom",
            name = "Top-Bottom (2x1)",
            category = "2 Photos",
            photoCount = 2,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 1.0f, 0.5f, "Top"),
                CollageSlot(2, 0.0f, 0.5f, 1.0f, 1.0f, "Bottom")
            ),
            description = "Horizontal split"
        ),
        CollageTemplate(
            id = "2_hero_top",
            name = "Wide Top & Base",
            category = "2 Photos",
            photoCount = 2,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 1.0f, 0.62f, "Hero Top"),
                CollageSlot(2, 0.0f, 0.62f, 1.0f, 1.0f, "Base")
            ),
            description = "60/40 cinematic horizontal split"
        ),
        CollageTemplate(
            id = "2_couples_romance",
            name = "Couples & Romance",
            category = "2 Photos",
            photoCount = 2,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.5f, 1.0f, "Partner 1"),
                CollageSlot(2, 0.5f, 0.0f, 1.0f, 1.0f, "Partner 2")
            ),
            description = "Romantic side-by-side portrait layout"
        ),
        CollageTemplate(
            id = "2_best_friends",
            name = "Best Friends Duo",
            category = "2 Photos",
            photoCount = 2,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.5f, 1.0f, "Bestie 1"),
                CollageSlot(2, 0.5f, 0.0f, 1.0f, 1.0f, "Bestie 2")
            ),
            description = "Friendship dual memory frame"
        ),
        CollageTemplate(
            id = "2_before_after",
            name = "Before & After",
            category = "2 Photos",
            photoCount = 2,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.5f, 1.0f, "Before"),
                CollageSlot(2, 0.5f, 0.0f, 1.0f, 1.0f, "After")
            ),
            description = "Comparison layout for transformations"
        ),
        CollageTemplate(
            id = "2_pip",
            name = "Picture in Picture",
            category = "2 Photos",
            photoCount = 2,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 1.0f, 1.0f, "Background"),
                CollageSlot(2, 0.58f, 0.58f, 0.96f, 0.96f, "Inset")
            ),
            description = "Full background with floating inset"
        ),

        // === 3 PHOTOS ===
        CollageTemplate(
            id = "3_top_one_bottom_two",
            name = "Hero Top + 2 Bottom",
            category = "3 Photos",
            photoCount = 3,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 1.0f, 0.5f, "Top Hero"),
                CollageSlot(2, 0.0f, 0.5f, 0.5f, 1.0f, "Bottom Left"),
                CollageSlot(3, 0.5f, 0.5f, 1.0f, 1.0f, "Bottom Right")
            ),
            description = "Header banner with 2 bottom columns"
        ),
        CollageTemplate(
            id = "3_top_two_bottom_one",
            name = "2 Top + Hero Bottom",
            category = "3 Photos",
            photoCount = 3,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.5f, 0.5f, "Top Left"),
                CollageSlot(2, 0.5f, 0.0f, 1.0f, 0.5f, "Top Right"),
                CollageSlot(3, 0.0f, 0.5f, 1.0f, 1.0f, "Bottom Hero")
            ),
            description = "2 top tiles with wide base banner"
        ),
        CollageTemplate(
            id = "3_left_hero_two_right",
            name = "Left Hero + 2 Right",
            category = "3 Photos",
            photoCount = 3,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.55f, 1.0f, "Left Hero"),
                CollageSlot(2, 0.55f, 0.0f, 1.0f, 0.5f, "Right Top"),
                CollageSlot(3, 0.55f, 0.5f, 1.0f, 1.0f, "Right Bottom")
            ),
            description = "Tall left feature with stacked right photos"
        ),
        CollageTemplate(
            id = "3_columns",
            name = "3 Columns (1x3)",
            category = "3 Photos",
            photoCount = 3,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.333f, 1.0f, "Left"),
                CollageSlot(2, 0.333f, 0.0f, 0.667f, 1.0f, "Center"),
                CollageSlot(3, 0.667f, 0.0f, 1.0f, 1.0f, "Right")
            ),
            description = "3 vertical strips side by side"
        ),
        CollageTemplate(
            id = "3_rows",
            name = "3 Rows (3x1)",
            category = "3 Photos",
            photoCount = 3,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 1.0f, 0.333f, "Top"),
                CollageSlot(2, 0.0f, 0.333f, 1.0f, 0.667f, "Middle"),
                CollageSlot(3, 0.0f, 0.667f, 1.0f, 1.0f, "Bottom")
            ),
            description = "3 horizontal strips stacked"
        ),

        // === 4 PHOTOS ===
        CollageTemplate(
            id = "4_classic_grid",
            name = "Classic 2x2 Grid",
            category = "4 Photos",
            photoCount = 4,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.5f, 0.5f, "Top Left"),
                CollageSlot(2, 0.5f, 0.0f, 1.0f, 0.5f, "Top Right"),
                CollageSlot(3, 0.0f, 0.5f, 0.5f, 1.0f, "Bottom Left"),
                CollageSlot(4, 0.5f, 0.5f, 1.0f, 1.0f, "Bottom Right")
            ),
            description = "Symmetric 4 quadrant square grid"
        ),
        CollageTemplate(
            id = "4_hero_left_three_right",
            name = "Hero Left + 3 Stack",
            category = "4 Photos",
            photoCount = 4,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.58f, 1.0f, "Hero Left"),
                CollageSlot(2, 0.58f, 0.0f, 1.0f, 0.333f, "Right Top"),
                CollageSlot(3, 0.58f, 0.333f, 1.0f, 0.667f, "Right Mid"),
                CollageSlot(4, 0.58f, 0.667f, 1.0f, 1.0f, "Right Low")
            ),
            description = "Large featured image with 3 side thumbnails"
        ),
        CollageTemplate(
            id = "4_hero_top_three_bottom",
            name = "Hero Top + 3 Bottom",
            category = "4 Photos",
            photoCount = 4,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 1.0f, 0.60f, "Hero Top"),
                CollageSlot(2, 0.0f, 0.60f, 0.333f, 1.0f, "Bottom 1"),
                CollageSlot(3, 0.333f, 0.60f, 0.667f, 1.0f, "Bottom 2"),
                CollageSlot(4, 0.667f, 0.60f, 1.0f, 1.0f, "Bottom 3")
            ),
            description = "Wide panorama hero with 3 bottom columns"
        ),
        CollageTemplate(
            id = "4_vertical_strips",
            name = "4 Strips (1x4)",
            category = "4 Photos",
            photoCount = 4,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.25f, 1.0f, "Col 1"),
                CollageSlot(2, 0.25f, 0.0f, 0.50f, 1.0f, "Col 2"),
                CollageSlot(3, 0.50f, 0.0f, 0.75f, 1.0f, "Col 3"),
                CollageSlot(4, 0.75f, 0.0f, 1.0f, 1.0f, "Col 4")
            ),
            description = "4 slender vertical photo strips"
        ),

        // === 5 PHOTOS ===
        CollageTemplate(
            id = "5_two_top_three_bottom",
            name = "2 Top + 3 Bottom",
            category = "5 Photos",
            photoCount = 5,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.5f, 0.5f, "Top Left"),
                CollageSlot(2, 0.5f, 0.0f, 1.0f, 0.5f, "Top Right"),
                CollageSlot(3, 0.0f, 0.5f, 0.333f, 1.0f, "Bottom 1"),
                CollageSlot(4, 0.333f, 0.5f, 0.667f, 1.0f, "Bottom 2"),
                CollageSlot(5, 0.667f, 0.5f, 1.0f, 1.0f, "Bottom 3")
            ),
            description = "2 large top photos with 3 bottom columns"
        ),
        CollageTemplate(
            id = "5_center_hero",
            name = "Center Hero + 4 Corners",
            category = "5 Photos",
            photoCount = 5,
            slots = listOf(
                CollageSlot(1, 0.22f, 0.22f, 0.78f, 0.78f, "Center Star"),
                CollageSlot(2, 0.0f, 0.0f, 0.5f, 0.22f, "Top Left"),
                CollageSlot(3, 0.5f, 0.0f, 1.0f, 0.22f, "Top Right"),
                CollageSlot(4, 0.0f, 0.78f, 0.5f, 1.0f, "Bottom Left"),
                CollageSlot(5, 0.5f, 0.78f, 1.0f, 1.0f, "Bottom Right")
            ),
            description = "Focal center star photo with corner accents"
        ),
        CollageTemplate(
            id = "5_hero_left_four_right",
            name = "Hero Left + 4 Quad Right",
            category = "5 Photos",
            photoCount = 5,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.5f, 1.0f, "Hero Left"),
                CollageSlot(2, 0.5f, 0.0f, 0.75f, 0.5f, "Right TL"),
                CollageSlot(3, 0.75f, 0.0f, 1.0f, 0.5f, "Right TR"),
                CollageSlot(4, 0.5f, 0.5f, 0.75f, 1.0f, "Right BL"),
                CollageSlot(5, 0.75f, 0.5f, 1.0f, 1.0f, "Right BR")
            ),
            description = "Tall left portrait with 2x2 grid on right"
        ),

        // === 6 PHOTOS ===
        CollageTemplate(
            id = "6_classic_3x2",
            name = "Classic 3x2 Grid",
            category = "6 Photos",
            photoCount = 6,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.333f, 0.5f, "P1"),
                CollageSlot(2, 0.333f, 0.0f, 0.667f, 0.5f, "P2"),
                CollageSlot(3, 0.667f, 0.0f, 1.0f, 0.5f, "P3"),
                CollageSlot(4, 0.0f, 0.5f, 0.333f, 1.0f, "P4"),
                CollageSlot(5, 0.333f, 0.5f, 0.667f, 1.0f, "P5"),
                CollageSlot(6, 0.667f, 0.5f, 1.0f, 1.0f, "P6")
            ),
            description = "3 columns x 2 rows symmetric layout"
        ),
        CollageTemplate(
            id = "6_classic_2x3",
            name = "Classic 2x3 Grid",
            category = "6 Photos",
            photoCount = 6,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 0.5f, 0.333f, "P1"),
                CollageSlot(2, 0.5f, 0.0f, 1.0f, 0.333f, "P2"),
                CollageSlot(3, 0.0f, 0.333f, 0.5f, 0.667f, "P3"),
                CollageSlot(4, 0.5f, 0.333f, 1.0f, 0.667f, "P4"),
                CollageSlot(5, 0.0f, 0.667f, 0.5f, 1.0f, "P5"),
                CollageSlot(6, 0.5f, 0.667f, 1.0f, 1.0f, "P6")
            ),
            description = "2 columns x 3 rows portrait grid"
        ),
        CollageTemplate(
            id = "6_hero_center_surround",
            name = "Hero Top + 5 Grid",
            category = "6 Photos",
            photoCount = 6,
            slots = listOf(
                CollageSlot(1, 0.0f, 0.0f, 1.0f, 0.45f, "Hero Header"),
                CollageSlot(2, 0.0f, 0.45f, 0.333f, 0.725f, "P2"),
                CollageSlot(3, 0.333f, 0.45f, 0.667f, 0.725f, "P3"),
                CollageSlot(4, 0.667f, 0.45f, 1.0f, 0.725f, "P4"),
                CollageSlot(5, 0.0f, 0.725f, 0.5f, 1.0f, "P5"),
                CollageSlot(6, 0.5f, 0.725f, 1.0f, 1.0f, "P6")
            ),
            description = "Featured wide header with 5 supporting photos"
        )
    )

    fun getAllTemplates(): List<CollageTemplate> = TEMPLATES

    fun getTemplatesByPhotoCount(count: Int): List<CollageTemplate> =
        TEMPLATES.filter { it.photoCount == count }

    fun getTemplateById(id: String): CollageTemplate =
        TEMPLATES.firstOrNull { it.id == id } ?: TEMPLATES.first()
}
