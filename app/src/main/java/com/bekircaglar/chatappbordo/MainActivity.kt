package com.bekircaglar.chatappbordo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.bekircaglar.chatappbordo.navigation.ChatAppNavigation
import com.bekircaglar.chatappbordo.presentation.auth.signin.SignInScreen
import com.bekircaglar.chatappbordo.presentation.auth.signup.SignUpScreen
import com.bekircaglar.chatappbordo.presentation.chat.ChatScreen
import com.bekircaglar.chatappbordo.presentation.chat.message.MessageScreen
import com.bekircaglar.chatappbordo.presentation.profile.ProfileScreen
import com.bekircaglar.chatappbordo.ui.theme.ChatAppBordoTheme
import com.bekircaglar.loadThemePreference
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {

            var isDarkTheme by remember {
                mutableStateOf(loadThemePreference(this))
            }

            ChatAppBordoTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                ChatAppNavigation(navController = navController){
                    isDarkTheme = it
                }

            }
        }
    }
}

