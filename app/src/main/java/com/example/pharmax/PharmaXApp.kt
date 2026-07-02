package com.example.pharmax

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.cloudinary.android.MediaManager
import com.example.pharmax.ui.theme.AppThemeState

class PharmaXApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("pharmax_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        AppThemeState.isDarkMode.value = isDark
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME
        )
        MediaManager.init(this, config)
    }
}
