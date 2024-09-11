package com.bekircaglar.bluchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.bekircaglar.bluchat.navigation.ChatAppNavigation
import com.bekircaglar.bluchat.ui.theme.ChatAppBordoTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth
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
                ChatAppNavigation(
                    navController = navController,
                    auth = auth,
                    onThemeChange = {
                        isDarkTheme = it
                        saveThemePreference(this, it)
                    })
            }
        }
    }
}

