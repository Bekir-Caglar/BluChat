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

// Nested Navigation araştır
@Composable
fun ChatAppNavigation(navController: NavHostController,onThemeChange: (Boolean) -> Unit) {

    NavHost(
        navController = navController,
        startDestination = Screens.AuthNav.route
        ){
        AuthNavGraph(navController)
        MainNavGraph(navController,onThemeChange)
    }

}
