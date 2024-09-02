package com.bekircaglar.chatappbordo.presentation.message.component
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatBubble(message: String, isSentByMe: Boolean) {
    val bubbleColor = if (isSentByMe) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
    val alignment = if (isSentByMe) Alignment.End else Alignment.Start

    // modifier extension olarak yap
    //constant olarak yap
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
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .background(bubbleColor, shape),
            shape = shape,
            color = bubbleColor
        ) {
            Text(
                text = message,
                modifier = Modifier
                    .padding(12.dp),
                color = if (isSentByMe) Color.White else Color.Black,
                fontSize = 16.sp,
                textAlign = if (isSentByMe) TextAlign.End else TextAlign.Start
            )
        }
    }
}
@Preview
@Composable
fun ChatBubblePreview() {
    ChatBubble(message = "Message", isSentByMe = false)
}