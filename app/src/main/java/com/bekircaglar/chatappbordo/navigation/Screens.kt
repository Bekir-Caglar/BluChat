package com.bekircaglar.chatappbordo.navigation

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import com.bekircaglar.chatappbordo.R

sealed class Screens(val route:String,val icon:Int? = null,val badgeCount:Int = 0,) {
    data object SingInScreen:Screens("sign_in_screen",)
    data object SingUpScreen:Screens("sign_up_screen",)
    data object ChatScreen:Screens("chat_screen", icon = R.drawable.ic_outlined_chat,3)
    data object ProfileScreen:Screens("profile_screen", icon = R.drawable.ic_outlined_profile)

    data object AuthNav : Screens("AUTH_NAV_GRAPH")

    data object HomeNav : Screens("HOME_NAV_GRAPH")
}