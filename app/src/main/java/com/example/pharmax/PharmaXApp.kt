package com.example.pharmax

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.cloudinary.android.MediaManager

class PharmaXApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("pharmax_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key"    to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
        )
        MediaManager.init(this, config)
    }
}
