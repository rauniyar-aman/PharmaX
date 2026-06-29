package com.example.pharmax.view

import android.app.Activity
import android.content.Intent
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmax.R
import com.example.pharmax.viewmodel.UserViewModel

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { SignInBody() }
    }
}

@Composable
fun SignInBody() {
    val context = LocalContext.current
    val vm: UserViewModel = viewModel()
    val message by vm.message.collectAsState()
    val isLoading by vm.loading.collectAsState()
    val user by vm.user.collectAsState()
    val isEmailUnverified by vm.isEmailUnverified.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    LaunchedEffect(user) {
        user?.let {
            context.startActivity(Intent(context, DashboardActivity::class.java))
            (context as? Activity)?.finish()
        }
    }

    SignInScreen(
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        isLoading = isLoading,
        showResendVerification = isEmailUnverified,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onTogglePassword = { passwordVisible = !passwordVisible },
        onSignIn = { vm.login(email, password) },
        onForgotPassword = { context.startActivity(Intent(context, ForgotPasswordActivity::class.java)) },
        onCreateAccount = { context.startActivity(Intent(context, SignUpActivity::class.java)) },
        onResendVerification = { vm.resendVerificationEmail(email, password) }
    )
}

@Composable
fun SignInScreen(
    email: String = "",
    password: String = "",
    passwordVisible: Boolean = false,
    isLoading: Boolean = false,
    showResendVerification: Boolean = false,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onTogglePassword: () -> Unit = {},
    onSignIn: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
    onResendVerification: () -> Unit = {}
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FF))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "PharmaX Logo",
                modifier = Modifier.size(140.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0E1D2A),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Access your clinical records and pharmacy services securely.",
                fontSize = 14.sp,
                color = Color(0xFF3F493F),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {

                Text(text = "Email Address", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0E1D2A))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("name@email.com") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF006B2C),
                        unfocusedIndicatorColor = Color(0xFF6F7A6E)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Password", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0E1D2A))
                    Text(text = "Forgot Password?", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0051D5), modifier = Modifier.clickable { onForgotPassword() })
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("••••••••") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = onTogglePassword) {
                            Icon(
                                painter = painterResource(id = if (passwordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
                                contentDescription = null,
                                tint = Color(0xFF6F7A6E)
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF006B2C),
                        unfocusedIndicatorColor = Color(0xFF6F7A6E)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                ElevatedButton(
                    onClick = onSignIn,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF00501F), contentColor = Color.White),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(text = "Sign In  →", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Can't sign in? Check your spam folder for the verification email.",
                fontSize = 12.sp,
                color = Color(0xFF6F7A6E),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
            )

            if (showResendVerification) {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .background(Color(0xFFFFF3CD), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Your email is not verified yet.",
                            fontSize = 13.sp,
                            color = Color(0xFF856404),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Resend verification email",
                            fontSize = 13.sp,
                            color = Color(0xFF0051D5),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onResendVerification() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(text = "New here?", color = Color(0xFF3F493F), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Create account", color = Color(0xFF006B2C), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { onCreateAccount() })
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row {
                Text(text = "Privacy Policy", fontSize = 12.sp, color = Color(0xFF6F7A6E))
                Text(text = "   |   ", fontSize = 12.sp, color = Color(0xFF6F7A6E))
                Text(text = "Terms of Service", fontSize = 12.sp, color = Color(0xFF6F7A6E))
                Text(text = "   |   ", fontSize = 12.sp, color = Color(0xFF6F7A6E))
                Text(text = "HIPAA", fontSize = 12.sp, color = Color(0xFF6F7A6E))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "© 2024 PharmaX Healthcare Systems.", fontSize = 11.sp, color = Color(0xFF6F7A6E), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInPreview() {
    SignInScreen()
}
