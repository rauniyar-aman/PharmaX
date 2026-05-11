package com.example.pharmax

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirect to SplashActivity which is the entry point for PharmaX
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }
}
