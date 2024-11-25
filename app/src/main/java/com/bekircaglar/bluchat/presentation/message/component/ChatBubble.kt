@file:OptIn(ExperimentalFoundationApi::class)

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.ImageLoader
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.BuildConfig
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.domain.model.message.Message
import com.bekircaglar.bluchat.domain.model.message.MessageType
import com.bekircaglar.bluchat.ui.theme.chatAnswerTextColor
import com.bekircaglar.bluchat.utils.chatBubbleModifier
import com.bekircaglar.bluchat.utils.getVideoThumbnail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val BubblePadding = 12.dp
private val BubbleShapeSent = RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
private val BubbleShapeReceived = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
private val BubbleColorReceived = Color(0xF7FFFFFF).copy(alpha = 0.6f)
private val TimestampFontSize = 12.sp
private val SenderNameFontSize = 14.sp
private val MessageFontSize = 16.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    context: Context,
    message: Message,
    isSentByMe: Boolean,
    timestamp: String,
    senderName: String,
    senderNameColor: Color,
    onImageClick: (String) -> Unit = {},
    onVideoClick: (String) -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onPinMessageClick: () -> Unit = {},
    onUnPinMessageClick: () -> Unit = {},
    onStarMessage: () -> Unit = {},
    onUnStarMessage: () -> Unit = {},
    onSwipeRight: (Message) -> Unit = {},
    replyMessage: Message? = null,
    replyMessageName: String? = null
) {
    val BubbleColorSent = MaterialTheme.colorScheme.tertiary
    var expanded by remember { mutableStateOf(false) }

    MessageDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onPinMessageClick = onPinMessageClick,
        message = message,
        onUnPinMessageClick = onUnPinMessageClick,
        onStarMessage = onStarMessage,
        onUnStarMessage = onUnStarMessage
    )

    Row(
        modifier = Modifier
            .chatBubbleModifier(isSentByMe) { expanded = true }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 50 && !isSentByMe) {
                        onSwipeRight(message)
                    }
                }
            },
        horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.7f),
            horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentSize()
                    .shadow(
                        elevation = 1.dp,
                        shape = if (isSentByMe) BubbleShapeSent else BubbleShapeReceived
                    )
                    .background(
                        if (isSentByMe) BubbleColorSent else BubbleColorReceived,
                        shape = if (isSentByMe) BubbleShapeSent else BubbleShapeReceived
                    ),
                shape = if (isSentByMe) BubbleShapeSent else BubbleShapeReceived,
                color = if (isSentByMe) BubbleColorSent else BubbleColorReceived
            ) {
                Column(modifier = Modifier.padding(BubblePadding)) {
                    replyMessage?.let {
                        ReplyMessage(replyMessageName, it)
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                    if (!isSentByMe) {
                        Text(
                            text = senderName,
                            color = senderNameColor,
                            fontSize = SenderNameFontSize,
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    when (message.messageType) {
                        MessageType.TEXT.toString() -> TextMessage(message, isSentByMe)
                        MessageType.IMAGE.toString() -> ImageMessage(
                            message,
                            isSentByMe,
                            onImageClick,
                            { expanded = true })

                        MessageType.VIDEO.toString() -> VideoMessage(
                            context,
                            message,
                            onVideoClick,
                            isSentByMe
                        )

                        MessageType.LOCATION.toString() -> LocationMessage(
                            isSentByMe = isSentByMe,
                            context,
                            message,
                            { expanded = true })
                    }
                    MessageTimestamp(message, timestamp, isSentByMe)

                }
            }
        }
    }
}

@Composable
fun ReplyMessage(replyMessageName: String?, replyMessage: Message) {
    Row(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(end = 8.dp)
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = replyMessageName ?: "Anonymous",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = replyMessage.message ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TextMessage(message: Message, isSentByMe: Boolean) {
    Text(
        text = message.useMessage,
        color = if (isSentByMe) Color.White else chatAnswerTextColor,
        fontSize = MessageFontSize,
        textAlign = TextAlign.Start
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageMessage(
    message: Message,
    isSentByMe: Boolean,
    onImageClick: (String) -> Unit,
    changeExpanded: () -> Unit
) {
    val imageUrl = message.useImageUrl
    Image(
        painter = rememberImagePainter(data = imageUrl),
        contentDescription = "Image Message",
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                enabled = true,
                onClick = { onImageClick(imageUrl) },
                onLongClick = { if (isSentByMe) changeExpanded() }
            ),
        contentScale = ContentScale.Crop
    )

    if (message.useMessage.isNotEmpty()) {
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = message.useMessage,
            color = if (isSentByMe) Color.White else Color(0xFF001F3F),
            fontSize = MessageFontSize,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun VideoMessage(
    context: Context,
    message: Message,
    onVideoClick: (String) -> Unit,
    isSentByMe: Boolean
) {
    val videoUrl = message.useVideoUrl
    VideoThumbnailComposable(
        context = context,
        videoUrl = videoUrl,
        onVideoClick = { onVideoClick(videoUrl) }
    )

    Spacer(modifier = Modifier.size(8.dp))
    Text(
        text = message.useMessage,
        color = if (isSentByMe) Color.White else Color(0xFF001F3F),
        fontSize = MessageFontSize,
        textAlign = TextAlign.Start
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocationMessage(
    isSentByMe: Boolean,
    context: Context,
    message: Message,
    changeExpanded: () -> Unit
) {
    val latitude = message.useLatitude
    val longitude = message.useLongitude
    val mapsApiKey = BuildConfig.GOOGLE_MAPS_KEY

    val mapPhotoUrl =
        "https://maps.googleapis.com/maps/api/staticmap?center=$latitude,$longitude&zoom=15&size=400x400&markers=color:red%7C$latitude,$longitude&key=$mapsApiKey"
    val mapUrl = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
    Image(
        painter = rememberImagePainter(data = mapPhotoUrl),
        contentDescription = "Location",
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                enabled = true,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
                    startActivity(context, intent, null)
                },
                onLongClick = { if (isSentByMe) changeExpanded() }
            ),
        contentScale = ContentScale.Crop
    )

    if (message.useLocationName.isNotEmpty()) {
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = message.useLocationName,
            color = if (isSentByMe) Color.White else Color(0xFF001F3F),
            fontSize = MessageFontSize,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun MessageTimestamp(message: Message, timestamp: String, isSentByMe: Boolean) {
    Row(verticalAlignment = Alignment.Bottom
    ) {
        if (message.isEdited) {
            Text(
                text = "Edited",
                color = Color.LightGray,
                fontSize = TimestampFontSize,
                textAlign = TextAlign.End,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        }
        Text(
            text = timestamp,
            color = if (isSentByMe) Color.White else Color.Gray,
            fontSize = TimestampFontSize,
            textAlign = TextAlign.End,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        if (isSentByMe) {
            Icon(
                painter = painterResource(R.drawable.double_tick),
                contentDescription = "Tick",
                tint = if (message.isRead) Color.Green else Color.Gray,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
@Composable
fun MessageDropdownMenu(
    expanded: Boolean,
    message: Message,
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPinMessageClick: () -> Unit,
    onUnPinMessageClick: () -> Unit,
    onStarMessage: () -> Unit,
    onUnStarMessage: () -> Unit
) {

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.large)
            .padding(16.dp)
            .width(150.dp)
    ) {
        DropdownMenuItem(
            enabled = true,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .shadow(elevation = 5.dp, shape = MaterialTheme.shapes.medium)
                .background(color = MaterialTheme.colorScheme.background),
            onClick = {
                onEditClick()
                onDismissRequest()
            },
            text = {
                Row(

                ) {
                    Text(text = "Edit")
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }

            },
        )
        DropdownMenuItem(
            enabled = true,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .shadow(elevation = 5.dp, shape = MaterialTheme.shapes.medium)
                .background(color = MaterialTheme.colorScheme.background),
            onClick = {
                if (message.pinned == true) {
                    onUnPinMessageClick()
                } else {
                    onPinMessageClick()
                }
                onDismissRequest()
            },
            text = {
                Row {
                    Text(
                        text = if (message.pinned == true) "Unpin message" else "Pin message"
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        painter = painterResource(R.drawable.baseline_push_pin_24),
                        contentDescription = "Delete",
                    )
                }
            }
        )

        DropdownMenuItem(
            enabled = true,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .shadow(elevation = 5.dp, shape = MaterialTheme.shapes.medium)
                .background(color = MaterialTheme.colorScheme.background),
            onClick = {
                if (message.starred == true) {
                    onUnStarMessage()
                } else {
                    onStarMessage()
                }
                onDismissRequest()
            },
            text = {
                Row {
                    Text(
                        text = if (message.starred == true) "Unstar message" else "Star message"
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Star",
                    )
                }
            }
        )

        DropdownMenuItem(
            enabled = true,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .shadow(elevation = 5.dp, shape = MaterialTheme.shapes.medium)
                .background(color = MaterialTheme.colorScheme.background),
            onClick = {
                onDeleteClick()
                onDismissRequest()
            }, text = {
                Row {
                    Text(
                        text = "Delete",
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        )


    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoThumbnailComposable(
    context: Context,
    videoUrl: String,
    onVideoClick: (String) -> Unit,
    size: Dp = 200.dp,
    isShapeShouldSquare: Boolean = true
) {
    val imageLoader = remember { ImageLoader(context) }
    var videoThumbnail by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(videoUrl) {
        videoThumbnail = withContext(Dispatchers.IO) {
            imageLoader.getVideoThumbnail(context, videoUrl)
        }
    }

    videoThumbnail?.let { bitmap ->
        Box {
            Image(
                painter = rememberImagePainter(bitmap),
                contentDescription = "Video Thumbnail",
                modifier = if (isShapeShouldSquare) {
                    Modifier
                        .size(size)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .combinedClickable(
                            enabled = true,
                            onClick = {
                                onVideoClick(videoUrl)
                            },
                            onLongClick = {}
                        )
                } else {
                    Modifier
                        .fillMaxWidth()
                },
                contentScale = ContentScale.Crop
            )

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "play button",
                modifier = Modifier
                    .background(color = Color.Black.copy(alpha = 0.3f), shape = CircleShape)
                    .size((size / 3))
                    .align(alignment = Alignment.Center)
            )


        }
    } ?: run {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .background(Color.Black, shape = RoundedCornerShape(12.dp))
        ) {
            CircularProgressIndicator(modifier = Modifier.size(size / 4))

        }
    }
}