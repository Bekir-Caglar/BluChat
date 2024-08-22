package com.bekircaglar.chatappbordo.presentation.profile

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bekircaglar.chatappbordo.R
import com.bekircaglar.chatappbordo.domain.model.MenuItem
import com.bekircaglar.chatappbordo.navigation.Screens
import com.bekircaglar.chatappbordo.presentation.ShowToastMessage
import com.bekircaglar.chatappbordo.presentation.bottomappbar.ChatAppBottomAppBar
import com.bekircaglar.chatappbordo.presentation.component.ChatAppTopBar
import com.bekircaglar.chatappbordo.presentation.profile.account.AccountDialog
import com.bekircaglar.chatappbordo.presentation.profile.appearance.AppearanceDialog
import com.bekircaglar.chatappbordo.saveThemePreference

@Composable
fun ProfileScreen(navController: NavController, onThemeChange: (Boolean) -> Unit) {

    val context = LocalContext.current
    val viewModel: ProfileViewModel = hiltViewModel()
    var showAccountDialog by remember { mutableStateOf(false) }
    var showAppearanceDialog by remember { mutableStateOf(false) }
    val isDarkTheme by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(isDarkTheme) }
    var userName: String = ""
    var userNumber: String = ""
    var userImageUrl: String? = null

    val user by viewModel.users.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

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
                    navController.navigate(Screens.AuthNav.route)
                },
                onError = {
                    ShowToastMessage(context = context, message = it)
                }
            )

        }
    )
    if (showAppearanceDialog) {
        AppearanceDialog(
            isDarkTheme = isDarkTheme,
            onDismissRequest = { showAppearanceDialog = false },
            onThemeChange = {
                onThemeChange(it)
                saveThemePreference(context = context, it)

            },
            isChecked2 = isChecked
        )
    }
    if (showAccountDialog) {
        AccountDialog(
            onDismissRequest = {
                showAccountDialog = false
            },

            )
    }
    Scaffold(
        topBar = {
            ChatAppTopBar(
                title = {
                    Text(text = "Profile", color = MaterialTheme.colorScheme.onSecondaryContainer)
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                    .background(color = MaterialTheme.colorScheme.secondaryContainer),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = if (!(userImageUrl.isNullOrEmpty())) rememberImagePainter(userImageUrl) else painterResource(
                        id = R.drawable.ic_outlined_profile
                    ),
                    contentDescription = null,
                    Modifier
                        .size(100.dp)
                        .shadow(elevation = 5.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(color = Color.White)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = userName, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "+90 $userNumber",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
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
                        it.onClick?.invoke()
                    })


                }


            }
        }


    }
}


//    @Preview
//    @Composable
//    fun ProfileScreenPreview() {
//        ChatAppBordoTheme {
//            ProfileScreen()
//        }
//    }