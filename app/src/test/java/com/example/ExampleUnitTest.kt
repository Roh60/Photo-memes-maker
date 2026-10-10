package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun imageDownsamplingHandlesPortraitLandscapeAndInvalidDimensions() {
    assertEquals(1, com.example.util.BitmapUtils.calculateInSampleSize(1200, 900, 1920))
    assertEquals(4, com.example.util.BitmapUtils.calculateInSampleSize(4000, 3000, 1920))
    assertEquals(8, com.example.util.BitmapUtils.calculateInSampleSize(12000, 2000, 1920))
    assertEquals(1, com.example.util.BitmapUtils.calculateInSampleSize(0, 1000, 1920))
    assertEquals(1, com.example.util.BitmapUtils.calculateInSampleSize(1000, 1000, 0))
  }

  @Test
  fun photoTemplateCatalogHasUniqueIdsAndValidVisualSettings() {
    val templates = com.example.data.model.TemplateRepository.getAllTemplates()
    assertTrue("Photo template catalog should not be empty", templates.isNotEmpty())
    assertEquals("Template IDs must be unique", templates.size, templates.map { it.id }.toSet().size)
    templates.forEach { template ->
      assertTrue("Template ${template.id} needs a name", template.name.isNotBlank())
      assertTrue("Template ${template.id} needs at least two gradient colors", template.gradientColors.size >= 2)
      assertTrue("Template ${template.id} needs a positive aspect ratio", template.aspectRatio > 0f)
    }
  }
}
