package com.wegielek.feature.fxRatesConverter.presentation.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.ConnectionErrorPopupViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ConnectionErrorPopupInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun connectionErrorPopup_showsAndHidesCorrectly() {
        // 1. Prepare flows to control the ViewModel state
        val isConnectedFlow = MutableStateFlow(true)
        val showErrorFlow = MutableStateFlow(false)

        // 2. Mock the ViewModel
        val viewModel = mockk<ConnectionErrorPopupViewModel>(relaxed = true)

        every { viewModel.isConnected } returns isConnectedFlow
        every { viewModel.showInternetConnectionError } returns showErrorFlow

        // 3. Compose UI with mocked ViewModel
        composeTestRule.setContent {
            ConnectionErrorPopup(modifier = Modifier, viewModel = viewModel)
        }

        // 4. Show the error
        composeTestRule.runOnUiThread {
            showErrorFlow.value = true
        }

        // 5. Assert that the popup is visible
        composeTestRule.onNodeWithText("No network").assertIsDisplayed()
        composeTestRule.onNodeWithText("Check your internet connection").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Internet Connection Error").assertIsDisplayed()

        // 6. Click the close button
        composeTestRule.onNodeWithContentDescription("Close").performClick()

        // 7. Verify that hideInternetConnectionError() was called
        verify { viewModel.hideInternetConnectionError() }

        // 8. Hide the error
        composeTestRule.runOnUiThread {
            showErrorFlow.value = false
        }

        // 9. Assert that the popup is gone
        composeTestRule.onNodeWithText("No network").assertDoesNotExist()
        composeTestRule.onNodeWithText("Check your internet connection").assertDoesNotExist()
    }
}
