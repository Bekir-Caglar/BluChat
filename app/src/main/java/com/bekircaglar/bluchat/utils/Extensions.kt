package com.bekircaglar.bluchat.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.bekircaglar.bluchat.presentation.shimmer.PlaceholderHighlight
import com.bekircaglar.bluchat.presentation.shimmer.placeholder
import com.bekircaglar.bluchat.presentation.shimmer.shimmer

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.placeholder(
    isLoading: Boolean,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape = RoundedCornerShape(4.dp),
    showShimmerAnimation: Boolean = true
): Modifier = composed {
    val highlight = if (showShimmerAnimation) {
        PlaceholderHighlight.shimmer(highlightColor = Color.Gray)
    } else {
        null
    }
    val specifiedBackgroundColor = backgroundColor.takeOrElse { Color(0xFFDBD6D1).copy(0.6f) }
    Modifier.placeholder(
        color = specifiedBackgroundColor,
        visible = isLoading,
        shape = shape,
        highlight = highlight
    )
}

@Composable
fun Modifier.passwordBorder(isValid: Boolean): Modifier {
    return if (isValid) {
        this
    } else {
        this.border(1.dp, Color.Red, MaterialTheme.shapes.medium)
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.chatBubbleModifier(isSentByMe: Boolean, onLongClick: () -> Unit): Modifier {
    return this
        .fillMaxWidth()
        .padding(8.dp)
        .then(
            if (isSentByMe) {
                Modifier.combinedClickable(enabled = true, onClick = {}, onLongClick = onLongClick)
            } else {
                Modifier
            }
        )
}
fun ImageLoader.getVideoThumbnail(context: Context, videoUrl: String): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(videoUrl, HashMap())
        retriever.getFrameAtTime(100 * 1000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        retriever.release()
    }
}
fun Modifier.conditionalPointerInput(
    isLongPressEnabled: Boolean,
    onLongPress: suspend PointerInputScope.() -> Unit
): Modifier {
    return if (isLongPressEnabled) {
        this.pointerInput(Unit, onLongPress)
    } else {
        this
    }
}