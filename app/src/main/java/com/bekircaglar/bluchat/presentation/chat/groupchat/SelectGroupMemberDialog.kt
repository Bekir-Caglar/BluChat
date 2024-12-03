package com.bekircaglar.bluchat.presentation.chat.groupchat


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.presentation.chat.component.Chats
import com.bekircaglar.bluchat.presentation.chat.component.SearchTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectGroupMemberBottomSheet(
    textFieldValue: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<Users>,
    onDismiss: () -> Unit,
    onNext: (List<String>) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var selectedUsers by remember { mutableStateOf(mutableListOf<Users>()) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = bottomSheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onDismiss() }) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = "Select Group Members",
                    style = MaterialTheme.typography.titleSmall
                )
                TextButton(onClick = {
                    val selectedUid = selectedUsers.map { it.uid }
                    onNext(selectedUid)
                    coroutineScope.launch { bottomSheetState.hide() }
                }) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SearchTextField(
                query = textFieldValue,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                text = "Search by phone number"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                        isSelected = selectedUsers.contains(contact),
                        onImageLoaded = {}
                    )
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewSelectGroupMemberDialog() {
    SelectGroupMemberBottomSheet(
        textFieldValue = "",
        onSearchQueryChange = {},
        searchResults = listOf(),
        onDismiss = {},
        onNext = {}
    )
}
