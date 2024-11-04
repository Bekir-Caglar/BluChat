package com.bekircaglar.bluchat

import android.content.Intent
import android.content.res.Configuration
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
import com.bekircaglar.bluchat.data.repository.ChatRepositoryImp
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import com.bekircaglar.bluchat.navigation.ChatAppNavigation
import com.bekircaglar.bluchat.ui.theme.ChatAppBordoTheme
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var chatsRepository: ChatsRepository

    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        callbackManager = CallbackManager.Factory.create()
        val ONESIGNAL_APP_ID = BuildConfig.ONESIGNAL_APP_ID

        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)

        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
            val playerId = OneSignal.User.pushSubscription.id
            chatsRepository.saveSubId(playerId)
            OneSignal.login(auth.currentUser!!.uid)
        }

        installSplashScreen()
        enableEdgeToEdge()
        setContent {

            var isDarkTheme by remember {
                mutableStateOf(loadThemePreference(context = this))
            }

            ChatAppBordoTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                ChatAppNavigation(
                    navController = navController,
                    auth = auth,
                    onThemeChange = {
                        isDarkTheme = !isDarkTheme
                        saveThemePreference(context = this, isDarkTheme)
                    }
                )
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                saveThemePreference(context = this, false)
            }

            Configuration.UI_MODE_NIGHT_YES -> {
                saveThemePreference(context = this, true)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}