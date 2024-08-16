package com.bekircaglar.chatappbordo

import android.content.Context
import android.content.SharedPreferences

fun saveThemePreference(context: Context, isDarkTheme: Boolean) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("is_dark_theme", isDarkTheme)
    editor.apply()
}

fun loadThemePreference(context: Context): Boolean {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("is_dark_theme", false)
}