package com.bekircaglar.bluchat.presentation.message.component

import android.provider.CalendarContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.IMAGE
import com.bekircaglar.bluchat.domain.model.Message

@Composable
fun MessageAlertDialog(
    message: Message,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete Message")
        },
        text = {
            Column {
                if (message.messageType == IMAGE) {
                    Column {
                        Image(
                            painter = rememberImagePainter(data = message.imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        message.message?.let { Text(text = it) }
                    }

                } else {
                    message.message?.let { Text(text = it) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Are you sure you want to delete this message?")
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Yes", color = MaterialTheme.colorScheme.background)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No", color = MaterialTheme.colorScheme.background)
            }
        }
    )
}