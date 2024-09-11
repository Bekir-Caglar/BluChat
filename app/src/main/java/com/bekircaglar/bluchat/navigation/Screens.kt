package com.bekircaglar.bluchat.navigation

import com.bekircaglar.bluchat.R


sealed class Screens(val route:String,val icon:Int? = null,val badgeCount:Int = 0,) {
    data object SingInScreen:Screens("sign_in_screen",)
    data object SingUpScreen:Screens("sign_up_screen",)
    data object ChatListScreen:Screens("chat_screen", icon = R.drawable.ic_outlined_chat,3)
    data object ProfileScreen:Screens("profile_screen", icon = R.drawable.ic_outlined_profile)
    data object MessageScreen:Screens("message_screen/{chatId}"){
        fun createRoute(chatId:String):String = "message_screen/$chatId"
    }

    data object AuthNav : Screens("AUTH_NAV_GRAPH")

    data object HomeNav : Screens("HOME_NAV_GRAPH")
}