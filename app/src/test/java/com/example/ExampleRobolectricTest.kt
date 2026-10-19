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
import com.example.util.ImageProcessingUtils
import com.example.ui.components.MemeEditorState
import com.example.ui.components.MemeFontType
import com.example.ui.components.MemeTextObject
import com.example.ui.components.toMemeTextBox
import com.example.ui.components.toMemeTextObject
import com.example.ui.components.FilterCategory
import com.example.ui.components.toComposeColorMatrix
import com.example.data.model.AVAILABLE_FILTER_PRESETS
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

    @Test
    fun `verify ImageProcessingUtils cinematic, vintage, and black-and-white filters with intensity adjustment`() {
        val testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        // 1. Cinematic Filter with Intensity
        val cinematicFull = ImageProcessingUtils.applyCinematicFilter(testBitmap, intensity = 1.0f)
        assertNotNull(cinematicFull)
        assertEquals(100, cinematicFull.width)
        assertEquals(100, cinematicFull.height)

        val cinematicHalf = ImageProcessingUtils.applyCinematicFilter(testBitmap, intensity = 0.5f)
        assertNotNull(cinematicHalf)

        val cinematicZero = ImageProcessingUtils.applyCinematicFilter(testBitmap, intensity = 0.0f)
        assertNotNull(cinematicZero)

        // 2. Vintage Filter with Intensity
        val vintageFull = ImageProcessingUtils.applyVintageFilter(testBitmap, intensity = 1.0f)
        assertNotNull(vintageFull)
        val vintageLow = ImageProcessingUtils.applyVintageFilter(testBitmap, intensity = 0.25f)
        assertNotNull(vintageLow)

        // 3. Black & White Filter with Intensity
        val bwFull = ImageProcessingUtils.applyBlackAndWhiteFilter(testBitmap, intensity = 1.0f)
        assertNotNull(bwFull)
        val bwHalf = ImageProcessingUtils.applyBlackAndWhiteFilter(testBitmap, intensity = 0.5f)
        assertNotNull(bwHalf)

        // 4. ColorMatrix interpolation check
        val targetMatrix = ImageProcessingUtils.createCinematicColorMatrix()
        val identityInterp = ImageProcessingUtils.interpolateColorMatrix(targetMatrix, 0.0f)
        // At 0.0f intensity, diagonal elements should be 1.0f and offsets 0.0f
        assertEquals(1.0f, identityInterp.array[0], 0.01f)
        assertEquals(0.0f, identityInterp.array[4], 0.01f)

        // At 1.0f intensity, it matches targetMatrix
        val fullInterp = ImageProcessingUtils.interpolateColorMatrix(targetMatrix, 1.0f)
        assertEquals(targetMatrix.array[0], fullInterp.array[0], 0.01f)
        assertEquals(targetMatrix.array[4], fullInterp.array[4], 0.01f)

        // 5. Thumbnail creation and vignette
        val thumbnail = ImageProcessingUtils.createThumbnail(testBitmap, maxDimension = 50)
        assertNotNull(thumbnail)
        assertTrue(thumbnail.width <= 50 && thumbnail.height <= 50)

        val vignetted = ImageProcessingUtils.applyVignette(testBitmap, intensity = 0.6f)
        assertNotNull(vignetted)
    }

    @Test
    fun `verify MemeEditor state-based multi-text objects, font, color, and outline controls`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val editorState = com.example.ui.components.MemeEditorState()

        // 1. Initial State
        assertTrue("Initially text objects should be empty", editorState.textObjects.isEmpty())
        assertEquals(null, editorState.selectedTextId)

        // 2. Add multiple text objects
        val topText = editorState.addText(
            text = "WHEN THE APP COMPILES",
            xPercent = 0.5f,
            yPercent = 0.15f,
            fontSizeSp = 36f,
            fontType = com.example.ui.components.MemeFontType.IMPACT,
            textColor = androidx.compose.ui.graphics.Color.White,
            hasOutline = true,
            outlineColor = androidx.compose.ui.graphics.Color.Black,
            outlineWidth = 4f
        )
        assertEquals(1, editorState.textObjects.size)
        assertEquals(topText.id, editorState.selectedTextId)
        assertEquals("WHEN THE APP COMPILES", editorState.selectedText?.text)

        val bottomText = editorState.addText(
            text = "ON THE FIRST TRY",
            xPercent = 0.5f,
            yPercent = 0.85f,
            fontSizeSp = 32f,
            fontType = com.example.ui.components.MemeFontType.POPPINS,
            textColor = androidx.compose.ui.graphics.Color(0xFFFFEB3B), // Yellow
            hasOutline = true,
            outlineColor = androidx.compose.ui.graphics.Color.Black,
            outlineWidth = 3f
        )
        assertEquals(2, editorState.textObjects.size)
        assertEquals(bottomText.id, editorState.selectedTextId)

        // 3. Test Font Controls
        editorState.setFont(bottomText.id, com.example.ui.components.MemeFontType.KALAM)
        assertEquals(com.example.ui.components.MemeFontType.KALAM, editorState.selectedText?.fontType)

        editorState.setFontSize(bottomText.id, 42f)
        assertEquals(42f, editorState.selectedText?.fontSizeSp)

        // 4. Test Color Controls
        val cyanColor = androidx.compose.ui.graphics.Color(0xFF00E5FF)
        editorState.setTextColor(bottomText.id, cyanColor)
        assertEquals(cyanColor, editorState.selectedText?.textColor)

        // 5. Test Outline Controls
        val crimsonOutline = androidx.compose.ui.graphics.Color(0xFFB71C1C)
        editorState.setOutline(bottomText.id, hasOutline = true, outlineColor = crimsonOutline, outlineWidth = 6.0f)
        val updatedBottom = editorState.selectedText
        assertNotNull(updatedBottom)
        assertTrue(updatedBottom?.hasOutline == true)
        assertEquals(crimsonOutline, updatedBottom?.outlineColor)
        assertEquals(6.0f, updatedBottom?.outlineWidth ?: 0f, 0.01f)

        // Outline toggle off
        editorState.setOutline(bottomText.id, hasOutline = false)
        assertFalse(editorState.selectedText?.hasOutline == true)

        // 6. Test Rotation and Text Alignment
        editorState.setRotation(bottomText.id, 15f)
        assertEquals(15f, editorState.selectedText?.rotation ?: 0f, 0.01f)

        editorState.setTextAlign(bottomText.id, androidx.compose.ui.text.style.TextAlign.Left)
        assertEquals(androidx.compose.ui.text.style.TextAlign.Left, editorState.selectedText?.textAlign)

        // 7. Test Layer Operations: Duplicate, Reorder, Lock
        editorState.duplicateText(topText.id)
        assertEquals(3, editorState.textObjects.size)

        editorState.bringToFront(topText.id)
        assertEquals(topText.id, editorState.textObjects.last().id)

        editorState.sendToBack(topText.id)
        assertEquals(topText.id, editorState.textObjects.first().id)

        editorState.toggleLock(topText.id)
        assertTrue(editorState.textObjects.first().isLocked)

        // 8. Test Delete Text
        editorState.removeText(topText.id)
        assertEquals(2, editorState.textObjects.size)

        // 9. Test Interoperability with legacy MemeTextBox
        val legacyBox = com.example.data.model.MemeTextBox(
            text = "Legacy Text",
            fontSizeSp = 28f,
            textColor = 0xFFFFFFFF,
            strokeColor = 0xFF000000
        )
        val convertedObj = legacyBox.toMemeTextObject()
        assertEquals("Legacy Text", convertedObj.text)
        assertEquals(28f, convertedObj.fontSizeSp)

        val convertedBack = convertedObj.toMemeTextBox()
        assertEquals(legacyBox.text, convertedBack.text)

        // 10. Test Render to Bitmap with text and outline
        val baseBmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        editorState.imageBitmap = baseBmp
        val renderedBmp = editorState.renderToBitmap(context = context, targetWidth = 500, targetHeight = 500)
        assertNotNull(renderedBmp)
        assertEquals(500, renderedBmp.width)
        assertEquals(500, renderedBmp.height)
    }

    @Test
    fun `verify MemeEditor export to gallery via MediaStore for high quality JPEG and PNG`() = kotlinx.coroutines.runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val baseBmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val editorState = MemeEditorState(initialBitmap = baseBmp)
        editorState.addText(
            text = "HOT MEME CAPTION",
            xPercent = 0.5f,
            yPercent = 0.2f,
            fontSizeSp = 36f,
            fontType = MemeFontType.IMPACT,
            textColor = androidx.compose.ui.graphics.Color.Yellow,
            hasOutline = true,
            outlineColor = androidx.compose.ui.graphics.Color.Black,
            outlineWidth = 4f
        )

        // 1. Export as High-Quality JPEG to Public Gallery via MediaStore
        val jpegUri = editorState.exportToGallery(
            context = context,
            title = "Meme_Test_Export_JPEG",
            isPng = false,
            targetWidth = 600,
            targetHeight = 600
        )
        assertNotNull(jpegUri)
        assertTrue(jpegUri.toString().startsWith("content://media/external/images/media"))

        // Verify the saved JPEG in MediaStore can be read and decoded
        val jpegStream = context.contentResolver.openInputStream(jpegUri!!)
        assertNotNull(jpegStream)
        val decodedJpeg = android.graphics.BitmapFactory.decodeStream(jpegStream)
        assertNotNull(decodedJpeg)
        assertEquals(600, decodedJpeg.width)
        assertEquals(600, decodedJpeg.height)

        // 2. Export as Lossless PNG to Public Gallery via MediaStore
        val pngUri = editorState.exportToGallery(
            context = context,
            title = "Meme_Test_Export_PNG",
            isPng = true,
            targetWidth = 600,
            targetHeight = 600
        )
        assertNotNull(pngUri)
        assertTrue(pngUri.toString().startsWith("content://media/external/images/media"))

        val pngStream = context.contentResolver.openInputStream(pngUri!!)
        assertNotNull(pngStream)
        val decodedPng = android.graphics.BitmapFactory.decodeStream(pngStream)
        assertNotNull(decodedPng)
        assertEquals(600, decodedPng.width)
        assertEquals(600, decodedPng.height)
    }

    @Test
    fun `verify ImageFilterCarousel and FilterCategory definitions and ColorMatrix conversions`() {
        // 1. Check all filter presets in AVAILABLE_FILTER_PRESETS
        assertTrue(AVAILABLE_FILTER_PRESETS.isNotEmpty())
        val presetIds = AVAILABLE_FILTER_PRESETS.map { it.id }
        assertTrue(presetIds.contains("none"))
        assertTrue(presetIds.contains("cinematic"))
        assertTrue(presetIds.contains("vintage"))
        assertTrue(presetIds.contains("bw"))
        assertTrue(presetIds.contains("glow"))
        assertTrue(presetIds.contains("sepia"))
        assertTrue(presetIds.contains("vivid"))
        assertTrue(presetIds.contains("neon"))

        // 2. FilterCategory logic
        assertEquals("All", FilterCategory.ALL.label)
        assertTrue(FilterCategory.CINEMATIC.presetIds.contains("cinematic"))
        assertTrue(FilterCategory.CINEMATIC.presetIds.contains("vintage"))
        assertTrue(FilterCategory.MONOCHROME.presetIds.contains("bw"))
        assertTrue(FilterCategory.AESTHETIC.presetIds.contains("glow"))
        assertTrue(FilterCategory.VIBRANT.presetIds.contains("vivid"))

        // 3. ColorMatrix conversion from Android to Compose
        val androidMatrix = ImageProcessingUtils.createCinematicColorMatrix()
        assertNotNull(androidMatrix)
        assertEquals(20, androidMatrix.array.size)

        val composeMatrix = androidMatrix.toComposeColorMatrix()
        assertNotNull(composeMatrix)
        assertEquals(20, composeMatrix.values.size)
        assertEquals(androidMatrix.array[0], composeMatrix.values[0], 0.001f)
        assertEquals(androidMatrix.array[4], composeMatrix.values[4], 0.001f)

        // 4. Test comprehensive buildAdjustedColorMatrix
        val adjustedMatrix = ImageProcessingUtils.buildAdjustedColorMatrix(
            preset = "vintage",
            intensity = 0.8f,
            brightness = 10f,
            contrast = 1.2f,
            saturation = 1.1f,
            warmth = 5f
        )
        assertNotNull(adjustedMatrix)
        assertEquals(20, adjustedMatrix.array.size)

        val composeAdjusted = adjustedMatrix.toComposeColorMatrix()
        assertEquals(20, composeAdjusted.values.size)
    }

    @Test
    fun `verify dual photo collage frames and repository helper methods`() {
        val dualTemplates = com.example.data.model.CollageTemplateRepository.getDualPhotoTemplates()
        assertTrue("Must have at least 10 dual-photo frames", dualTemplates.size >= 10)

        // Verify specific dual frame IDs are present
        val dualIds = dualTemplates.map { it.id }
        assertTrue(dualIds.contains("2_side_by_side"))
        assertTrue(dualIds.contains("2_top_bottom"))
        assertTrue(dualIds.contains("2_hero_top"))
        assertTrue(dualIds.contains("2_magazine_split"))
        assertTrue(dualIds.contains("2_polaroid_duo"))
        assertTrue(dualIds.contains("2_staggered_cards"))
        assertTrue(dualIds.contains("2_portrait_duo"))
        assertTrue(dualIds.contains("2_cinema_letterbox"))
        assertTrue(dualIds.contains("2_before_after"))
        assertTrue(dualIds.contains("2_pip"))
        assertTrue(dualIds.contains("2_diagonal_split"))

        // Every dual template must have exactly 2 slots
        dualTemplates.forEach { tpl ->
            assertEquals(2, tpl.photoCount)
            assertEquals(2, tpl.slots.size)
            assertEquals(1, tpl.slots[0].id)
            assertEquals(2, tpl.slots[1].id)
        }
    }

    @Test
    fun `verify text quotes frame styles and quotes repository`() {
        val allQuotes = com.example.data.model.QuoteRepository.getAllQuotes()
        assertTrue("Must contain a rich library of quotes", allQuotes.size >= 25)

        // Verify Motivational Quotes exist and have content
        val motivationalQuotes = com.example.data.model.QuoteRepository.getQuotesByCategory(com.example.data.model.QuoteCategory.MOTIVATIONAL)
        assertTrue("Must have motivational quotes", motivationalQuotes.size >= 8)
        motivationalQuotes.forEach { q ->
            assertTrue(q.quote.isNotBlank())
            assertTrue(q.author.isNotBlank())
            assertEquals(com.example.data.model.QuoteCategory.MOTIVATIONAL, q.category)
        }

        // Verify Facts Quotes exist and have rich facts content
        val factsQuotes = com.example.data.model.QuoteRepository.getQuotesByCategory(com.example.data.model.QuoteCategory.FACTS)
        assertTrue("Must have science & nature facts quotes", factsQuotes.size >= 8)
        factsQuotes.forEach { q ->
            assertTrue(q.quote.isNotBlank())
            assertTrue(q.author.isNotBlank())
            assertEquals(com.example.data.model.QuoteCategory.FACTS, q.category)
        }

        // Verify other categories (Success, Wisdom, Life, Hindi Suvichar)
        val successQuotes = com.example.data.model.QuoteRepository.getQuotesByCategory(com.example.data.model.QuoteCategory.SUCCESS)
        assertTrue(successQuotes.isNotEmpty())

        val wisdomQuotes = com.example.data.model.QuoteRepository.getQuotesByCategory(com.example.data.model.QuoteCategory.WISDOM)
        assertTrue(wisdomQuotes.isNotEmpty())

        val hindiQuotes = com.example.data.model.QuoteRepository.getQuotesByCategory(com.example.data.model.QuoteCategory.HINDI_SUVICHAR)
        assertTrue(hindiQuotes.isNotEmpty())

        // Verify all 10 Quote Frame Styles exist
        val frameStyles = com.example.data.model.QuoteFrameStyle.values()
        assertEquals(10, frameStyles.size)
        val frameNames = frameStyles.map { it.name }
        assertTrue(frameNames.contains("MINIMAL_BORDER"))
        assertTrue(frameNames.contains("GOLD_LUXURY"))
        assertTrue(frameNames.contains("NEON_CYBER"))
        assertTrue(frameNames.contains("TYPEWRITER_VINTAGE"))
        assertTrue(frameNames.contains("CINEMATIC_LETTERBOX"))
        assertTrue(frameNames.contains("GLASS_CARD"))
        assertTrue(frameNames.contains("EDITORIAL_MAGAZINE"))
        assertTrue(frameNames.contains("POLAROID_PIN"))
        assertTrue(frameNames.contains("DOUBLE_RULE_CLASSIC"))
        assertTrue(frameNames.contains("ROUNDED_PILL"))

        // Verify Background Presets
        val bgPresets = com.example.data.model.QuoteRepository.BACKGROUND_PRESETS
        assertTrue(bgPresets.size >= 6)
        bgPresets.forEach { bg ->
            assertTrue(bg.id.isNotBlank())
            assertTrue(bg.name.isNotBlank())
            assertTrue(bg.colors.size >= 2)
        }

        // Verify random quote retrieval
        val randomQuote = com.example.data.model.QuoteRepository.getRandomQuote()
        assertNotNull(randomQuote)
        assertTrue(randomQuote.quote.isNotBlank())

        // Verify single quote retrieval by ID
        val quoteM1 = com.example.data.model.QuoteRepository.getQuoteById("m1")
        assertNotNull(quoteM1)
        assertEquals("Mark Twain", quoteM1?.author)

        val quoteF1 = com.example.data.model.QuoteRepository.getQuoteById("f1")
        assertNotNull(quoteF1)
        assertTrue(quoteF1?.quote?.contains("Honey") == true)
    }

    @Test
    fun `verify curated quote templates and export configurations`() {
        // 1. Verify Templates catalog
        val allTemplates = com.example.data.model.QuoteRepository.getAllTemplates()
        assertTrue("Must have rich curated quote templates", allTemplates.size >= 8)

        allTemplates.forEach { tpl ->
            assertTrue(tpl.id.isNotBlank())
            assertTrue(tpl.name.isNotBlank())
            assertTrue(tpl.quote.isNotBlank())
            assertTrue(tpl.author.isNotBlank())
            assertNotNull(tpl.frameStyle)
            assertNotNull(tpl.category)
            assertTrue(tpl.badgeIcon.isNotBlank())
            assertTrue(tpl.borderWidthDp > 0f)
            assertTrue(tpl.fontSizeSp > 0f)
        }

        // 2. Verify Template category filtering
        val motivationalTpls = com.example.data.model.QuoteRepository.getTemplatesByCategory(com.example.data.model.QuoteCategory.MOTIVATIONAL)
        assertTrue(motivationalTpls.isNotEmpty())

        val factsTpls = com.example.data.model.QuoteRepository.getTemplatesByCategory(com.example.data.model.QuoteCategory.FACTS)
        assertTrue(factsTpls.isNotEmpty())

        val hindiTpls = com.example.data.model.QuoteRepository.getTemplatesByCategory(com.example.data.model.QuoteCategory.HINDI_SUVICHAR)
        assertTrue(hindiTpls.isNotEmpty())

        // 3. Verify single template retrieval
        val tplAli = com.example.data.model.QuoteRepository.getTemplateById("tpl_cinema_ali")
        assertNotNull(tplAli)
        assertEquals("Muhammad Ali", tplAli?.author)
        assertEquals(com.example.data.model.QuoteFrameStyle.CINEMATIC_LETTERBOX, tplAli?.frameStyle)

        // 4. Verify aspect ratio resolutions for export
        val aspectRatios = listOf("1:1" to (1080 to 1080), "9:16" to (1080 to 1920), "4:5" to (1080 to 1350))
        aspectRatios.forEach { (ratio, dims) ->
            val (w, h) = when (ratio) {
                "9:16" -> 1080 to 1920
                "4:5" -> 1080 to 1350
                else -> 1080 to 1080
            }
            assertEquals(dims.first, w)
            assertEquals(dims.second, h)

            val exportBmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
            assertNotNull(exportBmp)
            assertEquals(w, exportBmp.width)
            assertEquals(h, exportBmp.height)

            val canvas = android.graphics.Canvas(exportBmp)
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 32f
            }
            canvas.drawText("Test Quote Export", 50f, 100f, paint)
            exportBmp.recycle()
        }
    }
}


