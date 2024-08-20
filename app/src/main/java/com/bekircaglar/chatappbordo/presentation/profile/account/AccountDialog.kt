package com.bekircaglar.chatappbordo.presentation.profile.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bekircaglar.chatappbordo.R
import com.bekircaglar.chatappbordo.presentation.auth.component.AuthButton
import com.bekircaglar.chatappbordo.ui.theme.ChatAppBordoTheme


@Composable
fun AccountDialog(
    onDismissRequest: () -> Unit,
    onSave: (String, String, String) -> Unit,
    initialName: String = "",
    initialSurname: String = "",
    initialEmail: String = "",
    initialPhoneNumber: String = ""
) {
    var name by remember { mutableStateOf(initialName) }
    var surname by remember { mutableStateOf(initialSurname) }
    var email by remember { mutableStateOf(initialEmail) }
    var phoneNumber by remember { mutableStateOf(initialPhoneNumber) }

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
                // Profile Image
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.ic_user2),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(elevation = 5.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
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
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Surname Field
                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Surname") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Surname Field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Column(modifier = Modifier.padding(horizontal = 32.dp)) {
                    AuthButton(
                        onClick = { onSave(name, surname, email) },
                        buttonText = "Save"
                    )
                }
            }
        }
    }
}
@Preview
@Composable
fun AccountDialogPreview() {
    ChatAppBordoTheme {
        AccountDialog(
            onDismissRequest = {},
            onSave = { _, _, _ -> }
        )
    }
}
