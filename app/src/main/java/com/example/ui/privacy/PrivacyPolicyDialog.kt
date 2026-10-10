package com.example.ui.privacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.PrivacyTip,
                contentDescription = "Privacy Policy",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text("Privacy & Local Storage Policy", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Your Privacy is 100% Protected",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• On-Device Processing: All photo filters, editing adjustments, festival templates, and meme creations are processed 100% locally on your device.\n" +
                            "• Zero Cloud Upload: Your personal photos, gallery selections, and camera captures are NEVER uploaded to any remote server or external cloud.\n" +
                            "• Local Storage Only: Saved artworks and local drafts remain securely stored in your phone's storage. You can delete individual items or clear all drafts anytime.\n" +
                            "• Standard Permissions: Camera and media picker permissions are requested solely when you choose to take a photo or select an image from your device.\n" +
                            "• Native Sharing: When you share an image, it uses Android's native system share sheet directly between your installed apps.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("privacy_dialog_ok_button")
            ) {
                Text("Got It")
            }
        }
    )
}
