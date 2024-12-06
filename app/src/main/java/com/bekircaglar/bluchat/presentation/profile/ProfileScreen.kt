package com.bekircaglar.bluchat.presentation.profile

import ChatAppBottomAppBar
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.bekircaglar.bluchat.domain.model.MenuItem
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.ShowToastMessage
import com.bekircaglar.bluchat.presentation.component.ChatAppTopBar
import com.bekircaglar.bluchat.presentation.profile.account.AccountDialog
import com.bekircaglar.bluchat.presentation.profile.appearance.AppearanceDialog
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.utils.UiState
import com.bekircaglar.bluchat.utils.loadThemePreference
import com.onesignal.OneSignal

@Composable
fun ProfileScreen(navController: NavController, onThemeChange: () -> Unit) {

    val context = LocalContext.current
    val viewModel: ProfileViewModel = hiltViewModel()

    var showAccountDialog by remember { mutableStateOf(false) }
    var showAppearanceDialog by remember { mutableStateOf(false) }
    var userName: String = ""
    var userNumber: String = ""
    var userImageUrl: String? = null

    val user by viewModel.users.collectAsStateWithLifecycle()

    val currentUser = viewModel.users.collectAsStateWithLifecycle().value
    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()
    val uploadedImageUri by viewModel.uploadedImageUri.collectAsStateWithLifecycle()
    val isImageLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

    if (uiState is UiState.Error) {
        val errorMessage = (uiState as UiState.Error).message
        if (errorMessage != null) {
            ShowToastMessage(context = context, message = errorMessage)
        }
    } else {
        user?.let {
            userName = "${it.name} ${it.surname}"
            userNumber = it.phoneNumber
            userImageUrl = it.profileImageUrl
        }
    }


    val menuItemList = listOf(
        MenuItem(painterResource(id = R.drawable.ic_account), "Account") {
            showAccountDialog = true
        },
        MenuItem(painterResource(id = R.drawable.ic_sun), "Appearance") {
            showAppearanceDialog = true
        },
        MenuItem(painterResource(id = R.drawable.ic_privacy), "Privacy", {}),
        MenuItem(painterResource(id = R.drawable.ic_help), "Help", {}),
        MenuItem(painterResource(id = R.drawable.ic_logout), "Logout") {
            OneSignal.logout()
            viewModel.signOut(
                onSuccess = {
                    navController.navigate(Screens.AuthNav.route) {
                        popUpTo(Screens.HomeNav.route) { inclusive = true }
                    }
                },
                onError = {
                    ShowToastMessage(context = context, message = it)
                },
                context = context
            )
        }
    )

    if (showAppearanceDialog) {
        AppearanceDialog(
            onDismissRequest = { showAppearanceDialog = false },
            onThemeChange = {
                onThemeChange()
            },
            darkTheme = loadThemePreference(context = context)
        )
    }

    if (showAccountDialog) {
        AccountDialog(
            onDismissRequest = {
                showAccountDialog = false
            },
            onSave = { showAccountDialog = false },
            profileImage = if (uploadedImageUri != null) uploadedImageUri else currentUser?.profileImageUrl,
            isImageLoading = isImageLoading,
            onImageSelected = { uri ->
                viewModel.onImageSelected(uri)
            },
            onPermissionRequest = {
                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            },
            onCheckPhoneNumber = viewModel::checkPhoneNumber,
            currentUsers = currentUser!!
        )

    }
    Scaffold(
        topBar = {
            ChatAppTopBar(
                title = {
                    Text(text = "Profile", color = MaterialTheme.colorScheme.primary)
                },
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                searchIcon = false
            )
        },
        bottomBar = {
            ChatAppBottomAppBar(navController)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (uiState == UiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                    )
                }
            }
            if (uiState is UiState.Error) {
                (uiState as UiState.Error).message?.let {
                    ShowToastMessage(context, it)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    val painter = rememberAsyncImagePainter(model = userImageUrl)
                    val painterState = painter.state
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(elevation = 5.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(color = Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (painterState is AsyncImagePainter.State.Success) {

                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .background(color = Color.White, shape = CircleShape)
                                    .size(80.dp)
                            )
                        }
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(120.dp)
                        )

                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "+90 $userNumber",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.3f.dp)
                    .shadow(elevation = 1.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                contentPadding = PaddingValues(top = 8.dp),
            ) {
                items(menuItemList) {
                    ProfileMenu(menuIcon = it.icon, menuTitle = it.title, onClick = {
                        it.onClick.invoke()
                    })
                }
            }
        }
    }
}