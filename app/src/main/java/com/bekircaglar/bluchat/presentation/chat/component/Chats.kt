package com.bekircaglar.bluchat.presentation.chat.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.model.Chats
import com.bekircaglar.bluchat.presentation.chat.ChatViewModel
import com.bekircaglar.bluchat.presentation.message.MessageViewModel
import com.bekircaglar.bluchat.utils.GROUP
import com.bekircaglar.bluchat.utils.placeholder
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun Chats(
    chat: Chats,
    lastMessageSenderName: String? = "",
    onClick: () -> Unit,
    isSelected: Boolean = false,
    onImageLoaded: () -> Unit,
    currentUserId: String? = "",
) {
    val profileImage = chat.imageUrl
    val name = chat.name
    val surname = chat.surname
    val lastMessage = chat.lastMessage
    val messageTime = chat.messageTime
    val isOnline = chat.isOnline
    val lastMessageSender = chat.lastMessageSenderId




    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable {
                onClick()
            },
    ) {

        Box {
            val painter = rememberAsyncImagePainter(model = profileImage)
            val painterState = painter.state

            if (painterState is AsyncImagePainter.State.Loading) {
                Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            if (painterState is AsyncImagePainter.State.Success) {
                onImageLoaded()
            }

            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = if (chat.surname.isBlank()) Modifier
                    .size(50.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                else Modifier
                    .size(50.dp)
                    .clip(CircleShape)

            )



            if (isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.Green)
                        .align(Alignment.BottomEnd)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$name $surname",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            var myMessage = ""
            if (lastMessage != null) {
                if (chat.surname == "" && currentUserId == lastMessageSender) {
                    myMessage = "You: $lastMessage"
                }else if (chat.surname == ""){
                    myMessage = "$lastMessageSenderName: $lastMessage"
                }
                else myMessage = "$lastMessage"

                Text(
                    text = myMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            if (messageTime != null) {
                Text(
                    text = messageTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            }
        }

        if (isSelected)
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
    }
}
