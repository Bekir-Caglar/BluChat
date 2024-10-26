package com.bekircaglar.bluchat.presentation.chat.searchchat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.presentation.chat.component.Chats
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenChatBottomSheet(
    navController: NavController,
    textFieldValue: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<Users>,
    onDismiss: () -> Unit,
    onItemClick: (Users) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = bottomSheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Select contact to chat",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = textFieldValue,
                onValueChange = { onSearchQueryChange(it) },
                label = { Text("Search by phone number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.colors().copy(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                items(searchResults, key = { it.email }) { contact ->
                    val myChat = com.bekircaglar.bluchat.domain.model.Chats(
                        chatRoomId = "",
                        imageUrl = contact.profileImageUrl,
                        name = contact.name,
                        surname = contact.surname,
                        lastMessage = "+90 ${contact.phoneNumber}",
                        messageTime = "",
                        isOnline = contact.status
                    )
                    Chats(
                        chat = myChat,
                        onClick = {
                            onItemClick(contact)
                            coroutineScope.launch { bottomSheetState.hide() }
                        },
                        onImageLoaded = {}
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
