package com.bekircaglar.bluchat.presentation.message.starredmessages

import ChatBubble
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.VideoPlayerActivity
import com.bekircaglar.bluchat.loadThemePreference
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.component.ChatAppTopBar
import com.bekircaglar.bluchat.presentation.message.MessageViewModel
import com.bekircaglar.bluchat.presentation.message.convertTimestampToDate
import com.bekircaglar.bluchat.presentation.message.convertTimestampToDay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalFoundationApi::class)
@Composable

fun StarredMessagesScreen(chatId: String, navController: NavController) {
    val context = LocalContext.current
    val viewModel: MessageViewModel = hiltViewModel()
    val starredMessages by viewModel.starredMessages.collectAsStateWithLifecycle()

    val currentUser = viewModel.currentUser

    viewModel.getStarredMessages(chatId = chatId)

    val groupedMessages = starredMessages.reversed().groupBy { message ->
        convertTimestampToDay(message.timestamp!!)
    }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            ChatAppTopBar(title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)

                ) {
                    Column {
                        Text(
                            text = "Starred Messages",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        )

                    }
                }


            },
                navigationIcon = Icons.Default.KeyboardArrowLeft,
                onNavigateIconClicked = {
                    navController.navigate(Screens.MessageScreen.createRoute(chatId))
                },
            )
        }
    ) {
        LazyColumn(
            state = listState,
            reverseLayout = false,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                .paint(
                    painter = if (loadThemePreference(context = context)) {
                        painterResource(id = R.drawable.wp_dark)
                    } else {
                        painterResource(id = R.drawable.wp_background)
                    },
                    contentScale = ContentScale.FillBounds
                )
        ) {
            groupedMessages.forEach { (date, messagesForDate) ->
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                itemsIndexed(
                    messagesForDate,
                    key = { _, message -> message.messageId ?: 0 }) { _, message ->
                    if (message != null) {
                        val timestamp = convertTimestampToDate(message.timestamp!!)
                        val senderId = message.senderId

                        var senderName by remember { mutableStateOf("") }
                        LaunchedEffect(senderId) {
                            viewModel.getUserNameFromUserId(senderId!!) { name ->
                                senderName = name
                            }
                        }

                        val senderNameColor = viewModel.getUserColor(senderId!!)
                        message.messageType?.let { messageType ->
                            ChatBubble(
                                message = message,
                                isSentByMe = message.senderId == currentUser.uid,
                                timestamp = timestamp,
                                senderName = senderName,
                                senderNameColor = senderNameColor,
                                onImageClick = { imageUrl ->
                                    val encode = URLEncoder.encode(
                                        imageUrl,
                                        StandardCharsets.UTF_8.toString()
                                    )
                                    navController.navigate(
                                        Screens.ImageScreen.createRoute(
                                            encode
                                        )
                                    )
                                },
                                onUnStarMessage = {
                                    viewModel.unStarMessage(message, chatId)
                                },
                                onVideoClick = {
                                    val videoUrl = it
                                    val intent =
                                        Intent(context, VideoPlayerActivity::class.java).apply {
                                            putExtra("videoUri", videoUrl)
                                        }
                                    context.startActivity(intent)
                                },
                                context = context
                            )

                        }
                    }
                }
            }
        }
    }
}