package com.bekircaglar.chatappbordo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.bekircaglar.chatappbordo.presentation.chat.ChatScreen
import com.bekircaglar.chatappbordo.presentation.profile.ProfileScreen


fun NavGraphBuilder.MainNavGraph(navController: NavController,onThemeChange: (Boolean) -> Unit) {
    navigation(startDestination = Screens.ChatScreen.route, route = Screens.HomeNav.route) {
        composable(Screens.ChatScreen.route) {
            ChatScreen(navController)
        }
        composable(Screens.ProfileScreen.route) {
            ProfileScreen(navController,{onThemeChange(it)})

        }
    }
}