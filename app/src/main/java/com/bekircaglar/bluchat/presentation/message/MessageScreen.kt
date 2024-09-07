package com.bekircaglar.bluchat.presentation.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.component.ChatAppTopBar
import com.bekircaglar.bluchat.presentation.message.component.ChatBubble
import com.bekircaglar.bluchat.presentation.message.component.MessageTextField

@Composable
fun MessageScreen(navController: NavController, chatId: String) {

    val viewModel: MessageViewModel = hiltViewModel()

    val userInfo by viewModel.userData.collectAsStateWithLifecycle()

    val messages by viewModel.messages.collectAsStateWithLifecycle()

    var chatMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = chatId) {
        viewModel.getUserFromChatId(chatId)
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

    Scaffold(topBar = {
        ChatAppTopBar(title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
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
                navController.navigate(Screens.ChatScreen.route)
            },
            actionIcon = Icons.Default.Search, onActionIconClicked = {})
    }, bottomBar = {
        BottomAppBar(
            containerColor = Color.White,

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
    }


    ) {

        if (messages.isNotEmpty()) {
            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer)

            ) {
                items(count = messages.size, key = {i -> messages[i].messageId ?: i}) { i ->
                    val message = messages[i]
                    ChatBubble(
                        message = message.message!!, isSentByMe = message.senderId != userInfo?.uid
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lets start chatting",
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
    Icon(
        imageVector = Icons.Outlined.Add, contentDescription = null, tint = Color.Gray
    )
}

@Composable
private fun SendIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_send),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
    )
}