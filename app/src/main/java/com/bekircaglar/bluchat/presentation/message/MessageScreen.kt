package com.bekircaglar.bluchat.presentation.message

import android.view.WindowInsets.Side
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
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
import com.bekircaglar.bluchat.loadThemePreference
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.component.ChatAppTopBar
import com.bekircaglar.bluchat.presentation.message.component.ChatBubble
import com.bekircaglar.bluchat.presentation.message.component.MessageTextField
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageScreen(navController: NavController, chatId: String) {
    val viewModel: MessageViewModel = hiltViewModel()
    val context = LocalContext.current
    val userInfo by viewModel.userData.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val currentUser = viewModel._currentUser

    var chatMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()


    val groupedMessages = messages.groupBy { message ->
        convertTimestampToDay(message.timestamp!!)
    }

    LaunchedEffect(key1 = chatId) {
        viewModel.getChatRoom(chatId)
        viewModel.createMessageRoom(chatId)
        viewModel.loadInitialMessages(chatId)
    }

    val startPagination by remember {
        derivedStateOf {
            val lastIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -3
            val totalItemCount = messages.size
            lastIndex >= totalItemCount - 3
        }
    }
    LaunchedEffect(startPagination) {
        if (startPagination && messages.size >= 15) {
            viewModel.loadMoreMessages(chatId)
        }
    }

    LaunchedEffect(Unit) {
        val groupId = chatId
        val userId = currentUser.uid
        viewModel.observeGroupAndUserStatus(groupId, userId)
    }
    val isKickedOrGroupDeleted by viewModel.isKickedOrGroupDeleted.collectAsState()

    LaunchedEffect(isKickedOrGroupDeleted) {
        if (isKickedOrGroupDeleted) {
            navController.navigate(Screens.ChatListScreen.route) {
                popUpTo(Screens.MessageScreen.route) { inclusive = true }
            }
        }
    }

    Scaffold(topBar = {
        ChatAppTopBar(title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(Screens.ChatInfoScreen.createRoute(chatId))
                    }
                    .padding(start = 8.dp)

            ) {
                Image(
                    painter = rememberImagePainter(data = userInfo?.profileImageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(44.dp),
                )
                Text(
                    text = userInfo?.name ?: "",
                    modifier = Modifier.padding(start = 10.dp),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            }
        }, navigationIcon = Icons.Default.KeyboardArrowLeft,
            onNavigateIconClicked = {
                navController.navigate(Screens.ChatListScreen.route)
            },
            actionIcon = Icons.Default.Search,
            onActionIconClicked = {})
    }, bottomBar = {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { /*TODO*/ }, modifier = Modifier.padding(end = 8.dp)
                ) {
                    PlusIcon()
                }
                MessageTextField(
                    searchText = chatMessage,
                    onSearchTextChange = { newText ->
                        chatMessage =
                            newText.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    },
                    onSend = {
                        viewModel.sendMessage(chatMessage, chatId)
                        chatMessage = ""
                    },
                    placeholderText = "Type a message",
                    modifier = Modifier.width(300.dp)
                )

                    Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = {
                        if (chatMessage.isNotEmpty()) {
                            viewModel.sendMessage(chatMessage, chatId)
                            chatMessage = ""
                        }
                    }, modifier = Modifier.padding(end = 16.dp)
                ) {
                    SendIcon()
                }
            }
        }
    }, ) {
        if (messages.isNotEmpty()) {
            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .paint(
                        painter = if (loadThemePreference(context = context)) {
                            painterResource(id = R.drawable.bg_message_dark)
                        } else {
                            painterResource(id = R.drawable.bg_message_light)
                        },
                        contentScale = ContentScale.FillBounds
                    )
            ) {
                // Loop through the groups (dates) and messages under each date
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

                    itemsIndexed(messagesForDate, key = { _, message -> message.messageId ?: 0 }) { _, message ->
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
                            ChatBubble(
                                message = message,
                                isSentByMe = message.senderId == currentUser.uid,
                                timestamp = timestamp,
                                senderName = senderName,
                                senderNameColor = senderNameColor
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Let's start chatting",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun PlusIcon() {
    Icon(imageVector = Icons.Outlined.Add, contentDescription = null, tint = Color.Gray)
}

@Composable
private fun SendIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_send),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
    )
}

fun convertTimestampToDate(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(formatter)
}

fun convertTimestampToDay(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    return dateTime.format(formatter)
}