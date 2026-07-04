package com.example.pharmax

import android.app.Application
import com.cloudinary.android.MediaManager

class PharmaXApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Dark mode is a per-user preference (UserModel.darkMode), applied once the
        // signed-in user's data loads (see UserViewModel.loadCurrentUser/login).
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME
        )
        MediaManager.init(this, config)
    }
}
