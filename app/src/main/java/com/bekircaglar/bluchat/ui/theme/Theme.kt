package com.bekircaglar.bluchat.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OceanBlue,        // Ana vurgu rengi (ör. butonlar)
    onPrimary = IceBlue,        // Primary'nin üzerinde metin rengi (beyaz-açık)

    secondary = SkyBlue,        // İkincil vurgu rengi (ör. aktif durumlar, ikincil butonlar)
    onSecondary = MidnightBlue, // Secondary'nin üzerindeki metin (koyu)

    tertiary = BabyBlue,        // Üçüncül vurgu rengi (ek vurgu öğeleri için)
    onTertiary = NavyBlue,      // Tertiary'nin üzerindeki metin

    background = DarkBlue,      // Uygulamanın genel arka plan rengi
    onBackground = IceBlue,     // Arka plandaki metinlerin rengi

    surface = NavyBlue,         // Yüzey rengi (kartlar, dialoglar, vb.)
    onSurface = PowderBlue,     // Yüzeylerin üzerindeki metin rengi

    error = BrightBlue,         // Hata rengi (mavi varyasyonu kullanarak özelleştirildi)
    onError = MidnightBlue      // Hatanın üzerindeki metin
)

// Light Theme Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,          // Ana vurgu rengi (açık tonlar)
    onPrimary = MidnightBlue,   // Primary'nin üzerindeki metin rengi

    secondary = PowderBlue,     // İkincil vurgu rengi (daha yumuşak mavi)
    onSecondary = NavyBlue,     // Secondary'nin üzerindeki metin

    tertiary = BabyBlue,        // Üçüncül vurgu rengi (ek vurgu öğeleri için)
    onTertiary = DeepBlue,      // Tertiary'nin üzerindeki metin

    background = Color.White,       // Arka plan rengi (çok açık ton)
    onBackground = NavyBlue,    // Arka plan üzerindeki metin rengi

    surface = LightBlue,        // Yüzey rengi (kartlar, dialoglar, vb.)
    onSurface = DeepBlue,       // Yüzeylerin üzerindeki metin rengi

    error = BrightBlue,         // Hata rengi (mavi varyasyonu)
    onError = DarkBlue          // Hata üzerindeki metin
)

@Composable
fun ChatAppBordoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}