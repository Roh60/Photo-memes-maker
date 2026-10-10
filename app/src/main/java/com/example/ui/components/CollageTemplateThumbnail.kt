package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CollageTemplate

/**
 * Miniature canvas card previewing a collage template's slot structure.
 */
@Composable
fun CollageTemplateThumbnail(
    template: CollageTemplate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(88.dp)
            .clickable { onClick() }
            .testTag("collage_tpl_${template.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        }
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Miniature slot canvas
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .padding(3.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val gap = 2.5f

                    template.slots.forEachIndexed { index, slot ->
                        val slotL = slot.left * w + (if (slot.left > 0f) gap / 2f else 0f)
                        val slotT = slot.top * h + (if (slot.top > 0f) gap / 2f else 0f)
                        val slotR = slot.right * w - (if (slot.right < 1f) gap / 2f else 0f)
                        val slotB = slot.bottom * h - (if (slot.bottom < 1f) gap / 2f else 0f)

                        val slotW = (slotR - slotL).coerceAtLeast(1f)
                        val slotH = (slotB - slotT).coerceAtLeast(1f)

                        // Distinct muted color for each slot
                        val slotColor = when (index % 5) {
                            0 -> Color(0xFF6366F1)
                            1 -> Color(0xFFEC4899)
                            2 -> Color(0xFF10B981)
                            3 -> Color(0xFFF59E0B)
                            else -> Color(0xFF8B5CF6)
                        }

                        drawRoundRect(
                            color = slotColor.copy(alpha = 0.85f),
                            topLeft = Offset(slotL, slotT),
                            size = Size(slotW, slotH),
                            cornerRadius = CornerRadius(4f, 4f)
                        )

                        // Subtle inner outline
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.4f),
                            topLeft = Offset(slotL, slotT),
                            size = Size(slotW, slotH),
                            cornerRadius = CornerRadius(4f, 4f),
                            style = Stroke(width = 1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = template.name,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${template.photoCount} Photos",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
