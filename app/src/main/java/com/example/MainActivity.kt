package com.example

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.creations.MyCreationsScreen
import com.example.ui.greetings.DailyGreetingsScreen
import com.example.ui.home.HomeScreen
import com.example.ui.mememaker.MemeStudioScreen
import com.example.ui.mememaker.MemeTemplatesCatalogScreen
import com.example.ui.navigation.MainTab
import com.example.ui.navigation.Screen
import com.example.ui.photomaker.CollageStudioScreen
import com.example.ui.photomaker.PhotoStudioScreen
import com.example.ui.photomaker.TemplateEditorScreen
import com.example.ui.camera.CameraCaptureScreen
import com.example.ui.photomaker.TemplatesCatalogScreen
import com.example.ui.photomaker.TwoPhotoFrameScreen
import com.example.ui.quotesmaker.QuotesMakerScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer() {
    // Screen navigation stack
    val screenStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val currentScreen = screenStack.lastOrNull() ?: Screen.Home

    // Active bottom navigation tab
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }

    // Staging bitmap for PhotoStudio
    var stagedStudioBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var stagedCollageBitmaps by remember { mutableStateOf<List<Bitmap>?>(null) }
    var stagedCollageTemplateId by remember { mutableStateOf<String?>("2_side_by_side") }

    fun navigateTo(screen: Screen) {
        screenStack.add(screen)
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
        } else {
            // Reset to Home
            screenStack.clear()
            screenStack.add(Screen.Home)
            selectedTab = MainTab.HOME
        }
    }

    fun selectTab(tab: MainTab) {
        selectedTab = tab
        screenStack.clear()
        when (tab) {
            MainTab.HOME -> screenStack.add(Screen.Home)
            MainTab.PHOTO_MAKER -> screenStack.add(Screen.TemplatesCatalog)
            MainTab.MEMES_MAKER -> screenStack.add(Screen.MemesCatalog)
            MainTab.CREATIONS -> screenStack.add(Screen.MyCreations)
        }
    }

    // Determine whether to show bottom bar (show on root tabs, hide inside deep editors for max canvas space)
    val showBottomBar = currentScreen is Screen.Home ||
            currentScreen is Screen.TemplatesCatalog ||
            currentScreen is Screen.MemesCatalog ||
            currentScreen is Screen.MyCreations

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == MainTab.HOME && currentScreen is Screen.Home,
                        onClick = { selectTab(MainTab.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_item_home")
                    )
                    NavigationBarItem(
                        selected = selectedTab == MainTab.PHOTO_MAKER || currentScreen is Screen.TemplatesCatalog || currentScreen is Screen.PhotoStudio || currentScreen is Screen.TwoPhotoFrame || currentScreen is Screen.CollageStudio,
                        onClick = { selectTab(MainTab.PHOTO_MAKER) },
                        icon = { Icon(Icons.Default.AutoFixHigh, contentDescription = "Photo Maker") },
                        label = { Text("Photo Maker") },
                        modifier = Modifier.testTag("nav_item_photo_maker")
                    )
                    NavigationBarItem(
                        selected = selectedTab == MainTab.MEMES_MAKER || currentScreen is Screen.MemesCatalog || currentScreen is Screen.MemeStudio,
                        onClick = { selectTab(MainTab.MEMES_MAKER) },
                        icon = { Icon(Icons.Default.SentimentSatisfiedAlt, contentDescription = "Memes Maker") },
                        label = { Text("Memes") },
                        modifier = Modifier.testTag("nav_item_memes_maker")
                    )
                    NavigationBarItem(
                        selected = selectedTab == MainTab.CREATIONS && currentScreen is Screen.MyCreations,
                        onClick = { selectTab(MainTab.CREATIONS) },
                        icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "My Creations") },
                        label = { Text("Creations") },
                        modifier = Modifier.testTag("nav_item_creations")
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    is Screen.Home -> {
                        HomeScreen(
                            onOpenPhotoStudioWithBitmap = { bmp ->
                                stagedStudioBitmap = bmp
                                navigateTo(Screen.PhotoStudio)
                            },
                            onLaunchCamera = { navigateTo(Screen.CameraScreen("photo_studio")) },
                            onOpenTwoPhotoFrame = {
                                stagedCollageTemplateId = "2_side_by_side"
                                stagedCollageBitmaps = null
                                navigateTo(Screen.TwoPhotoFrame)
                            },
                            onOpenCollageWithTemplate = { tplId ->
                                stagedCollageTemplateId = tplId
                                stagedCollageBitmaps = null
                                navigateTo(Screen.CollageStudio(tplId))
                            },
                            onOpenCollageWithBitmaps = { bmps, tplId ->
                                stagedCollageBitmaps = bmps
                                stagedCollageTemplateId = tplId ?: "2_side_by_side"
                                navigateTo(Screen.CollageStudio(tplId ?: "2_side_by_side", bmps))
                            },
                            onOpenTemplate = { tplId -> navigateTo(Screen.TemplateEditor(tplId)) },
                            onOpenTemplatesCatalog = { navigateTo(Screen.TemplatesCatalog) },
                            onOpenMemeStudio = { memeId -> navigateTo(Screen.MemeStudio(memeId)) },
                            onOpenMemesCatalog = { navigateTo(Screen.MemesCatalog) },
                            onOpenCreations = { selectTab(MainTab.CREATIONS) },
                            onOpenQuotesMaker = { qId, fId, tplId -> navigateTo(Screen.QuotesMaker(qId, fId, tplId)) },
                            onOpenDailyGreetings = { navigateTo(Screen.DailyGreetings) }
                        )
                    }

                    is Screen.PhotoStudio -> {
                        PhotoStudioScreen(
                            initialBitmap = stagedStudioBitmap,
                            onLaunchCamera = { navigateTo(Screen.CameraScreen("photo_studio")) },
                            onNavigateBack = {
                                stagedStudioBitmap = null
                                navigateBack()
                            }
                        )
                    }

                    is Screen.TemplatesCatalog -> {
                        TemplatesCatalogScreen(
                            onSelectTemplate = { tplId -> navigateTo(Screen.TemplateEditor(tplId)) },
                            onNavigateBack = { selectTab(MainTab.HOME) }
                        )
                    }

                    is Screen.TemplateEditor -> {
                        TemplateEditorScreen(
                            templateId = targetScreen.templateId,
                            onNavigateBack = { navigateBack() }
                        )
                    }

                    is Screen.TwoPhotoFrame -> {
                        CollageStudioScreen(
                            initialTemplateId = stagedCollageTemplateId ?: "2_side_by_side",
                            initialBitmaps = stagedCollageBitmaps,
                            onNavigateBack = {
                                stagedCollageBitmaps = null
                                stagedCollageTemplateId = null
                                navigateBack()
                            }
                        )
                    }

                    is Screen.CollageStudio -> {
                        CollageStudioScreen(
                            initialTemplateId = targetScreen.templateId ?: stagedCollageTemplateId ?: "2_side_by_side",
                            initialBitmaps = targetScreen.initialBitmaps ?: stagedCollageBitmaps,
                            onNavigateBack = {
                                stagedCollageBitmaps = null
                                stagedCollageTemplateId = null
                                navigateBack()
                            }
                        )
                    }

                    is Screen.MemesCatalog -> {
                        MemeTemplatesCatalogScreen(
                            onSelectTemplate = { memeId -> navigateTo(Screen.MemeStudio(memeId)) },
                            onCreateBlankMeme = { navigateTo(Screen.MemeStudio(null)) },
                            onNavigateBack = { selectTab(MainTab.HOME) }
                        )
                    }

                    is Screen.MemeStudio -> {
                        MemeStudioScreen(
                            templateId = targetScreen.templateId,
                            onNavigateBack = { navigateBack() }
                        )
                    }

                    is Screen.MyCreations -> {
                        MyCreationsScreen(
                            onResumeDraft = { draft ->
                                if (draft.type == "meme") {
                                    navigateTo(Screen.MemeStudio(null))
                                } else {
                                    navigateTo(Screen.PhotoStudio)
                                }
                            },
                            onNavigateBack = { selectTab(MainTab.HOME) }
                        )
                    }

                    is Screen.DailyGreetings -> {
                        DailyGreetingsScreen(onNavigateBack = { navigateBack() })
                    }

                    is Screen.QuotesMaker -> {
                        val initialFrame = targetScreen.initialFrameId?.let { id ->
                            try {
                                com.example.data.model.QuoteFrameStyle.valueOf(id)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        QuotesMakerScreen(
                            initialQuoteId = targetScreen.initialQuoteId,
                            initialFrameStyle = initialFrame,
                            initialTemplateId = targetScreen.initialTemplateId,
                            onNavigateBack = { navigateBack() }
                        )
                    }

                    is Screen.CameraScreen -> {
                        CameraCaptureScreen(
                            onPhotoCaptured = { capturedBmp ->
                                stagedStudioBitmap = capturedBmp
                                if (screenStack.size > 1) {
                                    screenStack.removeAt(screenStack.lastIndex)
                                }
                                val top = screenStack.lastOrNull()
                                if (top !is Screen.PhotoStudio) {
                                    navigateTo(Screen.PhotoStudio)
                                }
                            },
                            onNavigateBack = { navigateBack() }
                        )
                    }
                }
            }
        }
    }
}
