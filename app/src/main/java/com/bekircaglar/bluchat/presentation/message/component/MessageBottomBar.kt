package com.bekircaglar.bluchat.presentation.message.component

import android.Manifest
import android.content.Context
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.utils.AudioRecorderManager
import com.bekircaglar.bluchat.utils.PermissionManager
import com.bekircaglar.bluchat.utils.VibrationUtils
import com.bekircaglar.bluchat.utils.conditionalPointerInput
import com.google.common.io.Resources
import kotlinx.coroutines.delay

@Composable
fun MessageBottomBar(
    onAttachClicked: () -> Unit,
    onCameraClicked: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSendAudio: (String, Int) -> Unit
) {
    val context = LocalContext.current

    var chatMessage by remember { mutableStateOf("") }
    var emojiState by remember { mutableStateOf(false) }
    var recordingCounter by remember { mutableIntStateOf(0) }

    var offsetX by remember { mutableStateOf(0f) }
    var buttonSize by remember { mutableStateOf(48.dp) }
    var isDragging by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    var buttonColor by remember { mutableStateOf(primaryColor) }
    var buttonIcon by remember { mutableStateOf(R.drawable.outline_mic_none_24) }

    val audioRecorderManager = remember { AudioRecorderManager(context) }
    val permissionManager = remember { PermissionManager(context) }

    val hasMessageContent = chatMessage.isNotEmpty()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            recordingCounter = 0
            while (isPressed) {
                delay(1000L)
                recordingCounter++
            }
        } else {
            recordingCounter = 0
        }
    }
    fun startAudioRecording(recorderManager: AudioRecorderManager) {
        try {
            recorderManager.startRecording()
            VibrationUtils.vibrate(context, 50)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to start recording", Toast.LENGTH_SHORT).show()
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startAudioRecording(audioRecorderManager)
        } else {
            Toast.makeText(context, "Audio recording permission denied", Toast.LENGTH_SHORT).show()
        }
    }



    BottomAppBar(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = isPressed,
                label = "animatedContent",
                transitionSpec ={
                    scaleIn(animationSpec = tween(300)) togetherWith scaleOut(
                        animationSpec = tween(300)
                    )
                }
            ) {
                when (it) {
                    true -> {
                        RecordingStatusDisplay(
                            recordingCounter = recordingCounter,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    false -> {
                        MessageTextField(
                            searchText = chatMessage,
                            onSearchTextChange = { newText -> chatMessage = newText },
                            onSend = {
                                if (chatMessage.isNotEmpty()) {
                                    onSendMessage(chatMessage)
                                    chatMessage = ""
                                }
                            },
                            onEmojiClicked = { emojiState = !emojiState },
                            onCameraClicked = onCameraClicked,
                            onAttachClicked = onAttachClicked,
                            placeholderText = "Type a message",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            AudioRecordButton(
                hasMessageContent = hasMessageContent,
                buttonSize = buttonSize,
                offsetX = offsetX,
                buttonColor = buttonColor,
                buttonIcon = buttonIcon,
                onSendMessage = {
                    if (hasMessageContent) {
                        onSendMessage(chatMessage)
                        chatMessage = ""
                    }
                },
                onStartRecording = {
                    VibrationUtils.vibrate(context, 50)
                    isPressed = true
                    buttonSize = 80.dp

                    if (permissionManager.hasAudioRecordPermission()) {
                        startAudioRecording(audioRecorderManager)
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onStopRecording = { cancelRecording ->
                    if (!isDragging) {
                        isPressed = false
                        buttonSize = 48.dp
                        val recordedFilePath = audioRecorderManager.stopRecording()

                        if (!cancelRecording && recordedFilePath != null && recordingCounter >= 1) {
                            onSendAudio(recordedFilePath, recordingCounter)
                        }
                    }


                },
                onDragStart = {
                    isDragging = true
                },
                onDragUpdate = { dragAmount ->
                    offsetX = (offsetX + dragAmount.x).coerceIn(-400f, 0f)
                    buttonColor = if (offsetX < -200) Color.Red else primaryColor
                    buttonIcon = if (offsetX < -200) R.drawable.outline_delete_24
                    else R.drawable.outline_mic_none_24
                },
                onDragEnd = {
                    if (offsetX < -200) {
                        audioRecorderManager.stopRecording()
                    } else {
                        val recordedFilePath = audioRecorderManager.stopRecording()
                        if (recordedFilePath != null && recordingCounter >= 1) {
                            onSendAudio(recordedFilePath, recordingCounter)
                        }
                    }
                    buttonSize = 48.dp
                    isPressed = false
                    isDragging = false
                    offsetX = 0f
                    buttonColor = primaryColor
                    buttonIcon = R.drawable.outline_mic_none_24

                }
            )
        }
    }

    if (emojiState) {
        EmojiPickerComponent(
            onEmojiPicked = { emoji ->
                chatMessage += emoji
            }
        )
    }
}

@Composable
fun RecordingStatusDisplay(
    recordingCounter: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
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
            contentDescription = "Recording",
            tint = Color.Red.copy(alpha = alpha),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = String.format("%02d:%02d", recordingCounter / 60, recordingCounter % 60),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Swipe left to cancel",
        )
        Text(
            text = "Swipe left to cancel",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun EmojiPickerComponent(
    onEmojiPicked: (String) -> Unit
) {
    AndroidView(
        factory = { ctx ->
            EmojiPickerView(ctx).apply {
                setBackgroundColor(Color.White.toArgb())
                setOnEmojiPickedListener { emoji ->
                    onEmojiPicked(emoji.emoji)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Composable
fun AudioRecordButton(
    hasMessageContent: Boolean,
    buttonSize: Dp,
    offsetX: Float,
    buttonColor: Color,
    buttonIcon: Int,
    onSendMessage: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: (Boolean) -> Unit,
    onDragStart: () -> Unit,
    onDragUpdate: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable {
                if (hasMessageContent) {
                    onSendMessage()
                }
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset { IntOffset(offsetX.toInt(), 0) }
                .size(buttonSize)
                .background(buttonColor, CircleShape)
                .conditionalPointerInput(!hasMessageContent) {
                    detectTapGestures(
                        onPress = {
                            onStartRecording()
                            tryAwaitRelease()
                            onStopRecording(false)
                        }
                    )
                }
                .conditionalPointerInput(!hasMessageContent) {
                    detectDragGestures(
                        onDragStart = { onDragStart() },
                        onDrag = { _, dragAmount -> onDragUpdate(dragAmount) },
                        onDragEnd = {
                            val isCancelled = offsetX < -200
                            onStopRecording(isCancelled)
                            onDragEnd()
                        }
                    )
                }
        ) {
            AnimatedContent(
                targetState = hasMessageContent,
                transitionSpec = {
                    scaleIn(animationSpec = tween(600)) togetherWith scaleOut(
                        animationSpec = tween(600)
                    )
                },
                label = "iconContent"
            ) { targetState ->
                Icon(
                    painter = if (targetState) painterResource(R.drawable.ic_send)
                    else painterResource(buttonIcon),
                    contentDescription = "Send/Record",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .size(20.dp)
                        .background(buttonColor, CircleShape)
                )
            }
        }
    }
}