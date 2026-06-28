package com.example.pharmax.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashScreen()
        }
    }
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    val activity = context as Activity

    LaunchedEffect(Unit) {
        delay(2000)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            context.startActivity(Intent(context, DashboardActivity::class.java))
        } else {
            context.startActivity(Intent(context, SignInActivity::class.java))
        }
        activity.finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF006B2C)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "PharmaX Logo",
                    tint = Color(0xFF006B2C),
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PharmaX",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "SECURING CONNECTION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF80DA8D),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your health, delivered.",
                fontSize = 16.sp,
                color = Color(0xFFB8E8C0),
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "HIPAA Compliant", fontSize = 12.sp, color = Color(0xFF80DA8D))
            Text(text = "  |  ", fontSize = 12.sp, color = Color(0xFF80DA8D))
            Text(text = "End-to-End Encrypted", fontSize = 12.sp, color = Color(0xFF80DA8D))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    SplashScreen()
}
