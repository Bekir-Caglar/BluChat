package com.bekircaglar.bluchat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.bekircaglar.bluchat.presentation.auth.signin.SignInScreen
import com.bekircaglar.bluchat.presentation.auth.signup.SignUpScreen

fun NavGraphBuilder.AuthNavGraph(navController: NavController) {
    navigation(startDestination = Screens.SingInScreen.route, route = Screens.AuthNav.route) {
        composable(Screens.SingInScreen.route) {
            SignInScreen(navController)
        }
        composable(Screens.SingUpScreen.route) {
            SignUpScreen(navController)
        }
    }
}