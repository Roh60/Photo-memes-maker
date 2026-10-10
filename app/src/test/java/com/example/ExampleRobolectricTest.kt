package com.example

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CaptionLibrary
import com.example.data.model.LayerItem
import com.example.data.model.LayerOrderAction
import com.example.data.model.LayerUtils
import com.example.data.model.MemeTextBox
import com.example.data.model.MemeTemplateRepository
import com.example.data.model.PlacedSticker
import com.example.data.model.StickerLibrary
import com.example.data.model.TemplateRepository
import com.example.ui.mememaker.ImageFilterViewModel
import com.example.ui.mememaker.PRESET_DEFINITIONS
import com.example.util.BitmapUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Photo & Memes", appName)
    }

    @Test
    fun `verify templates catalog loaded properly`() {
        val allTemplates = TemplateRepository.getAllTemplates()
        assertTrue("Templates catalog should not be empty", allTemplates.isNotEmpty())
        assertTrue("Festival templates should have Diwali", allTemplates.any { it.subCategory == "Diwali" })
        assertTrue("Festival templates should have Holi", allTemplates.any { it.subCategory == "Holi" })
    }

    @Test
    fun `verify meme templates and caption library`() {
        val memeTemplates = MemeTemplateRepository.TEMPLATES
        assertTrue("Meme templates should not be empty", memeTemplates.isNotEmpty())

        val funnyCaptions = CaptionLibrary.getIdeasByCategory("Funny")
        assertTrue("Caption ideas should have funny items", funnyCaptions.isNotEmpty())
    }

    @Test
    fun `verify drawable sticker asset library and transformation models`() {
        val drawables = StickerLibrary.DRAWABLE_STICKERS
        assertTrue("Drawable asset library must not be empty", drawables.isNotEmpty())

        // Check key categories
        val trending = StickerLibrary.getDrawableStickersByCategory("Trending")
        assertTrue("Trending category should contain stickers", trending.isNotEmpty())
        assertTrue("Should have thug glasses or laser eyes", trending.any { it.id == "thug_glasses" || it.id == "laser_eyes" })

        val glassesProps = StickerLibrary.getDrawableStickersByCategory("Glasses & Props")
        assertTrue("Glasses & Props should contain stickers", glassesProps.isNotEmpty())

        val badges = StickerLibrary.getDrawableStickersByCategory("Badges & Stamps")
        assertTrue("Badges & Stamps should contain stickers", badges.isNotEmpty())

        val bubbles = StickerLibrary.getDrawableStickersByCategory("Bubbles & Bursts")
        assertTrue("Bubbles & Bursts should contain stickers", bubbles.isNotEmpty())

        // Verify PlacedSticker move, scale, and rotate model
        val sticker = PlacedSticker(
            drawableResId = R.drawable.ic_sticker_thug_glasses,
            label = "Thug Glasses",
            xPercent = 0.5f,
            yPercent = 0.4f,
            scale = 1.25f,
            rotation = 45f
        )
        assertEquals(0.5f, sticker.xPercent)
        assertEquals(0.4f, sticker.yPercent)
        assertEquals(1.25f, sticker.scale)
        assertEquals(45f, sticker.rotation)

        // Test transformations
        val moved = sticker.copy(xPercent = 0.7f, yPercent = 0.8f)
        assertEquals(0.7f, moved.xPercent)
        assertEquals(0.8f, moved.yPercent)

        val scaled = sticker.copy(scale = 2.0f)
        assertEquals(2.0f, scaled.scale)

        val rotated = sticker.copy(rotation = -90f)
        assertEquals(-90f, rotated.rotation)
    }

    @Test
    fun `verify collage template repository and slot layouts`() {
        val templates = com.example.data.model.CollageTemplateRepository.getAllTemplates()
        assertTrue("Templates must not be empty", templates.isNotEmpty())

        // Ensure 2, 3, 4, 5, and 6 photo templates exist
        for (count in 2..6) {
            val matching = com.example.data.model.CollageTemplateRepository.getTemplatesByPhotoCount(count)
            assertTrue("Templates for $count photos must exist", matching.isNotEmpty())
        }

        // Validate normalized coordinate bounds for every template and its slots
        templates.forEach { template ->
            assertEquals(
                "Slots count must match photoCount for ${template.id}",
                template.photoCount,
                template.slots.size
            )
            template.slots.forEach { slot ->
                assertTrue("Slot left must be >= 0 in ${template.id}", slot.left >= 0f)
                assertTrue("Slot top must be >= 0 in ${template.id}", slot.top >= 0f)
                assertTrue("Slot right must be <= 1 in ${template.id}", slot.right <= 1.001f)
                assertTrue("Slot bottom must be <= 1 in ${template.id}", slot.bottom <= 1.001f)
                assertTrue("Slot left < right in ${template.id}", slot.left < slot.right)
                assertTrue("Slot top < bottom in ${template.id}", slot.top < slot.bottom)
            }
        }

        // Test independent slot adjustment state
        val slotState = com.example.data.model.CollageSlotState(
            slotId = 1,
            panX = 0.2f,
            panY = -0.1f,
            scale = 1.5f,
            rotation = 90f,
            isFlippedHorizontal = true,
            isFlippedVertical = false,
            filter = "sepia"
        )
        assertEquals(0.2f, slotState.panX)
        assertEquals(-0.1f, slotState.panY)
        assertEquals(1.5f, slotState.scale)
        assertEquals(90f, slotState.rotation)
        assertTrue(slotState.isFlippedHorizontal)
        assertEquals("sepia", slotState.filter)
    }

    @Test
    fun `verify ImageFilterViewModel generates low-resolution thumbnails with each filter preset applied`() = runBlocking {
        val viewModel = ImageFilterViewModel()
        val testBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)

        viewModel.setSourceBitmap(testBitmap)
        val thumbnails = viewModel.generateThumbnailsSync(testBitmap)
        assertTrue("Thumbnails list should not be empty", thumbnails.isNotEmpty())
        assertEquals(PRESET_DEFINITIONS.size, thumbnails.size)

        // Verify key presets: Sepia, Grayscale, Vintage, Vivid, Warm, Cinematic, HDR, Neon, None
        val sepiaThumb = thumbnails.firstOrNull { it.id == "sepia" }
        assertNotNull("Sepia thumbnail should exist", sepiaThumb)
        assertNotNull("Sepia thumbnail bitmap should be non-null", sepiaThumb?.bitmap)
        assertTrue("Thumbnail width should be low-resolution (<= 120)", (sepiaThumb?.bitmap?.width ?: 0) <= 120)

        val grayscaleThumb = thumbnails.firstOrNull { it.id == "grayscale" }
        assertNotNull("Grayscale thumbnail should exist", grayscaleThumb)
        assertNotNull("Grayscale thumbnail bitmap should be non-null", grayscaleThumb?.bitmap)

        val vintageThumb = thumbnails.firstOrNull { it.id == "vintage" }
        assertNotNull("Vintage thumbnail should exist", vintageThumb)
        assertNotNull("Vintage thumbnail bitmap should be non-null", vintageThumb?.bitmap)

        // Test applying presets
        viewModel.applyPreset("sepia")
        assertEquals("sepia", viewModel.uiState.value.activePreset)
        assertEquals(1.0f, viewModel.uiState.value.sepia)

        viewModel.applyPreset("grayscale")
        assertEquals("grayscale", viewModel.uiState.value.activePreset)
        assertEquals(0f, viewModel.uiState.value.saturation)

        viewModel.applyPreset("vintage")
        assertEquals("vintage", viewModel.uiState.value.activePreset)
        assertTrue(viewModel.uiState.value.sepia > 0f)

        // Test manual sliders
        viewModel.setBrightness(15f)
        assertEquals(15f, viewModel.uiState.value.brightness)

        viewModel.setContrast(1.3f)
        assertEquals(1.3f, viewModel.uiState.value.contrast)

        viewModel.resetFilters()
        assertEquals("none", viewModel.uiState.value.activePreset)
        assertEquals(0f, viewModel.uiState.value.brightness)
    }

    @Test
    fun `verify layer items, ordering, visibility, and locking mechanisms`() {
        val textBox1 = MemeTextBox(id = "text_1", text = "Top Caption", isVisible = true, isLocked = false)
        val textBox2 = MemeTextBox(id = "text_2", text = "Bottom Caption", isVisible = false, isLocked = true)
        val sticker1 = PlacedSticker(id = "sticker_1", emoji = "🔥", isVisible = true, isLocked = false)
        val sticker2 = PlacedSticker(id = "sticker_2", emoji = "😂", isVisible = true, isLocked = true)

        val textBoxes = listOf(textBox1, textBox2)
        val stickers = listOf(sticker1, sticker2)

        // Initial custom layer stack order: bottom to top
        val initialOrder = listOf("text_1", "sticker_1", "text_2", "sticker_2")
        val sortedLayers = LayerUtils.buildSortedLayers(textBoxes, stickers, initialOrder)

        assertEquals(4, sortedLayers.size)
        assertEquals("text_1", sortedLayers[0].id)
        assertEquals("sticker_1", sortedLayers[1].id)
        assertEquals("text_2", sortedLayers[2].id)
        assertEquals("sticker_2", sortedLayers[3].id)

        // Verify layer properties mapping
        assertTrue(sortedLayers[0].isVisible)
        assertFalse(sortedLayers[0].isLocked)
        assertFalse(sortedLayers[2].isVisible)
        assertTrue(sortedLayers[2].isLocked)
        assertTrue(sortedLayers[3].isLocked)

        // Test SEND_TO_BACK (move sticker_2 to index 0)
        val afterSendToBack = LayerUtils.reorder(initialOrder, "sticker_2", LayerOrderAction.SEND_TO_BACK)
        assertEquals("sticker_2", afterSendToBack[0])

        // Test BRING_TO_FRONT (move text_1 to the top)
        val afterBringToFront = LayerUtils.reorder(initialOrder, "text_1", LayerOrderAction.BRING_TO_FRONT)
        assertEquals("text_1", afterBringToFront.last())

        // Test MOVE_UP (move sticker_1 up one position)
        val afterMoveUp = LayerUtils.reorder(initialOrder, "sticker_1", LayerOrderAction.MOVE_UP)
        assertEquals("sticker_1", afterMoveUp[2])

        // Test MOVE_DOWN (move sticker_2 down one position)
        val afterMoveDown = LayerUtils.reorder(initialOrder, "sticker_2", LayerOrderAction.MOVE_DOWN)
        assertEquals("sticker_2", afterMoveDown[2])

        // Test rebuild with new order
        val reorderedLayers = LayerUtils.buildSortedLayers(textBoxes, stickers, afterBringToFront)
        assertEquals("text_1", reorderedLayers.last().id)
    }

    @Test
    fun `verify FileProvider image cache creation and sharing intent`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val testBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)

        // Verify shareBitmap creates cache file and prepares FileProvider
        BitmapUtils.shareBitmap(context, testBitmap, "Test Share Caption")

        val cacheDir = java.io.File(context.cacheDir, "shared_images")
        assertTrue("shared_images cache folder should exist", cacheDir.exists())
        val files = cacheDir.listFiles()
        assertTrue("shared_images cache folder should contain shared JPEG file", files != null && files.isNotEmpty())
    }
}

