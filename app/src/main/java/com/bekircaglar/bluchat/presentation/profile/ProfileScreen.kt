package com.bekircaglar.bluchat.presentation.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bekircaglar.bluchat.domain.model.MenuItem
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.ShowToastMessage
import com.bekircaglar.bluchat.presentation.bottomappbar.ChatAppBottomAppBar
import com.bekircaglar.bluchat.presentation.component.ChatAppTopBar
import com.bekircaglar.bluchat.presentation.profile.account.AccountDialog
import com.bekircaglar.bluchat.presentation.profile.appearance.AppearanceDialog
import com.bekircaglar.bluchat.saveThemePreference
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.loadThemePreference

@Composable
fun ProfileScreen(navController: NavController, onThemeChange: (Boolean) -> Unit) {

    val context = LocalContext.current
    val viewModel: ProfileViewModel = hiltViewModel()

    var showAccountDialog by remember { mutableStateOf(false) }
    var showAppearanceDialog by remember { mutableStateOf(false) }
    var userName: String = ""
    var userNumber: String = ""
    var userImageUrl: String? = null

    val user by viewModel.users.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val currentUser = viewModel.users.collectAsState().value
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

    if (error != null) {
        ShowToastMessage(context = context, message = error.toString())
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
        MenuItem(painterResource(id = R.drawable.ic_notification), "Notification", {}),
        MenuItem(painterResource(id = R.drawable.ic_help), "Help", {}),
        MenuItem(painterResource(id = R.drawable.ic_logout), "Logout") {
            viewModel.signOut(
                onSuccess = {
                    navController.navigate(Screens.AuthNav.route) {
                        popUpTo(Screens.HomeNav.route) { inclusive = true }
                    }
                },
                onError = {
                    ShowToastMessage(context = context, message = it)
                }
            )

        }
    )
    if (showAppearanceDialog) {
        AppearanceDialog(
            onDismissRequest = { showAppearanceDialog = false },
            onThemeChange = {
                onThemeChange(it)
                saveThemePreference(context = context, it)
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
            profileImage1 = if (uploadedImageUri != null) uploadedImageUri else currentUser?.profileImageUrl,
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
                    Text(text = "Profile", color = MaterialTheme.colorScheme.onSecondary)
                },
                containerColor = MaterialTheme.colorScheme.secondary,
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
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(color = MaterialTheme.colorScheme.secondary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = if (!(userImageUrl.isNullOrEmpty())) rememberImagePainter(userImageUrl) else painterResource(
                        id = R.drawable.ic_outlined_profile
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(elevation = 5.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(color = Color.White)
                )
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
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
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