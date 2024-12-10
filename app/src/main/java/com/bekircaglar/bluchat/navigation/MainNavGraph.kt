package com.bekircaglar.bluchat.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.LookaheadScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.bekircaglar.bluchat.presentation.chat.ChatListScreen
import com.bekircaglar.bluchat.presentation.chatinfo.ChatInfoScreen
import com.bekircaglar.bluchat.presentation.contacts.ContactsScreen
import com.bekircaglar.bluchat.presentation.message.MessageScreen
import com.bekircaglar.bluchat.presentation.message.camera.CameraScreen
import com.bekircaglar.bluchat.presentation.message.camera.SendTakenPhotoScreen
import com.bekircaglar.bluchat.presentation.message.component.ImageScreen
import com.bekircaglar.bluchat.presentation.message.map.MapScreen
import com.bekircaglar.bluchat.presentation.message.starredmessages.StarredMessagesScreen
import com.bekircaglar.bluchat.presentation.profile.ProfileScreen


@OptIn(ExperimentalSharedTransitionApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun NavGraphBuilder.MainNavGraph(
    navController: NavController,
    onThemeChange: () -> Unit,
    sharedTransitionScope: SharedTransitionScope
) {
    navigation(startDestination = Screens.ChatListScreen.route, route = Screens.HomeNav.route) {
        composable(Screens.ChatListScreen.route) {
            ChatListScreen(navController)
        }
        composable(Screens.ProfileScreen.route) {
            ProfileScreen(navController) { onThemeChange() }

        }
        composable(Screens.MessageScreen.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) {
            val chatId = it.arguments?.getString("chatId")
            with(sharedTransitionScope) {
                MessageScreen(
                    navController,
                    chatId!!,
                    this@with,
                    animatedVisibilityScope = this@composable
                )
            }
        }
        composable(Screens.ChatInfoScreen.route,
            arguments = listOf(navArgument("infoChatId") { type = NavType.StringType })
        ) {
            val infoChatId = it.arguments?.getString("infoChatId")
            with(sharedTransitionScope) {
                ChatInfoScreen(navController, infoChatId, this, this@composable)
            }
        }
        composable(
            Screens.StarredMessagesScreen.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })

        ) {
            val ch = it.arguments?.getString("chatId")
            if (ch != null) {
                StarredMessagesScreen(chatId = ch, navController = navController)
            }
        }


        composable(Screens.ImageScreen.route,
            arguments = listOf(navArgument("imageUrl") { type = NavType.StringType })
        ) {
            val imageId = it.arguments?.getString("imageUrl")
            with(sharedTransitionScope) {
                imageId?.let { it1 -> ImageScreen(it1, animatedVisibilityScope = this@composable,sharedTransitionScope = this@with) }
            }
        }
        composable(Screens.CameraScreen.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) {
            val chatId = it.arguments?.getString("chatId")
            chatId?.let { it1 -> CameraScreen(navController, it1) }
        }


        composable(
            Screens.SendTakenPhotoScreen.route,
            arguments = listOf(navArgument("imageUrl") { type = NavType.StringType },
                navArgument("chatId") { type = NavType.StringType })
        ) {
            val imageUrl = it.arguments?.getString("imageUrl")
            val chatId = it.arguments?.getString("chatId")
            imageUrl?.let { it1 ->
                if (chatId != null) {
                    SendTakenPhotoScreen(it1, chatId, navController)
                }
            }

        }
        composable(
            Screens.ContactScreen.route,
        ) {
            ContactsScreen(navController)
        }
        composable(Screens.MapScreen.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) {
            val chatId = it.arguments?.getString("chatId")
            if (chatId != null) {
                MapScreen(navController, chatId)
            }
        }
    }
}