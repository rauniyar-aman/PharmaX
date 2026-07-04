package com.example.pharmax.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmax.R
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PharmaXTheme {
                SplashBody(
                    onNavigateUserDashboard = {
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    },
                    onNavigateAdminDashboard = {
                        startActivity(Intent(this, AdminDashboardActivity::class.java))
                        finish()
                    },
                    onNavigateSignIn = {
                        startActivity(Intent(this, SignInActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SplashBody(
    onNavigateUserDashboard: () -> Unit = {},
    onNavigateAdminDashboard: () -> Unit = {},
    onNavigateSignIn: () -> Unit = {}
) {
    val vm: UserViewModel = viewModel()
    val user by vm.user.collectAsState()

    LaunchedEffect(Unit) {
        delay(3000)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            vm.loadCurrentUser()
        } else {
            onNavigateSignIn()
        }
    }

    LaunchedEffect(user) {
        user?.let {
            if (it.role == "admin") onNavigateAdminDashboard() else onNavigateUserDashboard()
        }
    }

    SplashScreen()
}

@Composable
fun SplashScreen() {
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
                    .size(220.dp)
                    .background(Color.White, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "PharmaX Logo",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your health, delivered.",
                fontSize = 16.sp,
                color = Color(0xFFB8E8C0),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }


    }
}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    SplashScreen()
}
