package com.bekircaglar.bluchat.presentation.chat.searchchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.presentation.chat.component.Chats

@Composable
fun OpenChatDialog(
    navController: NavController,
    textFieldValue: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<Users>,
    onDismiss: () -> Unit,
    onItemClick: (Users) -> Unit,
) {

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier

            ) {
                TextField(
                    value = textFieldValue,
                    onValueChange = { onSearchQueryChange(it) },
                    label = {Text("Search user by phone number")},
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(searchResults, key = { it.email}) { contact ->
                        val myChat = com.bekircaglar.bluchat.domain.model.Chats(
                            chatRoomId = "",
                            imageUrl = contact.profileImageUrl,
                            name = contact.name,
                            surname = contact.surname,
                            lastMessage = "",
                            messageTime = "",
                            isOnline = contact.status
                        )
                        Chats(myChat, onClick = {
                            onItemClick(contact)
                        })
                    }
                }
            }
        }
    }


}