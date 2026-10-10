package com.example.ui.home

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MemeTemplateRepository
import com.example.data.model.TemplateRepository
import com.example.data.repository.CreationsRepository
import com.example.ui.privacy.PrivacyPolicyDialog
import com.example.util.BitmapUtils
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenPhotoStudioWithBitmap: (Bitmap?) -> Unit,
    onLaunchCamera: () -> Unit,
    onOpenTwoPhotoFrame: () -> Unit,
    onOpenTemplate: (String) -> Unit,
    onOpenTemplatesCatalog: () -> Unit,
    onOpenMemeStudio: (String?) -> Unit,
    onOpenMemesCatalog: () -> Unit,
    onOpenCreations: () -> Unit,
    onOpenDailyGreetings: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { CreationsRepository(context) }
    val creations by repository.allCreations.collectAsState(initial = emptyList())
    var showPrivacyDialog by remember { mutableStateOf(false) }

    // Direct Camera and Gallery launchers from Home Screen
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val bmp = BitmapUtils.decodeUriToBitmap(context, uri)
                onOpenPhotoStudioWithBitmap(bmp)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            onOpenPhotoStudioWithBitmap(bmp)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Studio Header
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✨", fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Photo & Memes Maker",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Photo & Meme Creator",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            actions = {
                IconButton(
                    onClick = { showPrivacyDialog = true },
                    modifier = Modifier.testTag("home_privacy_btn")
                ) {
                    Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = "Privacy Policy")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        // Hero Quick Actions Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFFEC4899))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Creative Studio",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "Design in Seconds",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text("🎨", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Prominent Quick Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onLaunchCamera,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("home_take_photo_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF4F46E5)
                            )
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Take Photo", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("home_choose_gallery_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.25f),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gallery", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4 Core Hub Action Tiles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HomeHubTile(
                title = "Photo Studio",
                subtitle = "Filters & Edits",
                emoji = "🪄",
                bgColor = 0xFF6366F1,
                onClick = { onOpenPhotoStudioWithBitmap(null) },
                modifier = Modifier.weight(1f).testTag("tile_photo_studio")
            )
            HomeHubTile(
                title = "Collage Maker",
                subtitle = "2-6 Photo Grids",
                emoji = "🖼️",
                bgColor = 0xFFEC4899,
                onClick = onOpenTwoPhotoFrame,
                modifier = Modifier.weight(1f).testTag("tile_two_frame")
            )
            HomeHubTile(
                title = "Meme Creator",
                subtitle = "Punchlines",
                emoji = "😂",
                bgColor = 0xFFF59E0B,
                onClick = { onOpenMemeStudio(null) },
                modifier = Modifier.weight(1f).testTag("tile_meme_studio")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Added in the next feature step: personalized daily greeting maker.
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable { onOpenDailyGreetings() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(Color(0xFFFF8A65), Color(0xFFEC4899)))), contentAlignment = Alignment.Center) {
                    Text("🎉", fontSize = 28.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Daily Greetings & Quotes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Add your name/photo and share beautiful wishes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Open greetings")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Featured Festival Templates Section
        SectionHeader(
            title = "🎉 Festival Celebrations",
            actionText = "See All (${TemplateRepository.FESTIVAL_TEMPLATES.size})",
            onAction = onOpenTemplatesCatalog
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TemplateRepository.FESTIVAL_TEMPLATES.take(6).forEach { tpl ->
                HomeTemplateCarouselCard(
                    title = tpl.name,
                    category = tpl.subCategory,
                    emoji = tpl.decorativeEmoji,
                    gradientColors = tpl.gradientColors,
                    onClick = { onOpenTemplate(tpl.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Trending Stylish Photo Frames Section
        SectionHeader(
            title = "✨ Trending Stylish Frames",
            actionText = "Explore",
            onAction = onOpenTemplatesCatalog
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TemplateRepository.STYLISH_TEMPLATES.take(6).forEach { tpl ->
                HomeTemplateCarouselCard(
                    title = tpl.name,
                    category = tpl.subCategory,
                    emoji = tpl.decorativeEmoji,
                    gradientColors = tpl.gradientColors,
                    onClick = { onOpenTemplate(tpl.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Popular Meme Formats Section
        SectionHeader(
            title = "🔥 Popular Meme Formats",
            actionText = "All Formats",
            onAction = onOpenMemesCatalog
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MemeTemplateRepository.TEMPLATES.take(5).forEach { meme ->
                HomeMemeCarouselCard(
                    title = meme.title,
                    topText = meme.defaultTopText,
                    category = meme.category,
                    graphicType = meme.sampleGraphicType,
                    onClick = { onOpenMemeStudio(meme.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Creations Banner (if user has saved items)
        if (creations.isNotEmpty()) {
            SectionHeader(
                title = "📂 Recent Projects",
                actionText = "View All",
                onAction = onOpenCreations
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                creations.take(4).forEach { item ->
                    ElevatedCard(
                        modifier = Modifier
                            .width(130.dp)
                            .clickable { onOpenCreations() },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            AsyncImage(
                                model = File(item.filePath),
                                contentDescription = item.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                            )
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Monetization Banner (Creator Pro)

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showPrivacyDialog) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
    }
}

@Composable
fun HomeHubTile(
    title: String,
    subtitle: String,
    emoji: String,
    bgColor: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Color(bgColor).copy(alpha = 0.15f),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(emoji, fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onAction) {
            Text(actionText, style = MaterialTheme.typography.labelMedium)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun HomeTemplateCarouselCard(
    title: String,
    category: String,
    emoji: String,
    gradientColors: List<Long>,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
            .testTag("home_tpl_card_$category"),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(gradientColors.first()), Color(gradientColors.last()))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(emoji, fontSize = 22.sp)
                    }
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun HomeMemeCarouselCard(
    title: String,
    topText: String,
    category: String,
    graphicType: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
            .testTag("home_meme_card_$title"),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (graphicType) {
                        "smirk" -> "😎"
                        "shock" -> "😱"
                        "smart" -> "🧠"
                        else -> "😂"
                    },
                    fontSize = 32.sp
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
