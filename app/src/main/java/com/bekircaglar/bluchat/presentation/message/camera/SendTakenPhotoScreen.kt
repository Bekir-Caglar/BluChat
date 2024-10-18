package com.bekircaglar.bluchat.presentation.message.camera

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.Player
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.message.component.MessageTextField
import com.bekircaglar.bluchat.utils.IMAGE

@OptIn(UnstableApi::class)
@Composable
fun SendTakenPhotoScreen(imageUrl: String, chatId: String, navController: NavController) {
    val viewModel: SendTakenPhotoViewModel = hiltViewModel()
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl.contains(".mp4")) {
                val mediaItem: MediaItem = imageUrl.let { MediaItem.fromUri(it) }

                val mediaSource: MediaSource = remember {
                    ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                        .createMediaSource(mediaItem ?: MediaItem.EMPTY)
                }
                val exoPlayer = remember {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaSource(mediaSource)
                        playWhenReady = true
                        prepare()
                    }
                }
                Player(exoPlayer = exoPlayer)
            } else {
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            MessageTextField(
                searchText = message,
                onSearchTextChange = { newText ->
                    message = newText.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                },
                onSend = {
                    viewModel.sendMessage(
                        imageUrl = imageUrl,
                        message = message,
                        messageType = IMAGE,
                        chatId = chatId,
                    )
                    navController.navigate(Screens.MessageScreen.createRoute(chatId))
                },
                placeholderText = "Add a caption...",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        viewModel.sendMessage(
                            imageUrl = imageUrl,
                            message = message,
                            messageType = IMAGE,
                            chatId = chatId,
                        )
                        navController.navigate(Screens.MessageScreen.createRoute(chatId))
                    }
            )
        }
    }
}
