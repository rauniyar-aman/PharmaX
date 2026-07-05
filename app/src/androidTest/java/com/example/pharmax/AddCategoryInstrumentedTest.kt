package com.example.pharmax

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pharmax.view.AddCategoryActivity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddCategoryInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<AddCategoryActivity>()

    @Test
    fun addCategory_staysOnScreen_whenNameIsBlank() {
        composeRule.onNodeWithTag("saveButton")
            .performClick()

        composeRule.onNodeWithTag("categoryName")
            .assertIsDisplayed()
        assertFalse(composeRule.activity.isFinishing)
    }

    @Test
    fun discardButton_closesScreen() {
        composeRule.onNodeWithTag("categoryName")
            .performTextInput("Temporary Category")

        composeRule.onNodeWithTag("discardButton")
            .performClick()

        assertTrue(composeRule.activity.isFinishing)
    }
}
