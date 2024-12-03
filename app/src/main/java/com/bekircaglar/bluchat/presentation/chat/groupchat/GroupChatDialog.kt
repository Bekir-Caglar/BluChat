package com.bekircaglar.bluchat.presentation.chat.groupchat

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.bekircaglar.bluchat.presentation.ShowToastMessage
import com.bekircaglar.bluchat.presentation.auth.component.AuthButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatBottomSheet(
    onDismissRequest: () -> Unit,
    onPermissionRequest: () -> Unit,
    onCreateGroupChat: (groupName: String) -> Unit,
    selectedUri: Uri? = null,
    defaultImageUrl: String? = "https://firebasestorage.googleapis.com/v0/b/chatappbordo.appspot.com/o/def_user.png?alt=media&token=54d55dc5-4fad-415a-8b6f-d0f3b0619f31",
    defaultGroupName: String = "Group name",
    isImageLoading: Boolean = false,
    buttonText: String = "Create group chat",
    buttonColor: Color = MaterialTheme.colorScheme.primary
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var groupChatName by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = if (isImageLoading) Modifier else Modifier.clickable { onPermissionRequest() }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = selectedUri ?: defaultImageUrl),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .align(Alignment.BottomEnd)
                        .border(2.dp, Color.White, CircleShape)
                        .padding(4.dp)
                )

                if (isImageLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(100.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grup İsmi Giriş Alanı
            OutlinedTextField(
                value = groupChatName,
                onValueChange = { groupChatName = it },
                label = { Text(text = defaultGroupName) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Oluştur Butonu
            AuthButton(
                enabled = !isImageLoading,
                onClick = {
                    if (groupChatName.isEmpty()) {
                        if (defaultGroupName != "Group name") {
                            groupChatName = defaultGroupName
                            onCreateGroupChat(groupChatName)
                            coroutineScope.launch { bottomSheetState.hide() }
                        } else {
                            ShowToastMessage(context, "Please enter a group name")
                        }
                    } else {
                        onCreateGroupChat(groupChatName)
                        coroutineScope.launch { bottomSheetState.hide() }
                    }
                },
                containerColor = buttonColor,
                contentColor = Color.White,
                buttonText = buttonText
            )
        }
    }
}
