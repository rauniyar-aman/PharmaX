package com.example.pharmax

import android.app.Application
import com.cloudinary.android.MediaManager

class PharmaXApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = mapOf(
            "cloud_name" to "dyetskjmd",
            "api_key"    to "852955897677829",
            "api_secret" to "8oLKtKVfqpyLVX_XFBYVMe3hqPs"
        )
        MediaManager.init(this, config)
    }
}
