package com.bekircaglar.bluchat.presentation.message.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.utils.IMAGE
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.message.component.MessageTextField

@Composable
fun SendTakenPhotoScreen(imageUrl: String, chatId: String,navController: NavController) {
    val viewModel: SendTakenPhotoViewModel = hiltViewModel()
    var message by remember { mutableStateOf("") }

    println(imageUrl)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberImagePainter(imageUrl),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            MessageTextField(
                searchText = message,
                onSearchTextChange = { newText ->
                    message =
                        newText.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                },
                onSend = {
                    viewModel.sendMessage(
                        imageUrl = imageUrl.toString(),
                        message = message,
                        messageType = IMAGE,
                        chatId = chatId,
                    )
                    navController.navigate(Screens.MessageScreen.createRoute(chatId))

                },
                placeholderText = "Add a caption...",
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        viewModel.sendMessage(
                            imageUrl = imageUrl.toString(),
                            message = message,
                            messageType = IMAGE,
                            chatId = chatId,
                        )
                        navController.navigate(Screens.MessageScreen.createRoute(chatId))

                    }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

    }
}