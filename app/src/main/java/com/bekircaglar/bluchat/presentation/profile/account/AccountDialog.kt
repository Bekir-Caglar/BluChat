package com.bekircaglar.bluchat.presentation.profile.account

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.presentation.ShowToastMessage
import com.bekircaglar.bluchat.presentation.auth.component.AuthButton


@Composable
fun AccountDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    profileImage1: Any?,
    isImageLoading: Boolean,
    currentUsers: Users,
    onImageSelected: (Uri) -> Unit,
    onPermissionRequest: () -> Unit,
    onCheckPhoneNumber: (
        name: String,
        surname: String,
        phoneNumber: String,
        profileImage: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val context = LocalContext.current

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = if (isImageLoading) {
                        Modifier
                    } else {
                        Modifier.clickable {
                            onPermissionRequest()
                        }
                    }
                ) {
                    Image(
                        painter = rememberImagePainter(data = profileImage1),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(elevation = 5.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
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
                        CircularProgressIndicator()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Name: ${currentUsers.name}") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Surname: ${currentUsers.surname}") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number: ${currentUsers.phoneNumber}") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.padding(horizontal = 32.dp)) {
                    AuthButton(
                        enabled = !isImageLoading,
                        onClick = {
                            onCheckPhoneNumber(
                                name,
                                surname,
                                phoneNumber,
                                profileImage1.toString(),
                                {
                                    onDismissRequest()
                                    onSave()
                                },
                                {
                                    ShowToastMessage(context = context, message = it)
                                }
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background,
                        buttonText = "Save"
                    )
                }
            }
        }
    }
}