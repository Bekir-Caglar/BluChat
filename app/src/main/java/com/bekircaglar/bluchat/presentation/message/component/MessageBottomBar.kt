package com.bekircaglar.bluchat.presentation.message.component

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.utils.conditionalPointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBottomBar(
    onAttachClicked: () -> Unit,
    onCameraClicked: () -> Unit,
    sendMessage: (String) -> Unit,
) {
    val context = LocalContext.current
    var chatMessage by remember { mutableStateOf("") }
    val buttonTypeText = remember { mutableStateOf(false) }
    buttonTypeText.value = chatMessage.isNotEmpty()
    var emojiState by remember { mutableStateOf(false) }
    var counter by remember { mutableStateOf(0) }
    val primaryColor = MaterialTheme.colorScheme.primary

    var offsetX by remember { mutableStateOf(0f) }
    var buttonSize by remember { mutableStateOf(48.dp) }
    var isDragging by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    var buttonColor by remember { mutableStateOf(primaryColor) }
    var buttonIcon by remember { mutableStateOf(R.drawable.outline_mic_none_24) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            while (isPressed) {
                delay(1000L)
                counter++
            }
        } else {
            counter = 0
        }
    }

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
            if (!isPressed) {
                MessageTextField(
                    searchText = chatMessage,
                    onSearchTextChange = { newText -> chatMessage = newText },
                    onSend = {
                        if (chatMessage.isNotEmpty()) {
                            sendMessage(chatMessage)
                            chatMessage = ""
                        }
                    },
                    onEmojiClicked = { emojiState = !emojiState },
                    onCameraClicked = { onCameraClicked() },
                    onAttachClicked = { onAttachClicked() },
                    placeholderText = "Type a message", modifier = Modifier.weight(1f)
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ), label = ""
                    )

                    Icon(
                        painter = painterResource(R.drawable.outline_mic_none_24),
                        contentDescription = "Mic",
                        tint = Color.Red.copy(alpha = alpha),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format("%02d:%02d", counter / 60, counter % 60),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "left",
                )
                Text(
                    text = "Swipe left to cancel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .clickable {
                        if (buttonTypeText.value && chatMessage.isNotEmpty()) {
                            sendMessage(chatMessage)
                            chatMessage = ""
                        }
                    }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .offset { IntOffset(offsetX.toInt(), 0) }
                        .size(buttonSize)
                        .background(buttonColor, CircleShape)
                        .conditionalPointerInput(!buttonTypeText.value) {
                            detectTapGestures(
                                onPress = {
                                    isPressed = true
                                    coroutineScope.launch {
                                        buttonSize = 80.dp
                                    }
                                    tryAwaitRelease()
                                    if (!isDragging) {
                                        isPressed = false
                                        buttonSize = 48.dp
                                    }
                                },
                            )
                        }
                        .conditionalPointerInput(!buttonTypeText.value) {
                            detectDragGestures(
                                onDragStart = {
                                    isDragging = true
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    offsetX = (offsetX + dragAmount.x).coerceIn(-400f, 0f)
                                    if (offsetX < -200) {
                                        buttonColor = Color.Red
                                        buttonIcon = R.drawable.outline_delete_24
                                    } else {
                                        buttonColor = primaryColor
                                        buttonIcon = R.drawable.outline_mic_none_24
                                    }
                                },
                                onDragEnd = {
                                    isDragging = false
                                    isPressed = false
                                    if (offsetX < -200) {
                                        Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                                    }
                                    buttonSize = 48.dp
                                    offsetX = 0f
                                    buttonColor = primaryColor
                                    buttonIcon = R.drawable.outline_mic_none_24
                                }
                            )
                        }
                ) {
                    AnimatedContent(
                        targetState = buttonTypeText.value,
                        transitionSpec = {
                            scaleIn(animationSpec = tween(600)) togetherWith scaleOut(
                                animationSpec = tween(600)
                            )
                        }
                    ) { targetState ->
                        Icon(
                            painter = if (targetState) painterResource(R.drawable.ic_send)
                            else painterResource(buttonIcon),
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    buttonColor,
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
    if (emojiState) {
        AndroidView(
            factory = { context ->
                EmojiPickerView(context).apply {
                    setBackgroundColor(Color.White.toArgb())
                    setOnEmojiPickedListener { emoji ->
                        chatMessage += emoji.emoji
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}