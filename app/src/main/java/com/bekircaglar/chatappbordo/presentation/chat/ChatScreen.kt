@file:OptIn(ExperimentalMaterial3Api::class)

package com.bekircaglar.chatappbordo.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bekircaglar.chatappbordo.R
import com.bekircaglar.chatappbordo.domain.model.ChatData
import com.bekircaglar.chatappbordo.presentation.auth.component.AuthTextField
import com.bekircaglar.chatappbordo.presentation.bottomappbar.ChatAppBottomAppBar
import com.bekircaglar.chatappbordo.presentation.chat.component.ChatAppFAB
import com.bekircaglar.chatappbordo.presentation.chat.component.SearchTextField
import com.bekircaglar.chatappbordo.presentation.component.ChatAppTopBar
import com.bekircaglar.chatappbordo.ui.theme.ChatAppBordoTheme


@Composable
fun ChatScreen(navController: NavController) {

    var searchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Chats", color = MaterialTheme.colorScheme.onSecondaryContainer)
                },
                actions = {
                    AnimatedVisibility(
                        visible = searchActive,
                        enter = expandHorizontally(),
                    ) {
                        // component yap
                        SearchTextField(searchText = searchText, onSearchTextChange = { searchText = it })

                    }
                    IconButton(onClick = { searchActive = !searchActive }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }

            )
        },
        floatingActionButton = {
            ChatAppFAB(contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
        },
        bottomBar = {
            ChatAppBottomAppBar(navController = navController)
        }
    ) {
        val chatList = listOf(
            ChatData(
                profileImage = painterResource(id = R.drawable.ic_user1),
                userName = "John Doe",
                lastMessage = "Let's catch up tomorrow!",
                messageTime = "12:45 PM",
                unreadCount = 1,
                isOnline = true
            ),
            ChatData(
                profileImage = painterResource(id = R.drawable.ic_user2),
                userName = "Jane Smith",
                lastMessage = "Thank you for the help!",
                messageTime = "11:30 AM",
                unreadCount = 0,
                isOnline = false
            ),
            ChatData(
                profileImage = painterResource(id = R.drawable.ic_user3),
                userName = "Sam Wilson",
                lastMessage = "Call me when you're free.",
                messageTime = "10:15 AM",
                unreadCount = 3,
                isOnline = true
            ),
            ChatData(
                profileImage = painterResource(id = R.drawable.ic_user4),
                userName = "Emily Davis",
                lastMessage = "See you soon!",
                messageTime = "09:50 AM",
                unreadCount = 0,
                isOnline = false
            ),
            ChatData(
                profileImage = painterResource(id = R.drawable.ic_user5),
                userName = "Michael Brown",
                lastMessage = "Meeting rescheduled to 3 PM.",
                messageTime = "08:30 AM",
                unreadCount = 2,
                isOnline = false
            )
        )

        Spacer(modifier = Modifier.padding(top = 16.dp))
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            items(chatList) { chat ->
                Chats(
                    profileImage = chat.profileImage,
                    userName = chat.userName,
                    lastMessage = chat.lastMessage,
                    messageTime = chat.messageTime,
                    unreadCount = chat.unreadCount,
                    isOnline = chat.isOnline
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }



        }
    }


//@Preview
//@Composable
//fun ChatScreenPreview() {
//    ChatAppBordoTheme {
//        ChatScreen()
//    }
//}