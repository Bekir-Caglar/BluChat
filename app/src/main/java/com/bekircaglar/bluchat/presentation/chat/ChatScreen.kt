@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.bekircaglar.bluchat.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bekircaglar.bluchat.domain.model.Chats
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.ShowToastMessage
import com.bekircaglar.bluchat.presentation.bottomappbar.ChatAppBottomAppBar
import com.bekircaglar.bluchat.presentation.chat.component.ChatAppFAB
import com.bekircaglar.bluchat.presentation.chat.component.SearchTextField
import com.bekircaglar.bluchat.presentation.chat.searchchat.OpenChatDialog


@Composable
fun ChatScreen(navController: NavController) {

    val viewModel: ChatViewModel = hiltViewModel()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val context = LocalContext.current
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var addChatActive by remember { mutableStateOf(false) }

    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val textFieldValue by viewModel.searchQuery.collectAsStateWithLifecycle()

    val chatList by viewModel.chatUserList.collectAsStateWithLifecycle()

    val error by viewModel.error.collectAsStateWithLifecycle()
    val success by viewModel.succes.collectAsStateWithLifecycle()


    if (error != null) {
        ShowToastMessage(context = context, message = error!!)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Chats", color = MaterialTheme.colorScheme.onSecondaryContainer)
                },
                actions = {
                    AnimatedVisibility(
                        visible = isSearchActive,
                        enter = expandHorizontally(),
                    ) {

                        SearchTextField(
                            searchText = searchText,
                            onSearchTextChange = { searchText = it })

                    }
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }

            )
        },
        floatingActionButton = {
            ChatAppFAB(contentColor = MaterialTheme.colorScheme.onSecondaryContainer, onClick = {
                addChatActive = true
            })
        },
        bottomBar = {
            ChatAppBottomAppBar(navController = navController)
        }
    ) {

        if (addChatActive) {
            OpenChatDialog(
                searchResults = searchResults,
                textFieldValue = textFieldValue,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onDismiss = {
                    addChatActive = false
                },
                navController = navController,
                onItemClick = {
                    viewModel.createChatRoom(it.uid, navController)
                    addChatActive = false}
            )
        }





        Spacer(modifier = Modifier.padding(top = 16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            items(chatList) { chat ->

                val myChat = Chats(chatRoomId = chat.chatRoomId,
                    imageUrl = chat.imageUrl,
                    name = chat.name,
                    surname = chat.surname,
                    lastMessage = chat.lastMessage,
                    messageTime = chat.messageTime,
                    isOnline = chat.isOnline
                )
                Chats(chat =myChat) {
                    navController.navigate(Screens.MessageScreen.createRoute(chat.chatRoomId))
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }


    }
}