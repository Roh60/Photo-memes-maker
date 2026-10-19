package com.example.ui.navigation

sealed class Screen {
    object Home : Screen()
    object PhotoStudio : Screen()
    object TemplatesCatalog : Screen()
    data class TemplateEditor(val templateId: String) : Screen()
    object TwoPhotoFrame : Screen()
    data class CollageStudio(val templateId: String? = null, val initialBitmaps: List<android.graphics.Bitmap>? = null) : Screen()
    object MemesCatalog : Screen()
    data class MemeStudio(val templateId: String? = null) : Screen()
    object MyCreations : Screen()
    object DailyGreetings : Screen()
    data class CameraScreen(val returnTarget: String = "photo_studio") : Screen()
    data class QuotesMaker(
        val initialQuoteId: String? = null,
        val initialFrameId: String? = null,
        val initialTemplateId: String? = null
    ) : Screen()
}

enum class MainTab {
    HOME,
    PHOTO_MAKER,
    MEMES_MAKER,
    CREATIONS
}
