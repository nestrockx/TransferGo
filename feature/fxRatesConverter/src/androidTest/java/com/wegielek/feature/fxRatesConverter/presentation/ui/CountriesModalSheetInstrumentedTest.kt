package com.wegielek.feature.fxRatesConverter.presentation.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.CurrencyExchangeViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class CountriesModalSheetInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun countriesModalSheet_displaysAndHandlesSelection() {
        // 1. Create mocked ViewModel
        val searchFlow = MutableStateFlow("")
        val chooseFromSheetFlow = MutableStateFlow(true)
        val fromCurrencyFlow = MutableStateFlow("PLN")
        val toCurrencyFlow = MutableStateFlow("UAH")

        val viewModel = mockk<CurrencyExchangeViewModel>(relaxed = true)

        every { viewModel.searchField } returns searchFlow
        every { viewModel.chooseFromCurrencyModalSheet } returns chooseFromSheetFlow
        every { viewModel.fromCurrency } returns fromCurrencyFlow
        every { viewModel.toCurrency } returns toCurrencyFlow

        // 2. Compose UI with mocked ViewModel
        composeTestRule.setContent {
            CountriesModalSheet(viewModel)
        }

        // 3. Assert that title is correct for "sending from"
        composeTestRule.onNodeWithText("Sending from").assertIsDisplayed()

        // 4. Assert that all currencies except the current fromCurrency are displayed
        composeTestRule.onNodeWithText("Hrivna • UAH").assertIsDisplayed()
        composeTestRule.onNodeWithText("British Pound • GBP").assertIsDisplayed()
        composeTestRule.onNodeWithText("Euro • EUR").assertIsDisplayed()
        // PLN should be skipped (current fromCurrency)
        composeTestRule.onNodeWithText("Złoty • PLN").assertDoesNotExist()

        // 5. Click on a currency (GBP)
        composeTestRule.onNodeWithText("British Pound • GBP").assertHasClickAction().performClick()

        // 6. Verify that onFromCurrencySelected was called with GBP
        verify { viewModel.onFromCurrencySelected("GBP") }

        // 7. Verify that hideChooseFromCurrencySheet was called
        // Wait for coroutines to finish
        composeTestRule.runOnIdle {
            // Verify that hideChooseFromCurrencySheet was called
            verify { viewModel.hideChooseFromCurrencySheet() }
        }
    }

    @Test
    fun countriesModalSheet_handlesSearchFilter() {
        val searchFlow = MutableStateFlow("UAH")
        val chooseFromSheetFlow = MutableStateFlow(true)
        val fromCurrencyFlow = MutableStateFlow("PLN")
        val toCurrencyFlow = MutableStateFlow("UAH")

        val viewModel = mockk<CurrencyExchangeViewModel>(relaxed = true)

        every { viewModel.searchField } returns searchFlow
        every { viewModel.chooseFromCurrencyModalSheet } returns chooseFromSheetFlow
        every { viewModel.fromCurrency } returns fromCurrencyFlow
        every { viewModel.toCurrency } returns toCurrencyFlow

        composeTestRule.setContent {
            CountriesModalSheet(viewModel)
        }

        // Only UAH should be visible due to search filter
        composeTestRule.onNodeWithText("UAH").assertIsDisplayed()
        composeTestRule.onNodeWithText("GBP").assertDoesNotExist()
        composeTestRule.onNodeWithText("EUR").assertDoesNotExist()
    }
}
