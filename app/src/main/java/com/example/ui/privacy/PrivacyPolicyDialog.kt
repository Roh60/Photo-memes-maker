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
                    text = "Your Privacy Matters",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• On-Device Editing: Photo editing and meme creation workflows are designed to process images on your device.\n" +
                            "• Photo Sharing: Images shared through Android system sharing may be handled by the app or service you select under its own privacy policy.\n" +
                            "• Saved Creations: Creations saved by the app are stored in its designated device storage. You can delete saved items using the available app controls.\n" +
                            "• Permissions: Camera access is requested when you choose to take a photo. Image selection uses Android photo picker where supported.\n" +
                            "• Network and Third-Party Services: Included libraries or future features may have different data practices. Network-connected behavior must be reviewed before making privacy guarantees.",
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
