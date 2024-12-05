package com.bekircaglar.bluchat.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bekircaglar.bluchat.presentation.message.MessageScreen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalSharedTransitionApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ChatAppNavigation(
    navController: NavHostController,
    onThemeChange: () -> Unit,
    auth: FirebaseAuth
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = if (auth.currentUser == null) Screens.AuthNav.route else Screens.HomeNav.route
        ) {
            AuthNavGraph(navController)
            MainNavGraph(navController, onThemeChange, this@SharedTransitionLayout)
        }
    }
}