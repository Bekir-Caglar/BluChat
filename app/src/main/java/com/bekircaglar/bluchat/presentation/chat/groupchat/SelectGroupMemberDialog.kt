package com.bekircaglar.bluchat.presentation.chat.groupchat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.presentation.chat.component.Chats
import com.bekircaglar.bluchat.presentation.chat.component.SearchTextField


@Composable
fun SelectGroupMemberDialog(
    textFieldValue: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<Users>,
    onDismiss: () -> Unit,
    onNext: (List<String>) -> Unit,
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    var selectedUsers by remember { mutableStateOf(mutableListOf<Users>()) }


    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {}) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.clickable {
                                onDismiss()
                            }
                        )
                    }
                    Text(
                        text = "Select Group Members",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                    TextButton(onClick = {}) {
                        Text(
                            text = "Next",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.clickable {
                                val selectedUid = selectedUsers.map { it.uid }
                                onNext(selectedUid)
                            }
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {


                    SearchTextField(
                        searchText = textFieldValue,
                        onSearchTextChange = { onSearchQueryChange(it) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f),
                        placeholderText = "Search user by phone number",
                        height = 50
                    )


                }


                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(searchResults, key = { it.email }) { contact ->
                        val myChat = com.bekircaglar.bluchat.domain.model.Chats(
                            chatRoomId = "",
                            imageUrl = contact.profileImageUrl,
                            name = contact.name,
                            surname = contact.surname,
                            lastMessage = "",
                            messageTime = "",
                            isOnline = contact.status
                        )
                        Chats(
                            chat = myChat,
                            onClick = {
                                selectedUsers = if (selectedUsers.contains(contact)) {
                                    selectedUsers.toMutableList().apply { remove(contact) }
                                } else {
                                    selectedUsers.toMutableList().apply { add(contact) }
                                }
                            },
                            isSelected = selectedUsers.contains(contact)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSelectGroupMemberDialog() {
    SelectGroupMemberDialog(
        textFieldValue = "",
        onSearchQueryChange = {},
        searchResults = listOf(),
        onDismiss = {},
        onNext = {}
    )
}
