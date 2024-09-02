package com.bekircaglar.chatappbordo.presentation.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bekircaglar.chatappbordo.R
import com.bekircaglar.chatappbordo.navigation.Screens
import com.bekircaglar.chatappbordo.presentation.chat.component.SearchTextField
import com.bekircaglar.chatappbordo.presentation.component.ChatAppTopBar
import com.bekircaglar.chatappbordo.presentation.message.component.ChatBubble


@Composable
fun MessageScreen(navController: NavController, chatId: String) {

    val viewModel: MessageViewModel = hiltViewModel()

    val userInfo by viewModel.userData.collectAsStateWithLifecycle()

    val messages by viewModel.messages.collectAsStateWithLifecycle()

    var message by remember { mutableStateOf("") }

    LaunchedEffect(key1 = chatId) {
        viewModel.getUserFromChatId(chatId)
        viewModel.createMessageRoom(chatId)
        viewModel.loadMessages(chatId)
    }
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
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
        }, navigationIcon = Icons.Default.KeyboardArrowLeft, onNavigateIconClicked = {
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
                SearchTextField(
                    searchText = message,
                    onSearchTextChange = { message = it
                    })


                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = {
                        viewModel.sendMessage(message, chatId)
                        message = ""
                    }, modifier = Modifier.padding(end = 16.dp)
                ) {
                    SendIcon()
                }
            }
        }
    }


    ) {

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)

        ) {
            items(messages) { message ->
                if (message.message != null) {
                    ChatBubble(
                        message = message.message, isSentByMe = message.senderId != userInfo?.uid
                    )
                } else {
                    Text(text = "Hadi sohbete başla")
                }
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
