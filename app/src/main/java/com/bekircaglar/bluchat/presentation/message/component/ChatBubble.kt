import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.domain.model.Message
import com.bekircaglar.bluchat.utils.chatBubbleModifier
import com.bekircaglar.bluchat.utils.getVideoThumbnail

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    context: Context,
    messageType: String,
    message: Message,
    isSentByMe: Boolean,
    timestamp: String,
    senderName: String,
    senderNameColor: Color,
    onImageClick: (String) -> Unit = {},
    onvVideoClick: (String) -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onPinMessageClick: () -> Unit = {},
    onUnPinMessageClick: () -> Unit = {},
    onStarMessage: () -> Unit = {},
    onUnStarMessage: () -> Unit = {}
) {
    val bubbleColor =
        if (isSentByMe) MaterialTheme.colorScheme.tertiary else Color(0xF7FFFFFF).copy(alpha = 0.6f)
    val alignment = if (isSentByMe) Alignment.End else Alignment.Start

    val shape = if (isSentByMe) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }
    var expanded by remember { mutableStateOf(false) }

    MessageDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onEditClick = {
            onEditClick()
        },
        onDeleteClick = {
            onDeleteClick()
        },
        onPinMessageClick = {
            onPinMessageClick()
        },
        message = message,
        onUnPinMessageClick = {
            onUnPinMessageClick()
        },
        onStarMessage = {
            onStarMessage()
        },
        onUnStarMessage = {
            onUnStarMessage()
        }
    )

    Row(
        modifier = Modifier.chatBubbleModifier(isSentByMe) {
            expanded = true
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
                    .shadow(elevation = 1.dp, shape = shape)
                    .background(bubbleColor, shape = shape),
                shape = shape,
                color = bubbleColor
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    if (!isSentByMe) {
                        Text(
                            text = senderName,
                            color = senderNameColor,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))

                    if (messageType == "text") {
                        Text(
                            text = message.message!!,
                            color = if (isSentByMe) Color.White else Color(0xFF001F3F),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start
                        )
                    } else if (messageType == "image") {
                        if (message.imageUrl?.contains(".mp4") == true) {
                            VideoThumbnailComposable(
                                context = context,
                                videoUrl = message.imageUrl,
                                onVideoClick = { onvVideoClick(it) }
                            )
                        } else {
                            Image(
                                painter = rememberImagePainter(data = message.imageUrl),
                                contentDescription = "Image Message",
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .combinedClickable(
                                        enabled = true,
                                        onClick = {
                                            onImageClick(message.imageUrl!!)
                                        },
                                        onLongClick = {
                                            expanded = true
                                        }
                                    )
                                    .align(Alignment.CenterHorizontally),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = message.message ?: "",
                            color = if (isSentByMe) Color.White else Color(0xFF001F3F),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    if (message.edited == true) {
                        Row(
                            modifier = Modifier.align(Alignment.End),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Edited",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Text(
                                text = timestamp,
                                color = if (isSentByMe) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.End,
                            )
                            if (isSentByMe) {
                                Icon(
                                    painter = painterResource(R.drawable.double_tick),
                                    contentDescription = "Tick",
                                    tint = if (message.read == true) Color.Green else Color.Gray,
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.align(Alignment.End),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = timestamp,
                                color = if (isSentByMe) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.End,
                            )
                            if (isSentByMe) {
                                Icon(
                                    painter = painterResource(R.drawable.double_tick),
                                    contentDescription = "Tick",
                                    tint = if (message.read == true) Color.Green else Color.Gray,
                                )
                            }
                        }

                    }
                }
            }
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
    onStarMessage : () -> Unit,
    onUnStarMessage : () -> Unit
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
                if (message.pinned == true){
                    onUnPinMessageClick()
                }else{
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
                if (message.starred == true){
                    onUnStarMessage()
                }else{
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
                .background(color = MaterialTheme.colorScheme.background)
            ,
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
fun VideoThumbnailComposable(context:Context, videoUrl: String, onVideoClick: (String) -> Unit, isShapeShouldSquare:Boolean = true) {

    val imageLoader = remember { ImageLoader(context) }
    val videoThumbnail by remember {
        mutableStateOf(imageLoader.getVideoThumbnail(context, videoUrl))
    }

    videoThumbnail?.let { bitmap ->
        Box(){
            Image(
                painter = rememberImagePainter(bitmap),
                contentDescription = "Video Thumbnail",
                modifier = if (isShapeShouldSquare){
                    Modifier
                        .size(200.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .combinedClickable(
                            enabled = true,
                            onClick = {
                                onVideoClick(videoUrl)
                            },
                            onLongClick = {
                            }
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
                    .size(60.dp)
                    .align(alignment = Alignment.Center)
            )
        }
    }
}
