package com.bekircaglar.bluchat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.firebase.auth.FirebaseAuth

// Nested Navigation araştır
@Composable
fun ChatAppNavigation(navController: NavHostController,onThemeChange: () -> Unit,auth: FirebaseAuth) {

    NavHost(
        navController = navController,
        startDestination = if (auth.currentUser == null) Screens.AuthNav.route else Screens.HomeNav.route
        ){
        AuthNavGraph(navController)
        MainNavGraph(navController,onThemeChange)
    }

}
