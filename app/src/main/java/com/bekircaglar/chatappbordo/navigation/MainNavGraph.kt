package com.bekircaglar.chatappbordo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.bekircaglar.chatappbordo.presentation.chat.ChatScreen
import com.bekircaglar.chatappbordo.presentation.message.MessageScreen
import com.bekircaglar.chatappbordo.presentation.profile.ProfileScreen


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
                navArgument("userId") {type = NavType.StringType},
                navArgument("chatId") {type = NavType.StringType}
            )
        ) {
            val userId = it.arguments?.getString("userId")
            val chatId = it.arguments?.getString("chatId")
            MessageScreen(navController,userId!!,chatId!!)

        }
    }
}