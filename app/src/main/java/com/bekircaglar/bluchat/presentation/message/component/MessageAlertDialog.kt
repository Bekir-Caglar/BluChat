package com.bekircaglar.bluchat.presentation.message.component

import ChatBubble
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bekircaglar.bluchat.domain.model.Message
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MessageAlertDialog(
    message: Message,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val timestamp = convertTimestampToDate(message.timestamp ?: 0)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete Message")
        },
        text = {
            Column {
               ChatBubble(
                     messageType = message.messageType ?: "",
                     message = message,
                     isSentByMe = true,
                     timestamp = timestamp,
                     senderName = "",
                     senderNameColor = MaterialTheme.colorScheme.primary,
                     onImageClick = {},
                     onEditClick = {},
                     onDeleteClick = {}
               )
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

fun convertTimestampToDate(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(formatter)
}