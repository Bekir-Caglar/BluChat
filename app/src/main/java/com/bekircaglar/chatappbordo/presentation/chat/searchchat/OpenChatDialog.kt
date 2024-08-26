package com.bekircaglar.chatappbordo.presentation.chat.searchchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.navigation.Screens
import com.bekircaglar.chatappbordo.presentation.auth.signup.SignUpViewModel
import com.bekircaglar.chatappbordo.presentation.chat.ChatViewModel
import com.bekircaglar.chatappbordo.presentation.chat.Chats
import java.util.UUID

@Composable
fun OpenChatDialog(navController: NavController,textFieldValue :String,onSearchQueryChange:(String)-> Unit,searchResults:List<Users>,onDismiss: () -> Unit){



    // by ile = farklı

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
                    label = { Text("Telefon Numarası Ara") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(searchResults) { contact ->
                        Chats(profileImage = contact.profileImageUrl, name = contact.name, surname = contact.surname, lastMessage = null , messageTime = null, isOnline = contact.status,){
                           navController.navigate(Screens.MessageScreen.createRoute(contact.uid,UUID.randomUUID().toString()))
                        }
                    }
                }
            }
        }
    }


}