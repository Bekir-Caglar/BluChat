import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun ChatBubble(
    messageType: String,
    message: Message,
    isSentByMe: Boolean,
    timestamp: String,
    senderName: String,
    senderNameColor: Color,
    onImageClick: (String) -> Unit = {}
) {
    val bubbleColor =
        if (isSentByMe) MaterialTheme.colorScheme.tertiary else Color(0xF7FFFFFF).copy(alpha = 0.6f)
    val alignment = if (isSentByMe) Alignment.End else Alignment.Start

    val shape = if (isSentByMe) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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
                                    message.message?.let { onImageClick(it) }
                                }
                                .align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            message.message!!,
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.padding(top = 8.dp)
                        )
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
