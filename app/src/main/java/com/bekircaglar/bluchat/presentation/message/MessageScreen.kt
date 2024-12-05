package com.bekircaglar.bluchat.presentation.message

import ChatBubble
import VideoThumbnailComposable
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.BuildConfig
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.VideoPlayerActivity
import com.bekircaglar.bluchat.domain.model.message.Message
import com.bekircaglar.bluchat.domain.model.SheetOption
import com.bekircaglar.bluchat.domain.model.message.MessageType
import com.bekircaglar.bluchat.loadThemePreference
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.component.ChatAppTopBar
import com.bekircaglar.bluchat.presentation.message.component.AudioMessageBubble
import com.bekircaglar.bluchat.presentation.message.component.ImageSendBottomSheet
import com.bekircaglar.bluchat.presentation.message.component.MessageAlertDialog
import com.bekircaglar.bluchat.presentation.message.component.MessageBottomBar
import com.bekircaglar.bluchat.presentation.message.component.MessageExtraBottomSheet
import com.bekircaglar.bluchat.presentation.message.component.MessageTextField
import com.bekircaglar.bluchat.presentation.message.component.formatDuration
import com.bekircaglar.bluchat.utils.AudioRecorderManager
import com.bekircaglar.bluchat.utils.UiState
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class
)
@Composable
fun MessageScreen(
    navController: NavController,
    chatId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val viewModel: MessageViewModel = hiltViewModel()
    val context = LocalContext.current
    val userInfo by viewModel.userData.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val uploadedImage by viewModel.uploadedImageUri.collectAsStateWithLifecycle()
    val uploadedVideo by viewModel.uploadedVideoUri.collectAsStateWithLifecycle()
    val selectedImage by viewModel.selectedImageUri.collectAsStateWithLifecycle()
    val uploadedAudioUri by viewModel.uploadedAudioUri.collectAsStateWithLifecycle()
    val isKickedOrGroupDeleted by viewModel.isKickedOrGroupDeleted.collectAsState()
    val currentUser = viewModel.currentUser
    var messageText by remember { mutableStateOf("") }
    var bottomSheetState by remember { mutableStateOf(false) }
    var imageSendDialogState by remember { mutableStateOf(false) }
    var chatMessage by remember { mutableStateOf("") }
    var editedMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    var pageNumber by remember { mutableStateOf(1) }

    var emojiState by remember { mutableStateOf(false) }

    var selectedMessageForDeletion by remember { mutableStateOf<Message?>(null) }
    var selectedMessageForPin by remember { mutableStateOf<Message?>(null) }
    var selectedMessageForEdit by remember { mutableStateOf<Message?>(null) }
    var selectedMessageForReply by remember { mutableStateOf<Message?>(null) }

    var videoUploadState by remember { mutableStateOf(false) }
    var imageUploadState by remember { mutableStateOf(false) }
    var replyState by remember { mutableStateOf(false) }


    val pinnedMessages by viewModel.pinnedMessages.collectAsStateWithLifecycle()

    val screenState by viewModel.uiState.collectAsStateWithLifecycle()
    val moreMessageState by viewModel.moreMessageState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getPinnedMessages(chatId)
    }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onImageSelected(it)
            imageUploadState = true
        }
    }

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onVideoSelected(it)
            videoUploadState = true
        }
    }

    val permissionLauncherForVideo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            videoLauncher.launch("video/*")
        } else {
            Toast.makeText(context, "Galeriye erişim izni gerekli!", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncherForGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Galeriye erişim izni gerekli!", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncherForCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navController.navigate(Screens.CameraScreen.createRoute(chatId))
        } else {
            Toast.makeText(context, "Kamera erişim izni gerekli!", Toast.LENGTH_SHORT).show()
        }
    }

    val groupedMessages = messages.reversed().groupBy { message ->
        convertTimestampToDay(message.timestamp!!)
    }

    LaunchedEffect(key1 = chatId) {
        viewModel.getChatRoom(chatId)
        viewModel.createMessageRoom(chatId)
        viewModel.loadInitialMessages(chatId)
    }

    val startPagination by remember {
        derivedStateOf {
            val lastIndex = (pageNumber * 15) - listState.firstVisibleItemIndex
            val totalItemCount = messages.size
            lastIndex >= totalItemCount - 3
        }
    }
    LaunchedEffect(startPagination) {
        if (startPagination && messages.size >= 15) {
            viewModel.loadMoreMessages(moreLastKey = messages.lastOrNull()?.messageId, chatId)
            pageNumber++
        }
    }

    LaunchedEffect(Unit) {
        val groupId = chatId
        val userId = currentUser.uid
        viewModel.observeGroupAndUserStatus(groupId, userId)
    }

    LaunchedEffect(isKickedOrGroupDeleted) {
        if (isKickedOrGroupDeleted) {
            navController.navigate(Screens.ChatListScreen.route) {
                popUpTo(Screens.MessageScreen.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
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
                    with(sharedTransitionScope) {
                        Image(
                            painter = rememberImagePainter(data = userInfo?.profileImageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(44.dp)
                                .sharedElement(
                                    state = rememberSharedContentState(key = "profileImage"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ ->
                                        tween(500)
                                    }
                                )
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background, CircleShape)


                        )
                        Column {
                            Text(
                                text = userInfo?.name ?: "",
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .sharedElement(
                                        state = rememberSharedContentState(key = "name"),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        boundsTransform = { _, _ ->
                                            tween(1000)
                                        }
                                    ),
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            )
                            if (userInfo?.status == true) {
                                Text(
                                    text = "Online",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }


            }, navigationIcon = Icons.Default.KeyboardArrowLeft,
                onNavigateIconClicked = {
                    navController.navigate(Screens.ChatListScreen.route)
                },
                actionIcon = Icons.Outlined.Star,
                onActionIconClicked = {
                    navController.navigate(Screens.StarredMessagesScreen.createRoute(chatId))
                },
                onActionIcon2Clicked = {
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.background(Color.Transparent)
            ) {
                if (replyState) {
                    var senderName by remember { mutableStateOf("") }
                    selectedMessageForReply?.senderId?.let {
                        viewModel.getUserNameFromUserId(
                            it, onResult = {
                                senderName = it
                            })
                    }
                    selectedMessageForReply?.let {
                        Reply(
                            messageSenderName = senderName,
                            message = it,
                            onDismiss = {
                                replyState = false
                                selectedMessageForReply = null
                            })
                    }
                }
                MessageBottomBar(
                    onAttachClicked = {
                        bottomSheetState = true
                    },
                    onSendMessage = { message ->
                        viewModel.sendMessage(
                            message = message,
                            chatId = chatId,
                            messageType = MessageType.TEXT.toString(),
                            replyTo = if (selectedMessageForReply != null) selectedMessageForReply?.messageId else ""
                        )
                        replyState = false
                        selectedMessageForReply = null
                    },
                    onCameraClicked = {
                        permissionLauncherForCamera.launch(android.Manifest.permission.CAMERA)
                    },
                    onSendAudio = { audioPath, audioDuration ->
                        viewModel.uploadAudio(
                            audioPath = audioPath,
                            audioDuration = audioDuration,
                            chatId = chatId,
                            replyTo = if (selectedMessageForReply != null) selectedMessageForReply?.messageId else ""
                        )


                    }
                )


            }

        }

    ) {
        Box(modifier = Modifier.padding(it)) {
            if (screenState is UiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                    )
                }
            } else if (screenState is UiState.Success) {
                LaunchedEffect(Unit) {
                    listState.scrollToItem(messages.lastIndex + 1)

                }
                if (messages.isNotEmpty()) {
                    val lastMessageId = messages.first().messageId
                    LaunchedEffect(lastMessageId) {
                        listState.scrollToItem(messages.lastIndex + 1)

                    }
                }
                LaunchedEffect(listState.isScrollInProgress) {
                    listState.layoutInfo.visibleItemsInfo.forEach { visibleItem ->
                        val myMessage = messages.find {
                            visibleItem.key == it.messageId
                        }
                        if (myMessage?.read == false && myMessage.senderId != currentUser.uid)
                            viewModel.markMessageAsRead(visibleItem.key.toString(), chatId)

                    }
                }

                if (imageSendDialogState) {
                    ImageSendBottomSheet(
                        imageResId = uploadedImage!!,
                        onSend = { imageResId, message ->
                            viewModel.sendMessage(
                                message = message,
                                chatId = chatId,
                                imageUrl = imageResId,
                                messageType = MessageType.IMAGE.toString(),
                            )
                            imageSendDialogState = false
                        },
                        onDismiss = {
                            imageSendDialogState = false
                        }
                    )
                }

                LaunchedEffect(uploadedImage) {
                    imageUploadState = false
                    if (uploadedImage != null) {
                        imageSendDialogState = true
                    }
                }

                LaunchedEffect(uploadedVideo) {
                    videoUploadState = false
                    if (uploadedVideo != null) {
                        val encodedVideoUrl = URLEncoder.encode(
                            uploadedVideo.toString(),
                            StandardCharsets.UTF_8.toString()
                        )
                        navController.navigate(
                            Screens.SendTakenPhotoScreen.createRoute(
                                encodedVideoUrl,
                                chatId
                            )
                        )
                    }
                }

                if (bottomSheetState) {
                    MessageExtraBottomSheet(
                        onDismiss = { bottomSheetState = false },
                        onClicked = { option ->
                            when (option) {
                                "Photos" -> {
                                    permissionLauncherForGallery.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                                }

                                "Video" -> {
                                    permissionLauncherForVideo.launch(android.Manifest.permission.READ_MEDIA_VIDEO)
                                }

                                "Camera" -> {
                                    permissionLauncherForCamera.launch(android.Manifest.permission.CAMERA)
                                }

                                "Location" -> {
                                    navController.navigate(
                                        Screens.MapScreen.createRoute(
                                            chatId
                                        )
                                    )
                                }
                            }
                        },
                        myList = listOf(
                            SheetOption("Photos", R.drawable.ic_photos),
                            SheetOption("Video", R.drawable.ic_video_camera_media),
                            SheetOption("Camera", R.drawable.ic_camera),
                            SheetOption("Location", R.drawable.ic_location),
//                            SheetOption("Contact", R.drawable.ic_user_square),
                        )
                    )
                }

                if (messages.isNotEmpty()) {
                    Column(modifier = Modifier.padding(it)) {
                        if (moreMessageState is UiState.Loading) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    val lastPinnedMessage = pinnedMessages.lastOrNull()
                    Column {
                        if (pinnedMessages.lastOrNull() != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(
                                                    alpha = 0.3f
                                                )
                                            )
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_push_pin_24),
                                            contentDescription = null,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                    val formattedDuration = formatDuration(
                                        lastPinnedMessage?.useAudioDuration?.times(1000)
                                            ?: 0
                                    )
                                    val voiceMessage =
                                        if (lastPinnedMessage?.messageType == MessageType.AUDIO.toString()) "Voice message 🎤 ($formattedDuration)"
                                        else ""

                                    Text(
                                        text = when (lastPinnedMessage?.messageType) {

                                            MessageType.TEXT.toString() -> if (lastPinnedMessage.message.isNullOrEmpty()) "Text message" else lastPinnedMessage.useMessage
                                            MessageType.IMAGE.toString() -> if (lastPinnedMessage.message.isNullOrEmpty()) "Image 🏞️" else lastPinnedMessage.useMessage
                                            MessageType.VIDEO.toString() -> if (lastPinnedMessage.message.isNullOrEmpty()) "Video 🎥" else lastPinnedMessage.useMessage
                                            MessageType.LOCATION.toString() -> if (lastPinnedMessage.message.isNullOrEmpty()) "Location 🗺️" else lastPinnedMessage.useMessage
                                            MessageType.AUDIO.toString() -> if (lastPinnedMessage.message.isNullOrEmpty()) voiceMessage else lastPinnedMessage.useMessage
                                            else -> ""

                                        },
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .padding(start = 8.dp)
                                    )
                                }

                                when (lastPinnedMessage?.messageType) {
                                    MessageType.IMAGE.toString() -> {
                                        Image(
                                            painter = rememberImagePainter(data = lastPinnedMessage.useImageUrl),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(MaterialTheme.shapes.medium)
                                        )
                                    }

                                    MessageType.VIDEO.toString() -> {
                                        VideoThumbnailComposable(
                                            context = context,
                                            size = 50.dp,
                                            videoUrl = lastPinnedMessage.useVideoUrl,
                                            onVideoClick = {}
                                        )


                                    }

                                    MessageType.LOCATION.toString() -> {
                                        val latitude = lastPinnedMessage.useLatitude
                                        val longitude = lastPinnedMessage.useLongitude
                                        val mapsApiKey = BuildConfig.GOOGLE_MAPS_KEY

                                        val mapUrl =
                                            "https://maps.googleapis.com/maps/api/staticmap?center=$latitude,$longitude&zoom=15&size=400x400&markers=color:red%7C$latitude,$longitude&key=$mapsApiKey"
                                        Image(
                                            painter = rememberImagePainter(data = mapUrl),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(MaterialTheme.shapes.medium)
                                        )
                                    }


                                }
                            }

                        }
                        LazyColumn(
                            state = listState,
                            reverseLayout = false,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primary.copy(0.2f))
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
                                            .background(
                                                MaterialTheme.colorScheme.surface.copy(
                                                    alpha = 0.5f
                                                )
                                            )
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
                                    key = { _, message ->
                                        message.messageId ?: 0
                                    }) { _, message ->
                                    if (message != null) {
                                        val timestamp =
                                            convertTimestampToDate(message.timestamp!!)
                                        val senderId = message.senderId

                                        var senderName by remember { mutableStateOf("") }
                                        LaunchedEffect(senderId) {
                                            viewModel.getUserNameFromUserId(senderId!!) { name ->
                                                senderName = name
                                            }
                                        }
                                        var replyedMessage by remember {
                                            mutableStateOf<Message?>(
                                                null
                                            )
                                        }
                                        if (message.replyTo != "") {
                                            LaunchedEffect(replyedMessage) {
                                                viewModel.getMessageById(
                                                    message.replyTo.toString(),
                                                    chatId,
                                                    onResult = {
                                                        replyedMessage = it
                                                    })

                                            }
                                        }

                                        var senderReplyName by remember { mutableStateOf("") }
                                        LaunchedEffect(replyedMessage) {
                                            replyedMessage?.senderId?.let { it1 ->
                                                viewModel.getUserNameFromUserId(it1) { name ->
                                                    senderReplyName = name
                                                }
                                            }
                                        }

                                        val senderNameColor =
                                            viewModel.getUserColor(senderId!!)
                                        message.messageType?.let { messageType ->
                                            ChatBubble(
                                                context = context,
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
                                                onVideoClick = { videoUrl ->
                                                    val intent = Intent(
                                                        context,
                                                        VideoPlayerActivity::class.java
                                                    ).apply {
                                                        putExtra("videoUri", videoUrl)
                                                    }
                                                    context.startActivity(intent)
                                                },
                                                onEditClick = {
                                                    selectedMessageForEdit = message
                                                },
                                                onDeleteClick = {
                                                    selectedMessageForDeletion = message
                                                },
                                                onPinMessageClick = {
                                                    viewModel.pinMessage(message, chatId)
//                                                viewModel.getPinnedMessages(chatId)
                                                },
                                                onUnPinMessageClick = {
                                                    viewModel.unPinMessage(message, chatId)
//                                                viewModel.getPinnedMessages(chatId)
                                                },
                                                onStarMessage = {
                                                    viewModel.starMessage(message, chatId)
                                                },
                                                onUnStarMessage = {
                                                    viewModel.unStarMessage(message, chatId)
                                                },
                                                onSwipeRight = {
                                                    selectedMessageForReply = it
                                                    replyState = true
                                                },
                                                replyMessage = replyedMessage,
                                                replyMessageName = senderReplyName
                                            )

                                        }
                                    }
                                }
                            }
                        }
                    }


                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Let's start chatting",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                if (selectedMessageForEdit != null && selectedMessageForEdit?.senderId == currentUser.uid) {
                    Dialog(onDismissRequest = { selectedMessageForEdit = null }) {
                        Column {
                            ChatBubble(
                                message = selectedMessageForEdit!!,
                                isSentByMe = selectedMessageForEdit!!.senderId == currentUser.uid,
                                timestamp = convertTimestampToDate(selectedMessageForEdit!!.timestamp!!),
                                senderName = "",
                                onImageClick = { },
                                senderNameColor = Color.Transparent,
                                onEditClick = { },
                                onDeleteClick = { },
                                context = context
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = MaterialTheme.shapes.medium
                                    )
                            ) {
                                MessageTextField(
                                    searchText = editedMessage,
                                    onSearchTextChange = { newText ->
                                        editedMessage =
                                            newText.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                    },
                                    onSend = {
                                        viewModel.editMessage(
                                            messageId = selectedMessageForEdit!!.messageId!!,
                                            chatId = chatId,
                                            message = editedMessage
                                        )
                                        selectedMessageForEdit = null
                                    },
                                    placeholderText = "Edit your message",
                                    modifier = Modifier
                                        .width(250.dp)
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(
                                    onClick = {
                                        viewModel.editMessage(
                                            messageId = selectedMessageForEdit!!.messageId!!,
                                            chatId = chatId,
                                            message = editedMessage
                                        )
                                        selectedMessageForEdit = null

                                    }, modifier = Modifier.padding(end = 16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send edited message",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
                if (selectedMessageForDeletion != null && selectedMessageForDeletion?.senderId == currentUser.uid) {
                    MessageAlertDialog(
                        message = selectedMessageForDeletion!!,
                        onDismiss = { selectedMessageForDeletion = null },
                        onConfirm = {
                            viewModel.deleteMessage(
                                chatId,
                                selectedMessageForDeletion!!.messageId!!
                            )
                            selectedMessageForDeletion = null
                        },
                        context = context
                    )
                }
            }
        }
    }
    if (videoUploadState || imageUploadState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun Reply(messageSenderName: String, message: Message, onDismiss: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary.copy(0.15f))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(50.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(end = 8.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = messageSenderName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            when (message.messageType) {
                MessageType.TEXT.toString() -> {
                    Text(
                        text = message.useMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                MessageType.IMAGE.toString() -> {
                    Text(
                        text = message.message ?: "Image 🏞️",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }

                MessageType.VIDEO.toString() -> {
                    Text(
                        text = message.message ?: "Video 🎥",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                MessageType.LOCATION.toString() -> {
                    Text(
                        text = message.message ?: "Location 🗺️",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                MessageType.AUDIO.toString() -> {
                    val formattedDuration = formatDuration(message.useAudioDuration * 1000)
                    Text(
                        text = "Voice message 🎤 (${formattedDuration})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

            }
        }


        IconButton(onClick = { onDismiss() }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove quoted message",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
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