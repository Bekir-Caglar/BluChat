package com.bekircaglar.chatappbordo.presentation.profile.account

import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.bekircaglar.chatappbordo.R
import com.bekircaglar.chatappbordo.presentation.ShowToastMessage
import com.bekircaglar.chatappbordo.presentation.auth.component.AuthButton
import com.bekircaglar.chatappbordo.presentation.profile.ProfileViewModel
import com.bekircaglar.chatappbordo.ui.theme.ChatAppBordoTheme
import okhttp3.internal.wait


@Composable
fun AccountDialog(
    onDismissRequest: () -> Unit,
    onSave : () -> Unit = {},
    initialName: String = "",
    initialSurname: String = "",
    initialEmail: String = "",
    initialPhoneNumber: String = ""
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    var name by remember { mutableStateOf(initialName) }
    var surname by remember { mutableStateOf(initialSurname) }
    var email by remember { mutableStateOf(initialEmail) }
    var phoneNumber by remember { mutableStateOf(initialPhoneNumber) }

    val profileImage = viewModel.users.collectAsState().value?.profileImageUrl
    val currentUser = viewModel.users.collectAsState().value

    val context = LocalContext.current

    val selectedImageUri by viewModel.selectedImageUri.collectAsState()

    val uploadedImageUri by viewModel.uploadedImageUri.collectAsState()

    val isImageLoading by viewModel.isLoading.collectAsState()



    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onImageSelected(it)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Galeriye erişim izni gerekli!", Toast.LENGTH_SHORT).show()
        }
    }



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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = if(isImageLoading){
                    Modifier
                }
                    else{
                    Modifier.clickable{
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.READ_MEDIA_IMAGES
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                galleryLauncher.launch("image/*")

                            }

                            else -> {
                                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                            }
                        }
                    }
                }
                ) {



                    Image(
                        painter = rememberImagePainter(data = if (selectedImageUri != null) selectedImageUri else profileImage),
                        contentDescription = "Profile Image",
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
                    if (isImageLoading){
                        CircularProgressIndicator()
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Name : ${currentUser?.name}") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Surname : ${currentUser?.surname}") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number : ${currentUser?.phoneNumber}") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.padding(horizontal = 32.dp)) {
                    AuthButton(
                        enabled = isImageLoading.not(),
                        onClick = {
                            viewModel.checkPhoneNumber(
                                name = name,
                                surname = surname,
                                phoneNumber = phoneNumber,
                                profileImage = uploadedImageUri.toString(),
                                onSuccess = {
                                    onDismissRequest()
                                    onSave()
                                },
                                onError = {
                                    ShowToastMessage(context = context, message = it)
                                }
                            )
                        },
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
        )
    }
}
