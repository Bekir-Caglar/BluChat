@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.bekircaglar.bluchat.presentation.chat

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.bekircaglar.bluchat.presentation.chat.component.BottomSheet
import com.bekircaglar.bluchat.presentation.chat.component.ChatAppFAB
import com.bekircaglar.bluchat.presentation.chat.component.Chats
import com.bekircaglar.bluchat.presentation.chat.component.SearchTextField
import com.bekircaglar.bluchat.presentation.chat.groupchat.GroupChatDialog
import com.bekircaglar.bluchat.presentation.chat.groupchat.SelectGroupMemberDialog
import com.bekircaglar.bluchat.presentation.chat.searchchat.OpenChatDialog
import com.bekircaglar.bluchat.presentation.component.ChatAppTopBar


@Composable
fun ChatListScreen(navController: NavController) {

    val viewModel: ChatViewModel = hiltViewModel()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val context = LocalContext.current
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var addChatActive by remember { mutableStateOf(false) }
    var selectGroupUserDialog by remember { mutableStateOf(false) }
    var createGroupChatDialog by remember { mutableStateOf(false) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()

    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val textFieldValue by viewModel.searchQuery.collectAsStateWithLifecycle()

    val chatList by viewModel.chatUserList.collectAsStateWithLifecycle()

    val uploadedImageUri by viewModel.uploadedImageUri.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val error by viewModel.error.collectAsStateWithLifecycle()
    val success by viewModel.success.collectAsStateWithLifecycle()

    var groupMembers by remember { mutableStateOf(emptyList<String>()) }


    if (error != null) {
        ShowToastMessage(context = context, message = error!!)
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onImageSelected(it)

        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Galeriye erişim izni gerekli!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Chats", color = MaterialTheme.colorScheme.onPrimary)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    AnimatedVisibility(
                        visible = isSearchActive,
                        enter = expandHorizontally(),
                    ) {

                        SearchTextField(
                            searchText = searchText,
                            height = 50,
                            width = 300,
                            onSearchTextChange = { searchText = it },
                        )

                    }
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            Icons.Default.Search, contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }

            )
        },
        floatingActionButton = {
            ChatAppFAB(
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                onClick = {
                    isBottomSheetVisible = true
                }
            )
        },
        bottomBar = {
            ChatAppBottomAppBar(navController = navController)
        }
    ) {

        if (selectGroupUserDialog) {
            SelectGroupMemberDialog(
                searchResults = searchResults,
                textFieldValue = textFieldValue,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onDismiss = {
                    selectGroupUserDialog = false
                },
                onNext = {
                    groupMembers = it
                    createGroupChatDialog = true
                    selectGroupUserDialog = false
                },
            )
        }

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
                    addChatActive = false
                }
            )
        }

        if (isBottomSheetVisible) {
            BottomSheet(
                onDismiss = { isBottomSheetVisible = false },
                onClicked = {
                    when (it) {
                        "New Chat" -> addChatActive = true
                        "Create Group Chat" -> selectGroupUserDialog = true
                    }
                }
            )
        }
        if (createGroupChatDialog) {
            GroupChatDialog(
                selectedUri = selectedImageUri,
                onDismissRequest = { createGroupChatDialog = false },
                onCreateGroupChat = { groupChatName ->
                    viewModel.createGroupChatRoom(
                        groupMembers,
                        groupChatName,
                        uploadedImageUri.toString()
                    )
                    createGroupChatDialog = false
                },
                isImageLoading = isLoading,
                onPermissionRequest = { permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES) }
            )
        }


        Spacer(modifier = Modifier.padding(top = 16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.background),
        ) {
            items(chatList) { chat ->

                val myChat = Chats(
                    chatRoomId = chat.chatRoomId,
                    imageUrl = chat.imageUrl,
                    name = chat.name,
                    surname = chat.surname,
                    lastMessage = chat.lastMessage,
                    messageTime = chat.messageTime,
                    isOnline = chat.isOnline
                )
                Chats(chat = myChat, onClick = {
                    navController.navigate(Screens.MessageScreen.createRoute(chat.chatRoomId))
                })

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }


    }
}