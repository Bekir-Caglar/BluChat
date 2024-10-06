package com.bekircaglar.bluchat.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.bekircaglar.bluchat.presentation.chat.ChatListScreen
import com.bekircaglar.bluchat.presentation.chatinfo.ChatInfoScreen
import com.bekircaglar.bluchat.presentation.message.MessageScreen
import com.bekircaglar.bluchat.presentation.message.camera.CameraScreen
import com.bekircaglar.bluchat.presentation.message.camera.SendTakenPhotoScreen
import com.bekircaglar.bluchat.presentation.message.component.ImageScreen
import com.bekircaglar.bluchat.presentation.message.starredmessages.StarredMessagesScreen
import com.bekircaglar.bluchat.presentation.profile.ProfileScreen


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun NavGraphBuilder.MainNavGraph(navController: NavController, onThemeChange: (Boolean) -> Unit) {
    navigation(startDestination = Screens.ChatListScreen.route, route = Screens.HomeNav.route) {
        composable(Screens.ChatListScreen.route) {
            ChatListScreen(navController)
        }
        composable(Screens.ProfileScreen.route) {
            ProfileScreen(navController, { onThemeChange(it) })

        }
        composable(
            Screens.MessageScreen.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType }
            )
        ) {
            val chatId = it.arguments?.getString("chatId")
            MessageScreen(navController, chatId!!)

        }
        composable(
            Screens.ChatInfoScreen.route,
            arguments = listOf(
                navArgument("infoChatId") { type = NavType.StringType }
            )
        ) {
            val infoChatId = it.arguments?.getString("infoChatId")
            ChatInfoScreen(navController, infoChatId)
        }
        composable(
            Screens.StarredMessagesScreen.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType }
            )

        ){
            val ch = it.arguments?.getString("chatId")
            if (ch != null) {
                StarredMessagesScreen(chatId = ch,navController = navController)
            }
        }


        composable(
            Screens.ImageScreen.route,
            arguments = listOf(
                navArgument("imageUrl") { type = NavType.StringType }
            )
        ) {
            val imageId = it.arguments?.getString("imageUrl")
            imageId?.let { it1 -> ImageScreen(it1) }
        }
        composable(Screens.CameraScreen.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType }
            )) {
            val chatId = it.arguments?.getString("chatId")
            chatId?.let { it1 -> CameraScreen(navController, it1) }
        }


        composable(
            Screens.SendTakenPhotoScreen.route,
            arguments = listOf(
                navArgument("imageUrl") { type = NavType.StringType },
                navArgument("chatId") { type = NavType.StringType }
            )
        ) {
            val imageUrl = it.arguments?.getString("imageUrl")
            val chatId = it.arguments?.getString("chatId")
            imageUrl?.let { it1 ->
                if (chatId != null) {
                    SendTakenPhotoScreen(it1, chatId,navController)
                }
            }

        }
    }
}