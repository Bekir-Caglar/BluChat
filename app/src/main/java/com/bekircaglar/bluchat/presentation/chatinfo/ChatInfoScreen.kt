package com.bekircaglar.bluchat.presentation.chatinfo

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.chat.groupchat.GroupChatDialog
import com.bekircaglar.bluchat.presentation.chat.groupchat.SelectGroupMemberDialog
import com.bekircaglar.bluchat.presentation.component.ChatAppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInfoScreen(
    navController: NavController? = null,
    chatId: String? = null,
) {

    val viewModel: ChatInfoViewModel = hiltViewModel()

    val currentUser = viewModel.currentUser
    val context = LocalContext.current

    val chatRoom by viewModel.chatRoom.collectAsStateWithLifecycle()
    val userList by viewModel.chatUserList.collectAsStateWithLifecycle()
    val chatUserIdList by viewModel.chatUserIdList.collectAsStateWithLifecycle()

    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val textFieldValue by viewModel.searchQuery.collectAsStateWithLifecycle()

    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val uploadedImageUri by viewModel.uploadedImageUri.collectAsStateWithLifecycle()


    var selectGroupUserDialog by remember { mutableStateOf(false) }
    var updateGroupInfoDialog by remember { mutableStateOf(false) }


    val adminId = chatRoom.chatAdminId

    val isCurrentUserAdmin: Boolean = currentUser.uid == adminId


    LaunchedEffect(Unit) {
        viewModel.getChatRoom(chatId!!)
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
            ChatAppTopBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Group Info",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(end = 30.dp)
                        )
                    }
                },
                navigationIcon = Icons.Default.KeyboardArrowLeft,
                onNavigateIconClicked = {
                    navController?.navigate(Screens.MessageScreen.createRoute(chatId!!)) {
                        popUpTo(Screens.ChatInfoScreen.route) { inclusive = true }
                    }
                },
            )
        }
    ) { paddingValues ->

        if (selectGroupUserDialog) {
            val filteredSearchResults = searchResults.filter { user ->
                user.uid !in chatUserIdList
            }
            SelectGroupMemberDialog(
                searchResults = filteredSearchResults,
                textFieldValue = textFieldValue,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onDismiss = {
                    selectGroupUserDialog = false
                },
                onNext = { addedUser ->
                    val newList = chatUserIdList + addedUser + currentUser.uid
                    viewModel.addParticipant(chatId!!, newList)
                    viewModel.getChatRoom(chatId!!)
                    selectGroupUserDialog = false
                },
            )
        }
        if (updateGroupInfoDialog) {
            GroupChatDialog(
                defaultImageUrl = chatRoom.chatImage,
                defaultGroupName = chatRoom.chatName!!,
                selectedUri = uploadedImageUri,
                onDismissRequest = { updateGroupInfoDialog = false },
                onCreateGroupChat = { groupChatName ->
                    if (uploadedImageUri.toString().isEmpty()) {
                        viewModel.updateChatInfo(chatId!!, groupChatName,chatRoom.chatImage!!)
                    }else viewModel.updateChatInfo(chatId!!, groupChatName,uploadedImageUri.toString())

                    viewModel.getChatRoom(chatId)
                    updateGroupInfoDialog = false
                },
                buttonText = "Update group info",
                isImageLoading = isLoading,
                onPermissionRequest = { permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES) }
            )
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = if (isCurrentUserAdmin) Modifier
                        .clickable {
                            updateGroupInfoDialog = true
                        } else Modifier

                ) {
                    Image(
                        painter = rememberImagePainter(data = chatRoom.chatImage),
                        contentDescription = "Group Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(elevation = 5.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    )

                    if (isCurrentUserAdmin) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .align(Alignment.BottomEnd)
                                .border(2.dp, Color.White, CircleShape)
                                .padding(4.dp)
                        )
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = chatRoom.chatName ?: "",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )


                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${userList.size+1} members",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            TextField(
                value = "",
                onValueChange = { /*TODO*/ },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                placeholder = {
                    Text(text = "Search members")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            if(isCurrentUserAdmin) {
                MemberItem(
                    Users(
                        uid = "0",
                        name = "Add participants",
                        surname = "",
                        profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/chatappbordo.appspot.com/o/profileImages%2Faddicon.png?alt=media&token=9f24b0b2-0e43-4444-a8d3-7274f00a8ee8"
                    ), false, onItemClick = {
                    selectGroupUserDialog = true
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(userList) { member ->
                    MemberItem(member, isCurrentUserAdmin, onUserKicked = {
                        viewModel.kickUser(chatId = chatId!!, userId = member.uid)
                        viewModel.getChatRoom(chatId)
                    })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isCurrentUserAdmin) {
                ElevatedButton(
                    onClick = {
                        viewModel.deleteGroup(chatId!!)
                        navController?.navigate(Screens.ChatListScreen.route) {
                            popUpTo(Screens.ChatInfoScreen.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Blue,
                    ),
                ) {
                    Text(
                        text = "Delete Group",
                        color = Color.Red
                    )
                }
            }
            ElevatedButton(
                onClick = {
                    viewModel.leaveChat(chatId!!)
                    navController?.navigate(Screens.ChatListScreen.route) {
                        popUpTo(Screens.ChatInfoScreen.route) { inclusive = true }
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color.Gray,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.Blue,
                ),
            ) {
                Text(
                    text = "Leave Group",
                    color = MaterialTheme.colorScheme.error
                )
            }

        }
    }
}

@Composable
fun MemberItem(member: Users, isCurrentUserAdmin: Boolean, onUserKicked: () -> Unit = {},onItemClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onItemClick() }) {
            Image(
                painter = rememberImagePainter(data = member.profileImageUrl!!),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = member.name + " " + member.surname,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (isCurrentUserAdmin) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Remove User",
                tint = Color.Red,
                modifier = Modifier
                    .clickable {
                        onUserKicked()
                    }
            )
        }
    }
}