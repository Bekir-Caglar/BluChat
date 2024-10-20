package com.bekircaglar.bluchat.presentation.chatinfo

import VideoThumbnailComposable
import android.content.Intent
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.utils.GROUP
import com.bekircaglar.bluchat.utils.PRIVATE
import com.bekircaglar.bluchat.utils.UiState
import com.bekircaglar.bluchat.VideoPlayerActivity
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.chat.groupchat.GroupChatDialog
import com.bekircaglar.bluchat.presentation.chat.groupchat.SelectGroupMemberDialog
import com.bekircaglar.bluchat.presentation.component.ChatAppTopBar
import kotlinx.coroutines.launch

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
    val uploadImageState by viewModel.uploadImageState.collectAsStateWithLifecycle()
    val uploadedImageUri by viewModel.uploadedImageUri.collectAsStateWithLifecycle()
    val otherUser by viewModel.otherUser.collectAsStateWithLifecycle()

    val chatImages by viewModel.ChatImages.collectAsStateWithLifecycle()

    val chatImagesState by viewModel.chatImagesState.collectAsStateWithLifecycle()

    val chatListState by viewModel.stateOfUserListState.collectAsStateWithLifecycle()

    var selectGroupUserDialog by remember { mutableStateOf(false) }
    var updateGroupInfoDialog by remember { mutableStateOf(false) }

    val chatType = chatRoom.chatType

    val adminId = chatRoom.chatAdminId

    val isCurrentUserAdmin: Boolean = currentUser.uid == adminId

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.getChatRoom(chatId!!)
    }
    LaunchedEffect(Unit) {
        viewModel.getChatImages(chatId!!)
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



    Scaffold(topBar = {
        ChatAppTopBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    if (chatType == PRIVATE) {
                        Text(
                            text = "User Info",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(end = 30.dp)
                        )
                    } else Text(
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
    }) { paddingValues ->

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
            GroupChatDialog(defaultImageUrl = chatRoom.chatImage,
                defaultGroupName = chatRoom.chatName!!,
                selectedUri = uploadedImageUri,
                onDismissRequest = { updateGroupInfoDialog = false },
                onCreateGroupChat = { groupChatName ->
                    if (uploadedImageUri.toString().isEmpty()) {
                        viewModel.updateChatInfo(chatId!!, groupChatName, chatRoom.chatImage!!)
                    } else viewModel.updateChatInfo(
                        chatId!!, groupChatName, uploadedImageUri.toString()
                    )

                    viewModel.getChatRoom(chatId)
                    updateGroupInfoDialog = false
                },
                buttonText = "Update group info",
                isImageLoading = uploadImageState == UiState.Loading,
                onPermissionRequest = { permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES) })
        }
        MyBottomSheetScaffold(
            bottomSheetScaffoldState = bottomSheetScaffoldState,
            imageUrls = chatImages,
            onMediaSelected = {
                val encodedUrl = Uri.encode(it)
                navController?.navigate(Screens.ImageScreen.createRoute(encodedUrl))
            },
        ) {
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
                    Box(contentAlignment = Alignment.Center,
                        modifier = if (isCurrentUserAdmin) Modifier.clickable {
                            updateGroupInfoDialog = true
                        } else Modifier

                    ) {
                        if (chatType == PRIVATE) {
                            val painter = rememberAsyncImagePainter(model = otherUser.profileImageUrl)
                            val painterState = painter.state
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .shadow(
                                        elevation = 5.dp,
                                        shape = CircleShape
                                    )
                                    .background(color = Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                if (painterState is AsyncImagePainter.State.Success) {

                                } else {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .background(
                                                color = Color.White,
                                                shape = CircleShape
                                            )
                                            .size((100 / 3).dp)
                                    )
                                }
                                Image(
                                    painter = painter,
                                    contentDescription = "User Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .shadow(elevation = 5.dp, shape = CircleShape)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                )

                            }

                        } else {
                            val painter = rememberAsyncImagePainter(model = chatRoom.chatImage)
                            val painterState = painter.state
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .shadow(
                                        elevation = 5.dp,
                                        shape = CircleShape
                                    )
                                    .background(color = Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                if (painterState is AsyncImagePainter.State.Success) {

                                } else {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .background(
                                                color = Color.White,
                                                shape = CircleShape
                                            )
                                            .size((100 / 3).dp)
                                    )
                                }
                                Image(
                                    painter = painter,
                                    contentDescription = "Group Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .shadow(elevation = 5.dp, shape = CircleShape)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                )

                            }

                        }

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

                    if (chatType == PRIVATE) {
                        Text(
                            text = otherUser.name + " " + otherUser.surname,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    } else Text(
                        text = chatRoom.chatName ?: "",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )


                    Spacer(modifier = Modifier.height(4.dp))

                    if (chatType == PRIVATE) {
                        Text(
                            text = "+90" + " " + otherUser.phoneNumber,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = "${userList.size + 1} members",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))



                    Column(
                        modifier = Modifier
                            .shadow(elevation = 5.dp, shape = RoundedCornerShape(16.dp))
                            .background(
                                color = MaterialTheme.colorScheme.background,
                            )
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = "Media, link and documents ",
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (chatImages.size >= 5) Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.expand()
                                    }
                                }) {
                                Text("View all ${chatImages.size} ")
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "More",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                        if (chatImages.isEmpty()) {
                            Text(
                                text = "No media found",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(32.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        } else if (chatImagesState == UiState.Loading) {
                            CircularProgressIndicator()
                        }
                        LazyRow(
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(chatImages) { imageUrl ->


                                if (imageUrl.contains(".mp4")) {
                                    VideoThumbnailComposable(context = context,
                                        size = 100.dp,
                                        videoUrl = imageUrl,
                                        onVideoClick = {
                                            val intent =
                                                Intent(context, VideoPlayerActivity::class.java)
                                            intent.putExtra("videoUrl", imageUrl)
                                            context.startActivity(intent)
                                        }
                                    )
                                } else {
                                    val painter = rememberAsyncImagePainter(model = imageUrl)
                                    val painterState = painter.state
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .shadow(
                                                elevation = 5.dp,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .background(color = Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (painterState is AsyncImagePainter.State.Success) {

                                        } else {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .background(
                                                        color = Color.White,
                                                        shape = CircleShape
                                                    )
                                                    .size((100 / 3).dp)
                                            )
                                        }
                                        Image(
                                            painter = painter,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    val encodedUrl = Uri.encode(imageUrl)
                                                    navController?.navigate(
                                                        Screens.ImageScreen.createRoute(
                                                            encodedUrl
                                                        )
                                                    )
                                                },
                                            contentScale = ContentScale.Crop
                                        )

                                    }


                                }

                            }

                        }

                    }

                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {

                        Column(
                            modifier = Modifier
                                .shadow(
                                    elevation = 5.dp,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .background(color = Color.White),
                        ){
                            if (chatListState == UiState.Loading) {
                                CircularProgressIndicator()
                            } else if (chatType == GROUP) {
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
                                    colors = TextFieldDefaults.colors().copy(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedContainerColor = MaterialTheme.colorScheme.secondary,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                                    )
                                )
                            }
                            if (isCurrentUserAdmin) {
                                MemberItem(Users(
                                    uid = "0",
                                    name = "Add participants",
                                    surname = "",
                                    profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/chatappbordo.appspot.com/o/add%20icon.png?alt=media&token=4a2ceced-da09-4083-8515-c2a03a507d72"
                                ), isCurrentUserAdmin = false,
                                    onItemClick = {
                                        selectGroupUserDialog = true
                                    })
                            }

                            if (chatType == GROUP) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.5f)
                                ) {

                                    items(userList.distinctBy { it.uid }, key = { it.uid }) { member ->
                                        MemberItem(member, isCurrentUserAdmin, onUserKicked = {
                                            viewModel.kickUser(chatId = chatId!!, userId = member.uid)
                                            viewModel.getChatRoom(chatId)
                                        })
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))


                    if (isCurrentUserAdmin) {
                        ElevatedButton(
                            onClick = {
                                viewModel.deleteGroup(chatId!!)
                                navController?.navigate(Screens.ChatListScreen.route) {
                                    popUpTo(Screens.ChatInfoScreen.route) { inclusive = true }
                                }
                            },
                            modifier = Modifier.padding(16.dp),
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = Color.Gray,
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.Blue,
                            ),
                        ) {
                            Text(
                                text = "Delete Group", color = Color.Red
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
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = Color.Gray,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.Blue,
                        ),
                    ) {
                        if (chatType == PRIVATE) {
                            Text(
                                text = "Leave Chat", color = Color.Red
                            )
                        } else Text(
                            text = "Leave Group", color = MaterialTheme.colorScheme.error
                        )
                    }

                }

            }
        }
    }
    if (chatListState is UiState.Error){
        (chatListState as UiState.Error).message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun MemberItem(
    member: Users,
    isCurrentUserAdmin: Boolean,
    onUserKicked: () -> Unit = {},
    onItemClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onItemClick() }) {
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
            Icon(imageVector = Icons.Default.Clear,
                contentDescription = "Remove User",
                tint = Color.Red,
                modifier = Modifier.clickable {
                    onUserKicked()
                })
        }
    }
}