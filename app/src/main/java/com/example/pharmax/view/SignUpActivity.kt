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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmax.R
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.UserViewModel

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { PharmaXTheme { SignUpBody() } }
    }
}

@Composable
fun SignUpBody() {
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

    SignUpScreen(
        isLoading = isLoading,
        onSignUp = { fullName, email, phone, password, confirmPassword ->
            vm.registerUser(fullName, email, "+977$phone", password, confirmPassword) {
                context.startActivity(Intent(context, SignInActivity::class.java))
                (context as? Activity)?.finish()
            }
        },
        onShowError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
        onSignIn = { context.startActivity(Intent(context, SignInActivity::class.java)) }
    )
}

@Composable
fun SignUpScreen(
    isLoading: Boolean = false,
    onSignUp: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onShowError: (String) -> Unit = {},
    onSignIn: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val fullNameError = submitted && fullName.isBlank()
    val phoneError = submitted && phone.isBlank()
    val emailError = submitted && (email.isBlank() || !emailValid)
    val confirmEmailError = submitted && (confirmEmail.isBlank() || confirmEmail != email)
    val passwordError = submitted && password.isBlank()
    val confirmPasswordError = submitted && (confirmPassword.isBlank() || confirmPassword != password)

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "PharmaX Logo",
                modifier = Modifier.size(80.dp).align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {

                Text(text = "Create Account", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Please enter your details to register.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(20.dp))

                RequiredLabel("Full Name")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("John Doe") }, singleLine = true, shape = RoundedCornerShape(8.dp), isError = fullNameError,
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedIndicatorColor = Color(0xFF006B2C), unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant, errorIndicatorColor = MaterialTheme.colorScheme.error, errorContainerColor = MaterialTheme.colorScheme.surface))
                if (fullNameError) ErrorText("Full name is required")

                Spacer(modifier = Modifier.height(12.dp))

                RequiredLabel("Phone Number")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) phone = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("9XXXXXXXXX") },
                    prefix = { Text("+977 ", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    isError = phoneError,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedIndicatorColor = Color(0xFF006B2C), unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant, errorIndicatorColor = MaterialTheme.colorScheme.error, errorContainerColor = MaterialTheme.colorScheme.surface)
                )
                if (phoneError) ErrorText("Phone number is required")

                Spacer(modifier = Modifier.height(12.dp))

                RequiredLabel("Email Address")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("email@example.com") }, singleLine = true, shape = RoundedCornerShape(8.dp), isError = emailError,
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedIndicatorColor = Color(0xFF006B2C), unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant, errorIndicatorColor = MaterialTheme.colorScheme.error, errorContainerColor = MaterialTheme.colorScheme.surface))
                if (emailError) ErrorText(if (email.isBlank()) "Email address is required" else "Please enter a valid email address")

                Spacer(modifier = Modifier.height(12.dp))

                RequiredLabel("Confirm Email")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = confirmEmail, onValueChange = { confirmEmail = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Re-enter your email") }, singleLine = true, shape = RoundedCornerShape(8.dp), isError = confirmEmailError,
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedIndicatorColor = Color(0xFF006B2C), unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant, errorIndicatorColor = MaterialTheme.colorScheme.error, errorContainerColor = MaterialTheme.colorScheme.surface))
                if (confirmEmailError) ErrorText(if (confirmEmail.isBlank()) "Please confirm your email" else "Email addresses do not match")

                Spacer(modifier = Modifier.height(12.dp))

                RequiredLabel("Password")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("••••••••") }, singleLine = true, isError = passwordError,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(painter = painterResource(id = if (passwordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24), contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) } },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedIndicatorColor = Color(0xFF006B2C), unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant, errorIndicatorColor = MaterialTheme.colorScheme.error, errorContainerColor = MaterialTheme.colorScheme.surface)
                )
                if (passwordError) ErrorText("Password is required")

                Spacer(modifier = Modifier.height(12.dp))

                RequiredLabel("Confirm Password")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword, onValueChange = { confirmPassword = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("••••••••") }, singleLine = true, isError = confirmPasswordError,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(painter = painterResource(id = if (confirmPasswordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24), contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) } },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedIndicatorColor = Color(0xFF006B2C), unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant, errorIndicatorColor = MaterialTheme.colorScheme.error, errorContainerColor = MaterialTheme.colorScheme.surface)
                )
                if (confirmPasswordError) ErrorText(if (confirmPassword.isBlank()) "Confirm password is required" else "Passwords do not match")

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = agreedToTerms, onCheckedChange = { agreedToTerms = it }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFF006B2C)))
                    Text(
                        text = buildAnnotatedString {
                            append("I agree to the ")
                            withStyle(SpanStyle(color = Color(0xFF0051D5), fontWeight = FontWeight.Medium)) { append("Terms of Service") }
                            append(" and ")
                            withStyle(SpanStyle(color = Color(0xFF0051D5), fontWeight = FontWeight.Medium)) { append("Privacy Policy") }
                            append(".")
                        },
                        fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                ElevatedButton(
                    onClick = {
                        submitted = true
                        if (fullName.isBlank() || phone.isBlank() || email.isBlank() || !emailValid || confirmEmail.isBlank() || confirmEmail != email || password.isBlank() || confirmPassword.isBlank() || confirmPassword != password) return@ElevatedButton
                        if (!agreedToTerms) { onShowError("Please agree to the Terms of Service"); return@ElevatedButton }
                        onSignUp(fullName, email, phone, password, confirmPassword)
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF00501F), contentColor = Color.White),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text(text = "Create Account  →", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Already have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Sign in", color = Color(0xFF006B2C), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { onSignIn() })
                }

            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RequiredLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Text(text = " *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun ErrorText(message: String) {
    Text(text = message, fontSize = 12.sp, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp, start = 4.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpPreview() {
    SignUpScreen()
}
