package com.bekircaglar.bluchat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.bekircaglar.bluchat.presentation.chat.ChatScreen
import com.bekircaglar.bluchat.presentation.message.MessageScreen
import com.bekircaglar.bluchat.presentation.profile.ProfileScreen


fun NavGraphBuilder.MainNavGraph(navController: NavController,onThemeChange: (Boolean) -> Unit) {
    navigation(startDestination = Screens.ChatScreen.route, route = Screens.HomeNav.route) {
        composable(Screens.ChatScreen.route) {
            ChatScreen(navController)
        }
        composable(Screens.ProfileScreen.route) {
            ProfileScreen(navController,{onThemeChange(it)})

        }
        composable(
            Screens.MessageScreen.route,
            arguments = listOf(
                navArgument("chatId") {type = NavType.StringType}
            )
        ) {
            val chatId = it.arguments?.getString("chatId")
            MessageScreen(navController,chatId!!)

        }
    }
}