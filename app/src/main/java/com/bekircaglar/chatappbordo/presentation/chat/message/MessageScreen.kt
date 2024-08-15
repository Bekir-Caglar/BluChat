package com.bekircaglar.chatappbordo.presentation.chat.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bekircaglar.chatappbordo.R
import com.bekircaglar.chatappbordo.presentation.chat.component.SearchTextField
import com.bekircaglar.chatappbordo.presentation.component.ChatAppTopBar
import com.bekircaglar.chatappbordo.ui.theme.ChatAppBordoTheme


@Composable
fun MessageScreen() {
    val userName = "Bekir Çağlar"

    var message by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            ChatAppTopBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_user4),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(44.dp)
                        )
                        Text(
                            text = userName,
                            modifier = Modifier
                                .padding(start = 10.dp),
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        )
                    }
                },
                navigationIcon = Icons.Default.KeyboardArrowLeft,
                onNavigateIconClicked = {},

                actionIcon = Icons.Default.Search,
                onActionIconClicked = {}
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,

            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { /*TODO*/ }, modifier = Modifier.padding(end = 8.dp)) {
                       Icon(imageVector = Icons.Outlined.Add, contentDescription = null , tint = Color.Gray)
                    }
                    SearchTextField(searchText = message, onSearchTextChange ={message = it} )
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.padding(end = 16.dp)
                        ) {
                        Icon(painter = painterResource(id = R.drawable.ic_send), contentDescription = null , tint = MaterialTheme.colorScheme.primary)
                    }
                }


            }
        }


    ) {
        LazyColumn(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer)

        ) {

        }

    }


}

@Preview
@Composable
fun PreviewMessageScreen() {
    ChatAppBordoTheme {
        MessageScreen()
    }
}

