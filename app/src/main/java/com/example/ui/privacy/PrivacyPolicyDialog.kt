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
            Text("Privacy & Local Storage", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Your Privacy Matters",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Local Storage: Your photos, drafts and saved creations are stored only on your phone. The app has no internet permission and does not upload anything to any server.\n" +
                            "• On-Device Editing: Photo editing and meme creation are done on your device.\n" +
                            "• Saved Creations: Saved items stay in the app's storage and exports go to Pictures/PhotoMemesMaker. You can delete them anytime from My Creations or your gallery.\n" +
                            "• Permissions: Camera access is requested only when you choose to take a photo. Image selection uses the Android photo picker.\n" +
                            "• Sharing: When you share an image, it goes through Android's share sheet to the app you choose, which has its own privacy policy.",
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
