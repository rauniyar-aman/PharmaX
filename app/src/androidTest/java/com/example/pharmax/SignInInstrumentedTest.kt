package com.example.pharmax

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pharmax.view.SignInActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<SignInActivity>()

    @Test
    fun signInScreen_acceptsInput() {
        composeRule.onNodeWithTag("email")
            .performTextInput("test@example.com")

        composeRule.onNodeWithTag("password")
            .performTextInput("password123")
    }

    @Test
    fun signInScreen_staysOnScreen_whenFieldsAreBlank() {
        composeRule.onNodeWithTag("signIn")
            .performClick()

        composeRule.onNodeWithTag("email")
            .assertIsDisplayed()
    }
}
