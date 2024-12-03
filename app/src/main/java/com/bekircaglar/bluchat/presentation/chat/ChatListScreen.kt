@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.bekircaglar.bluchat.presentation.chat

import ChatAppBottomAppBar
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.utils.UiState
import com.bekircaglar.bluchat.domain.model.Chats
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.chat.component.BottomSheet
import com.bekircaglar.bluchat.presentation.chat.component.ChatAppFAB
import com.bekircaglar.bluchat.presentation.chat.component.Chats
import com.bekircaglar.bluchat.presentation.chat.component.SearchTextField
import com.bekircaglar.bluchat.presentation.chat.component.ShimmerItem
import com.bekircaglar.bluchat.presentation.chat.groupchat.GroupChatBottomSheet
import com.bekircaglar.bluchat.presentation.chat.groupchat.SelectGroupMemberBottomSheet
import com.bekircaglar.bluchat.presentation.chat.searchchat.OpenChatBottomSheet
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val selectedUser by viewModel.selectedUser.collectAsStateWithLifecycle()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val chatPPImageState by viewModel.chatPPImageState.collectAsStateWithLifecycle()

    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()

    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val textFieldValue by viewModel.searchQuery.collectAsStateWithLifecycle()

    val chatList by viewModel.chatUserList.collectAsStateWithLifecycle()

    val uploadedImageUri by viewModel.uploadedImageUri.collectAsStateWithLifecycle()

    val uploadImageState by viewModel.UploadImageState.collectAsStateWithLifecycle()


    var groupMembers by remember { mutableStateOf(emptyList<String>()) }


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

    Scaffold(topBar = {
        TopAppBar(title = {
            if (!isSearchActive)
            Text(
                text = "BluChat",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ), actions = {
            AnimatedVisibility(
                visible = isSearchActive,
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start, clip = false),
                exit =  shrinkHorizontally(shrinkTowards = Alignment.Start, clip = false) + fadeOut()
            ) {
                SearchTextField(
                    query = searchText,
                    onQueryChange = { searchText = it },
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
            IconButton(onClick = { isSearchActive = !isSearchActive }) {
                Icon(
                    Icons.Default.Search, contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        })
    }, floatingActionButton = {
        ChatAppFAB(contentColor = Color.White,
            backgroundColor = MaterialTheme.colorScheme.primary,
            onClick = {
                isBottomSheetVisible = true
            })
    }, bottomBar = {
        ChatAppBottomAppBar(navController = navController)
    }) {

        if (uiState is UiState.Loading) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(MaterialTheme.colorScheme.background),
            ) {
                repeat(10) {
                    item {
                        ShimmerItem()
                    }
                }
            }
        } else if (uiState == UiState.Success()) {
            if (selectGroupUserDialog) {
                SelectGroupMemberBottomSheet(
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
                OpenChatBottomSheet(searchResults = searchResults,
                    textFieldValue = textFieldValue,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    onDismiss = {
                        addChatActive = false
                    },
                    navController = navController,
                    onItemClick = {
                        viewModel.createChatRoom(it.uid, navController)
                        addChatActive = false
                    })
            }

            if (isBottomSheetVisible) {
                BottomSheet(onDismiss = { isBottomSheetVisible = false }, onClicked = {
                    when (it) {
                        "New Chat" -> addChatActive = true
                        "Create Group Chat" -> selectGroupUserDialog = true
                    }
                })
            }
            if (createGroupChatDialog) {
                GroupChatBottomSheet(selectedUri = selectedImageUri,
                    onDismissRequest = { createGroupChatDialog = false },
                    onCreateGroupChat = { groupChatName ->
                        viewModel.createGroupChatRoom(
                            groupMembers, groupChatName, uploadedImageUri.toString()
                        )
                        createGroupChatDialog = false
                    },
                    isImageLoading = uploadImageState == UiState.Loading,
                    onPermissionRequest = { permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES) })
            }

            Spacer(modifier = Modifier.padding(top = 16.dp))
            if (chatList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .background(MaterialTheme.colorScheme.background),
                ) {

                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                    items(chatList.sortedByDescending { it.messageTime }) { chat ->

                        val formattedMessageTime = remember(chat.messageTime) {
                            chat.messageTime?.let { it1 ->
                                formatMessageTime(
                                    it1.toLong(),
                                    timeFormat,
                                    dayOfWeekFormat,
                                    dateFormat
                                )
                            }
                        }

                        val myChat = Chats(
                            chatRoomId = chat.chatRoomId,
                            imageUrl = chat.imageUrl,
                            name = chat.name,
                            surname = chat.surname,
                            lastMessage = chat.lastMessage,
                            lastMessageSenderId = chat.lastMessageSenderId,
                            messageTime = formattedMessageTime,
                            isOnline = chat.isOnline
                        )

                        var lastMessageSenderName by remember { mutableStateOf("") }
                        LaunchedEffect(chat.lastMessageSenderId) {
                            chat.lastMessageSenderId?.let { it1 ->
                                viewModel.getUserNameFromUserId(it1) { name ->
                                    lastMessageSenderName = name
                                }
                            }
                        }
                        Chats(
                            chat = myChat,
                            onClick = {
                                navController.navigate(Screens.MessageScreen.createRoute(chat.chatRoomId))
                            },
                            onImageLoaded = {
                                viewModel.changeImageState()
                            },
                            lastMessageSenderName = lastMessageSenderName,
                            currentUserId = currentUser,

                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sticker_shake_hand),
                            contentDescription = "Chat",
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(modifier = Modifier.height(64.dp))
                        Text(
                            text = "No chats yet!", style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }

        if (uiState is UiState.Error) {
            val errorMessage = (uiState as UiState.Error).message
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}

fun formatMessageTime(
    timestamp: Long,
    timeFormat: SimpleDateFormat,
    dayOfWeekFormat: SimpleDateFormat,
    dateFormat: SimpleDateFormat
): String {
    val messageDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    val currentDate = Calendar.getInstance()

    return when {
        messageDate.isSameDay(currentDate) -> timeFormat.format(messageDate.time)
        messageDate.isSameWeek(currentDate) -> dayOfWeekFormat.format(messageDate.time)
        else -> dateFormat.format(messageDate.time)
    }
}

fun Calendar.isSameDay(other: Calendar): Boolean {
    return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
}

fun Calendar.isSameWeek(other: Calendar): Boolean {
    return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            this.get(Calendar.WEEK_OF_YEAR) == other.get(Calendar.WEEK_OF_YEAR)
}