package com.example.pharmax.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmax.R
import com.example.pharmax.viewmodel.UserViewModel

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ForgotPasswordBody(onBack = { finish() }) }
    }
}

@Composable
fun ForgotPasswordBody(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val vm: UserViewModel = viewModel()
    val message by vm.message.collectAsState()
    val isLoading by vm.loading.collectAsState()

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    ForgotPasswordScreen(
        isLoading = isLoading,
        onSend = { email -> vm.sendPasswordResetEmail(email) },
        onBack = onBack
    )
}

@Composable
fun ForgotPasswordScreen(
    isLoading: Boolean = false,
    onSend: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FF))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "PharmaX Logo",
                modifier = Modifier.size(140.dp).align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {

                Text(text = "Forgot Password?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0E1D2A))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter the email address associated with your account and we'll send you a link to reset your password.",
                    fontSize = 14.sp, color = Color(0xFF3F493F), lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Email Address", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0E1D2A))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email, onValueChange = { email = it }, modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. name@company.com") }, singleLine = true, shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color(0xFF006B2C), unfocusedIndicatorColor = Color(0xFF6F7A6E))
                )

                Spacer(modifier = Modifier.height(24.dp))

                ElevatedButton(
                    onClick = { onSend(email) },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF006B2C), contentColor = Color.White),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text(text = "Send Reset Link  →", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3F493F), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Back to Sign In", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3F493F), modifier = Modifier.clickable { onBack() })
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "END-TO-END SECURE", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6F7A6E), letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Your medical data and credentials are encrypted according to HIPAA healthcare standards.", fontSize = 12.sp, color = Color(0xFF6F7A6E), textAlign = TextAlign.Center, lineHeight = 18.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordPreview() {
    ForgotPasswordScreen()
}
