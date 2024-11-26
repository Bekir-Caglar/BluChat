package com.bekircaglar.bluchat.presentation.message.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.ui.theme.ChatAppBordoTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBottomBar(
    onAttachClicked: () -> Unit,
    onCameraClicked: () -> Unit,
    onEmojiClicked: () -> Unit,
    sendMessage: (String) -> Unit,
) {
    var chatMessage by remember { mutableStateOf("") }
    val buttonTypeText = remember { mutableStateOf(false) }
    buttonTypeText.value = chatMessage.isNotEmpty()
    BottomAppBar(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            MessageTextField(
                searchText = chatMessage,
                onSearchTextChange = { newText -> chatMessage = newText },
                onSend = {
                    if (chatMessage.isNotEmpty()) {
                        sendMessage(chatMessage)
                        chatMessage = ""
                    }
                },
                onEmojiClicked = { onEmojiClicked() },
                onCameraClicked = { onCameraClicked() },
                onAttachClicked = { onAttachClicked() },
                placeholderText = "Type a message", modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = {
                },
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
                    .combinedClickable(
                        onLongClick = {

                        },
                        onClick = {
                        if (buttonTypeText.value) {
                            sendMessage(chatMessage)
                            chatMessage = ""
                        }
                    })
            ) {
                AnimatedContent(
                    targetState = buttonTypeText.value,
                    transitionSpec = {
                        scaleIn(animationSpec = tween(600)) togetherWith  scaleOut(
                            animationSpec = tween(600)
                        )
                    }
                ) { targetState ->
                    Icon(
                        painter = if (targetState) painterResource(R.drawable.ic_send)
                        else painterResource(R.drawable.outline_mic_none_24),
                        contentDescription = "More",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Preview
@Composable
fun MessageBottomBarPreview() {
    ChatAppBordoTheme {
        MessageBottomBar(
            onAttachClicked = {},
            sendMessage = {},
            onCameraClicked = {},
            onEmojiClicked = {}
        )
    }
}
