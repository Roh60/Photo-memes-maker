package com.example.ui.greetings

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.util.BitmapUtils
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

private data class GreetingDesign(val title: String, val message: String, val category: String, val emoji: String, val colors: List<Color>)
private val greetingDesigns = listOf(
    GreetingDesign("Good Morning", "A new day, a new hope. Have a beautiful day!", "Daily", "☀️", listOf(Color(0xFFFF9A44), Color(0xFFFFD36E))),
    GreetingDesign("Stay Positive", "Small steps every day lead to big changes.", "Quotes", "🌈", listOf(Color(0xFF5146E5), Color(0xFFB14AED))),
    GreetingDesign("Happy Birthday", "May your day be full of joy, love and laughter!", "Celebrations", "🎂", listOf(Color(0xFFFF5C93), Color(0xFFFFB36B))),
    GreetingDesign("Festival Wishes", "Wishing you happiness, peace and prosperity.", "Festivals", "🪔", listOf(Color(0xFF087F5B), Color(0xFF65C18C))),
    GreetingDesign("Good Night", "Rest well and wake up with a grateful heart.", "Daily", "🌙", listOf(Color(0xFF142850), Color(0xFF4B6CB7))),
    GreetingDesign("Friendship", "Life is better with friends like you.", "Quotes", "💖", listOf(Color(0xFFB83280), Color(0xFFED64A6)))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGreetingsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedDesign by remember { mutableStateOf(greetingDesigns.first()) }
    var selectedCategory by remember { mutableStateOf("All") }
    var showShareError by remember { mutableStateOf(false) }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { selectedUri = it }

    Scaffold(topBar = { TopAppBar(title = { Text("Daily Greetings Studio", fontWeight = FontWeight.Bold) }, navigationIcon = {
        IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
    }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(bottom = 20.dp)) {
            Text("Make it personal. Share the good vibes.", Modifier.padding(horizontal = 18.dp, vertical = 8.dp), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(3.dp)) {
                Box(Modifier.fillMaxWidth().background(Brush.linearGradient(selectedDesign.colors)).padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(selectedDesign.emoji, fontSize = 38.sp)
                        if (selectedUri != null) AsyncImage(model = selectedUri, contentDescription = "Selected photo", modifier = Modifier.size(104.dp).clip(CircleShape))
                        Text(selectedDesign.title, color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                        Text(selectedDesign.message, color = Color.White, fontSize = 15.sp, textAlign = TextAlign.Center)
                        if (userName.isNotBlank()) Text("With love, ${userName.trim()}", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text("Made with Photo & Memes Maker", color = Color.White.copy(alpha = .82f), fontSize = 10.sp)
                    }
                }
            }
            OutlinedTextField(value = userName, onValueChange = { userName = it.take(40) }, label = { Text("Your name (optional)") }, placeholder = { Text("Enter name for personalization") }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp))
            Button(onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null); Spacer(Modifier.width(8.dp)); Text(if (selectedUri == null) "Add Your Photo (Optional)" else "Change Photo")
            }
            Text("Choose a category", Modifier.padding(start = 18.dp, top = 18.dp, bottom = 8.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Daily", "Quotes", "Festivals", "Celebrations").forEach { category ->
                    val active = selectedCategory == category
                    Card(Modifier.clickable { selectedCategory = category }, shape = RoundedCornerShape(50), colors = CardDefaults.cardColors(containerColor = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)) {
                        Text(category, Modifier.padding(horizontal = 16.dp, vertical = 10.dp), color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Text("Greeting templates", Modifier.padding(start = 18.dp, top = 18.dp, bottom = 8.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            greetingDesigns.filter { selectedCategory == "All" || it.category == selectedCategory }.chunked(2).forEach { rowDesigns ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowDesigns.forEach { design ->
                        val active = selectedDesign.title == design.title
                        Card(Modifier.weight(1f).clickable { selectedDesign = design }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
                            Column(Modifier.fillMaxWidth().background(Brush.linearGradient(design.colors)).padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(design.emoji, fontSize = 28.sp); Text(design.title, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center); Text(if (active) "✓ Selected" else "Tap to use", color = Color.White.copy(alpha = .9f), fontSize = 11.sp)
                            }
                        }
                    }
                    if (rowDesigns.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            Button(onClick = {
                showShareError = false
                scope.launch { try { val photo = selectedUri?.let { BitmapUtils.decodeUriToBitmap(context, it, 1000) }; shareGreetingImage(context, selectedDesign, userName.trim(), photo) } catch (_: Exception) { showShareError = true } }
            }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
                Icon(Icons.Default.Share, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Create & Share Greeting")
            }
            if (showShareError) Text("Could not create the greeting. Please try again.", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 18.dp))
        }
    }
}

private fun shareGreetingImage(context: Context, design: GreetingDesign, name: String, photo: Bitmap?) {
    val width = 1080; val height = 1350
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); val canvas = Canvas(bitmap); val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val colors = design.colors.map { it.toArgbInt() }
    for (y in 0 until height) { paint.color = blend(colors[0], colors[1], y.toFloat() / height); canvas.drawRect(0f, y.toFloat(), width.toFloat(), y + 1f, paint) }
    paint.color = 0x33FFFFFF; canvas.drawCircle(100f, 110f, 170f, paint); canvas.drawCircle(1010f, 1240f, 250f, paint)
    if (photo != null && photo.width > 0 && photo.height > 0) {
        val side = minOf(photo.width, photo.height); val left = (photo.width - side) / 2; val top = (photo.height - side) / 2
        val source = android.graphics.Rect(left, top, left + side, top + side); val dest = RectF(325f, 220f, 755f, 650f)
        canvas.save(); canvas.clipPath(android.graphics.Path().apply { addRoundRect(dest, 28f, 28f, android.graphics.Path.Direction.CW) }); canvas.drawBitmap(photo, source, dest, paint); canvas.restore()
    }
    paint.color = android.graphics.Color.WHITE; paint.textAlign = Paint.Align.CENTER; paint.typeface = Typeface.create("sans-serif", Typeface.BOLD); paint.textSize = 76f
    val titleY = if (photo == null) 510f else 770f; drawWrappedText(canvas, design.title, width / 2f, titleY, 900f, paint, 88f)
    paint.typeface = Typeface.create("sans-serif", Typeface.NORMAL); paint.textSize = 40f; drawWrappedText(canvas, design.message, width / 2f, titleY + 145f, 850f, paint, 56f)
    if (name.isNotBlank()) { paint.typeface = Typeface.create("sans-serif", Typeface.BOLD); paint.textSize = 42f; canvas.drawText("With love, $name", width / 2f, 1110f, paint) }
    paint.typeface = Typeface.create("sans-serif", Typeface.NORMAL); paint.textSize = 24f; paint.alpha = 220; canvas.drawText("Made with Photo & Memes Maker", width / 2f, 1280f, paint)
    val shareDir = File(context.cacheDir, "shared_images").apply { mkdirs() }; val output = File(shareDir, "greeting_${System.currentTimeMillis()}.jpg")
    FileOutputStream(output).use { check(bitmap.compress(Bitmap.CompressFormat.JPEG, 94, it)) { "Image encoding failed" } }; bitmap.recycle()
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", output)
    val intent = Intent(Intent.ACTION_SEND).apply { type = "image/jpeg"; putExtra(Intent.EXTRA_STREAM, uri); putExtra(Intent.EXTRA_TEXT, "${design.title} — made with Photo & Memes Maker"); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
    context.startActivity(Intent.createChooser(intent, "Share your greeting"))
}

private fun drawWrappedText(canvas: Canvas, text: String, centerX: Float, startY: Float, maxWidth: Float, paint: Paint, lineHeight: Float) {
    val lines = mutableListOf<String>(); var line = ""
    text.split(" ").forEach { word -> val candidate = if (line.isEmpty()) word else "$line $word"; if (paint.measureText(candidate) > maxWidth && line.isNotEmpty()) { lines.add(line); line = word } else line = candidate }
    if (line.isNotEmpty()) lines.add(line); lines.forEachIndexed { index, value -> canvas.drawText(value, centerX, startY + index * lineHeight, paint) }
}
private fun blend(start: Int, end: Int, fraction: Float): Int { val f = fraction.coerceIn(0f, 1f); val r = (android.graphics.Color.red(start) * (1 - f) + android.graphics.Color.red(end) * f).toInt(); val g = (android.graphics.Color.green(start) * (1 - f) + android.graphics.Color.green(end) * f).toInt(); val b = (android.graphics.Color.blue(start) * (1 - f) + android.graphics.Color.blue(end) * f).toInt(); return android.graphics.Color.rgb(r, g, b) }
private fun Color.toArgbInt(): Int = android.graphics.Color.rgb((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())
