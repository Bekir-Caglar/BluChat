import android.widget.Space
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.domain.model.Message
import com.bekircaglar.bluchat.ui.theme.BabyBlue
import com.bekircaglar.bluchat.ui.theme.BlueMinus20

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    messageType: String,
    message: Message,
    isSentByMe: Boolean,
    timestamp: String,
    senderName: String,
    senderNameColor: Color,
    onImageClick: (String) -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
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
        }
    )

    Row(
        modifier = if (isSentByMe){
            Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(enabled = true, onClick = {}, onLongClick = {
                expanded = true
            })}else {
            Modifier.fillMaxWidth()
                .padding(8.dp)
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
                        Image(
                            rememberImagePainter(data = message.imageUrl),
                            contentDescription = "Image Message",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(shape = RoundedCornerShape(12.dp))
                                .clickable {
                                    message.imageUrl?.let { onImageClick(it) }
                                }
                                .align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.Crop
                        )
                        if (message.message != "") {
                            Text(
                                message.message!!,
                                color = if (isSentByMe) Color.White else Color(0xFF001F3F),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    Text(
                        text = timestamp,
                        color = if (isSentByMe) Color.White else Color.Gray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .padding(16.dp)
            .width(120.dp)
    ) {
        DropdownMenuItem(
            enabled = true,
            onClick = {
                onEditClick()
                onDismissRequest()
            }, text = {
                Row {
                    Text(text = "Edit")
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }

            }
        )
        DropdownMenuItem(
            enabled = true,
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
            })

    }
    // Delete Option

}
