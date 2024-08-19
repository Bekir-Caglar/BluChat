package com.bekircaglar.chatappbordo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bekircaglar.chatappbordo.R
import com.bekircaglar.chatappbordo.presentation.auth.signin.SignInScreen
import com.bekircaglar.chatappbordo.presentation.auth.signup.SignUpScreen
import com.bekircaglar.chatappbordo.presentation.chat.ChatScreen
import com.google.firebase.auth.FirebaseAuth

// Nested Navigation araştır
@Composable
fun ChatAppNavigation(navController: NavHostController,onThemeChange: (Boolean) -> Unit,auth: FirebaseAuth) {

    NavHost(
        navController = navController,
        startDestination = if (auth.currentUser == null) Screens.AuthNav.route else Screens.HomeNav.route
        ){
        AuthNavGraph(navController)
        MainNavGraph(navController,onThemeChange)
    }

}
