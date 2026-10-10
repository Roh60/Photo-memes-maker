package com.example.ui.navigation

sealed class Screen {
    object Home : Screen()
    object PhotoStudio : Screen()
    object TemplatesCatalog : Screen()
    data class TemplateEditor(val templateId: String) : Screen()
    object TwoPhotoFrame : Screen()
    data class CollageStudio(val templateId: String? = null) : Screen()
    object MemesCatalog : Screen()
    data class MemeStudio(val templateId: String? = null) : Screen()
    object MyCreations : Screen()
    object DailyGreetings : Screen()
    data class CameraScreen(val returnTarget: String = "photo_studio") : Screen()
}

enum class MainTab {
    HOME,
    PHOTO_MAKER,
    MEMES_MAKER,
    CREATIONS
}
