package com.bekircaglar.bluchat.presentation.message.component

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.ui.theme.AppTheme
import com.bekircaglar.bluchat.ui.theme.BlueLight
import com.bekircaglar.bluchat.ui.theme.DarkBlue
import kotlinx.coroutines.launch

@Composable
fun AudioMessageBubble(
    audioUrl: Uri,
    audioDuration: Int = 0,
    isIncoming: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableStateOf(0f) }
    var totalDuration by remember { mutableStateOf(audioDuration * 1000) }

    val audioPlayerManager = remember { AudioPlayerManager(context) }

    val waveform = remember { generateWaveform(audioUrl) }

    val bubbleColor = if (isIncoming)
        BlueLight
    else
        MaterialTheme.colorScheme.primary

    val textColor = if (isIncoming)
        DarkBlue
    else
        MaterialTheme.colorScheme.background

    val formattedDuration = remember { formatDuration(totalDuration) }

    val togglePlayback: () -> Unit = {
        coroutineScope.launch {
            if (isPlaying) {
                audioPlayerManager.pause()
                isPlaying = false
            } else {
                if (currentProgress > 0f && currentProgress < totalDuration) {
                    audioPlayerManager.play(
                        audioUrl,
                        startProgress = currentProgress,
                        onProgress = { progress, duration ->
                            currentProgress = progress
                            totalDuration = duration
                        },
                        onCompletion = {
                            isPlaying = false
                            currentProgress = 0f
                        }
                    )
                } else {
                    audioPlayerManager.play(audioUrl,
                        onProgress = { progress, duration ->
                            currentProgress = progress
                            totalDuration = duration
                        },
                        onCompletion = {
                            isPlaying = false
                            currentProgress = 0f
                        }
                    )
                }
                isPlaying = true
            }
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = currentProgress,
        label = "Audio Progress"
    )

    Row(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bubbleColor)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isIncoming) MaterialTheme.colorScheme.primary
                    else Color.White.copy(alpha = 0.2f)
                )
                .clickable { togglePlayback() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = if (isPlaying) painterResource(R.drawable.baseline_pause_24) else painterResource(
                    R.drawable.baseline_play_arrow_24
                ),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(30.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                waveform.forEachIndexed { index, height ->
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight(height)
                            .background(
                                if (index < (animatedProgress * waveform.size).toInt())
                                    textColor.copy(alpha = 0.7f)
                                else
                                    textColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = formattedDuration,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(R.drawable.outline_mic_none_24),
            contentDescription = "Voice Memo",
            tint = textColor.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
}

fun generateWaveform(filePath: Uri, barCount: Int = 20): List<Float> {
    return List(barCount) {
        kotlin.random.Random.nextFloat() * 0.7f + 0.3f
    }
}

fun formatDuration(milliseconds: Int): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

class AudioPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null


    fun resume() {
        mediaPlayer?.start()
    }

    fun play(
        audioUrl: Uri,
        startProgress: Float = 0f,
        onProgress: (Float, Int) -> Unit = { _, _ -> },
        onCompletion: () -> Unit = {}
    ) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, audioUrl)
            prepare()



            if (startProgress > 0f) {
                val seekToPosition = (duration * startProgress).toInt()
                seekTo(seekToPosition)
            }

            val duration = duration
            start()

            val handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    val currentPosition = currentPosition
                    val progress = currentPosition.toFloat() / duration
                    onProgress(progress, duration)

                    if (isPlaying) {
                        handler.postDelayed(this, 100)
                    }
                }
            }
            handler.post(runnable)

            setOnCompletionListener {
                onCompletion()
                stop()
                handler.removeCallbacks(runnable)
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }
}


@Preview
@Composable
fun AudioMessageBubblePreview() {
    AppTheme {
        AudioMessageBubble(
            audioUrl = Uri.EMPTY,
            isIncoming = true
        )
    }
}