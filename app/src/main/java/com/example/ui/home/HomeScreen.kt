package com.example.ui.home

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.annotation.DrawableRes
import androidx.compose.ui.res.painterResource
import com.example.data.model.StickerLibrary
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Refresh
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
import com.example.data.model.CollageTemplate
import com.example.data.model.CollageTemplateRepository
import com.example.data.model.MemeTemplateRepository
import com.example.data.model.QuoteCategory
import com.example.data.model.QuoteFrameStyle
import com.example.data.model.QuoteItem
import com.example.data.model.QuoteRepository
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
    onOpenCollageWithTemplate: (String) -> Unit = {},
    onOpenCollageWithBitmaps: (List<Bitmap>, String?) -> Unit = { _, _ -> },
    onOpenTemplate: (String) -> Unit,
    onOpenTemplatesCatalog: () -> Unit,
    onOpenMemeStudio: (String?) -> Unit,
    onOpenMemesCatalog: () -> Unit,
    onOpenCreations: () -> Unit,
    onOpenQuotesMaker: (quoteId: String?, frameId: String?, templateId: String?) -> Unit = { _, _, _ -> },
    onOpenDailyGreetings: () -> Unit = {}
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeHubTile(
                    title = "Meme Creator",
                    subtitle = "Punchlines & Humor",
                    emoji = "😂",
                    bgColor = 0xFFF59E0B,
                    onClick = onOpenMemesCatalog,
                    modifier = Modifier.weight(1f).testTag("tile_meme_studio")
                )
                HomeHubTile(
                    title = "Quotes Maker",
                    subtitle = "Frames & Thoughts",
                    emoji = "✍️",
                    bgColor = 0xFF10B981,
                    onClick = { onOpenQuotesMaker(null, null, null) },
                    modifier = Modifier.weight(1f).testTag("tile_quotes_studio")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        // VIP & Premium Photo Templates Section
        SectionHeader(
            title = "👑 VIP & Premium Photo Templates",
            actionText = "See All (${TemplateRepository.PREMIUM_TEMPLATES.size})",
            onAction = onOpenTemplatesCatalog
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TemplateRepository.PREMIUM_TEMPLATES.forEach { tpl ->
                HomeTemplateCarouselCard(
                    title = tpl.name,
                    category = tpl.subCategory,
                    emoji = tpl.decorativeEmoji,
                    gradientColors = tpl.gradientColors,
                    isPremium = true,
                    onClick = { onOpenTemplate(tpl.id) }
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
    isPremium: Boolean = false,
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
                if (isPremium) {
                    Surface(
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        color = Color(0xFFD97706),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "👑 PRO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
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

@Composable
fun HomeStickerCarouselCard(
    name: String,
    category: String,
    @DrawableRes resId: Int,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
            .testTag("home_sticker_card_$name"),
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
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = resId),
                        contentDescription = name,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun HomeDualFrameCarouselCard(
    template: CollageTemplate,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .width(136.dp)
            .clickable { onClick() }
            .testTag("home_dual_tpl_${template.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp, 75.dp)
                    .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                    .padding(5.dp)
            ) {
                // Miniature preview slots
                template.slots.forEach { slot ->
                    val slotColor = if (slot.id == 1) Color(0xFF6366F1).copy(alpha = 0.9f) else Color(0xFFEC4899).copy(alpha = 0.9f)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = (slot.left * 90).dp,
                                top = (slot.top * 65).dp,
                                end = ((1f - slot.right) * 90).dp,
                                bottom = ((1f - slot.bottom) * 65).dp
                            )
                            .background(slotColor, RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${slot.id}",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = template.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HomeQuoteFrameCard(
    style: QuoteFrameStyle,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
            .testTag("home_quote_frame_${style.name}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp, 65.dp)
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .padding(6.dp)
                    .border(1.5.dp, Color(0xFFD4AF37), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("“ ”", fontSize = 18.sp, color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = style.displayName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Frame Style",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun HomeQuoteItemCard(
    quoteItem: QuoteItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(115.dp)
            .clickable { onClick() }
            .testTag("home_quote_item_${quoteItem.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${quoteItem.badgeIcon} ${quoteItem.category.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text("“", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.Gray)
            }
            Text(
                text = quoteItem.quote,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "— ${quoteItem.author}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HomeQuoteTemplateCard(
    template: com.example.data.model.QuoteTemplate,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(115.dp)
            .clickable { onClick() }
            .testTag("home_quote_template_${template.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${template.badgeIcon} ${template.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = template.frameStyle.displayName.take(12),
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = "“${template.quote}”",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "— ${template.author}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Use ⚡",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


