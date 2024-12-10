package com.bekircaglar.bluchat.presentation.message.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ImageScreen(imageUrl: String,animatedVisibilityScope: AnimatedVisibilityScope,sharedTransitionScope: SharedTransitionScope) {

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
                    .sharedElement(
                        state = rememberSharedContentState("sendImage"),
                        boundsTransform = { _,_ ->
                            tween(500)
                        },
                        animatedVisibilityScope = animatedVisibilityScope
                        ),
                contentScale = ContentScale.Fit
            )
        }
    }
}