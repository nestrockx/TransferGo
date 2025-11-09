package com.wegielek.feature.fxRatesConverter.presentation.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.wegielek.feature.fxRatesConverter.domain.model.ExchangeRate
import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.ConnectionErrorPopupViewModel
import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.CurrencyExchangeViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class CurrencyExchangeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun currencyExchangeScreen_canOpenModalAndSelectCurrency() {
        // 1. Mock ViewModel
        val fromCurrencyFlow = MutableStateFlow("PLN")
        val toCurrencyFlow = MutableStateFlow("UAH")
        val fromAmountFlow = MutableStateFlow(BigDecimal(100.0))
        val toAmountFlow = MutableStateFlow(BigDecimal(0.0))
        val chooseFromSheetFlow = MutableStateFlow(false)
        val chooseToSheetFlow = MutableStateFlow(false)
        val fromAmountExceededFlow = MutableStateFlow(false)
        val searchFieldFlow = MutableStateFlow("")
        val exchangeFlow =
            MutableStateFlow(
                ExchangeRate(
                    "PLN",
                    "UAH",
                    BigDecimal(1.2),
                    BigDecimal(120.0),
                    BigDecimal(120.0),
                ),
            )

        val isConnectedFlow = MutableStateFlow(true)
        val showErrorFlow = MutableStateFlow(false)

        val connectionErrorPopupViewModel = mockk<ConnectionErrorPopupViewModel>(relaxed = true)

        every { connectionErrorPopupViewModel.isConnected } returns isConnectedFlow
        every { connectionErrorPopupViewModel.showInternetConnectionError } returns showErrorFlow

        val viewModel = mockk<CurrencyExchangeViewModel>(relaxed = true)

        every { viewModel.fromCurrency } returns fromCurrencyFlow
        every { viewModel.toCurrency } returns toCurrencyFlow
        every { viewModel.fromAmount } returns fromAmountFlow
        every { viewModel.toAmount } returns toAmountFlow
        every { viewModel.chooseFromCurrencyModalSheet } returns chooseFromSheetFlow
        every { viewModel.chooseToCurrencyModalSheet } returns chooseToSheetFlow
        every { viewModel.fromAmountExceeded } returns fromAmountExceededFlow
        every { viewModel.searchField } returns searchFieldFlow
        every { viewModel.exchangeResult } returns exchangeFlow

        // 2. Compose UI
        composeTestRule.setContent {
            CurrencyExchangeScreen(
                viewModel = viewModel,
                connectionErrorPopupViewModel = connectionErrorPopupViewModel,
                onNavigateBack = {},
            )
        }

        // 3. Check sending from and receiver gets text
        composeTestRule.onNodeWithTag("sendingFromCurrency").assertIsDisplayed()
        composeTestRule.onNodeWithText("Receiver gets").assertIsDisplayed()

        // 4. Click the "Sending from" currency to show modal
        composeTestRule.onNodeWithText("PLN").assertHasClickAction().performClick()

        // 5. Tell the ViewModel to show modal
        composeTestRule.runOnIdle {
            chooseFromSheetFlow.value = true
        }

        // 6. Modal sheet should appear
        composeTestRule.onNodeWithTag("sendingHeader").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hrivna • UAH").assertIsDisplayed()
        composeTestRule.onNodeWithText("British Pound • GBP").assertIsDisplayed()
        composeTestRule.onNodeWithText("Euro • EUR").assertIsDisplayed()

        // 7. Click GBP
        composeTestRule.onNodeWithText("British Pound • GBP").performClick()

        // 8. Wait for Compose + coroutines
        composeTestRule.runOnIdle {
            // Verify that ViewModel methods were called
            verify { viewModel.onFromCurrencySelected("GBP") }
            verify { viewModel.hideChooseFromCurrencySheet() }
        }
    }

    @Test
    fun currencyExchangeScreen_canSwapCurrencies() {
        val fromCurrencyFlow = MutableStateFlow("PLN")
        val toCurrencyFlow = MutableStateFlow("UAH")
        val fromAmountFlow = MutableStateFlow(BigDecimal(100.0))
        val toAmountFlow = MutableStateFlow(BigDecimal(0.0))
        val chooseFromSheetFlow = MutableStateFlow(false)
        val chooseToSheetFlow = MutableStateFlow(false)
        val fromAmountExceededFlow = MutableStateFlow(false)
        val searchFieldFlow = MutableStateFlow("")
        val exchangeFlow =
            MutableStateFlow(
                ExchangeRate(
                    "PLN",
                    "UAH",
                    BigDecimal(1.2),
                    BigDecimal(120.0),
                    BigDecimal(120.0),
                ),
            )

        val isConnectedFlow = MutableStateFlow(true)
        val showErrorFlow = MutableStateFlow(false)

        val connectionErrorPopupViewModel = mockk<ConnectionErrorPopupViewModel>(relaxed = true)

        every { connectionErrorPopupViewModel.isConnected } returns isConnectedFlow
        every { connectionErrorPopupViewModel.showInternetConnectionError } returns showErrorFlow

        val viewModel = mockk<CurrencyExchangeViewModel>(relaxed = true)

        every { viewModel.fromCurrency } returns fromCurrencyFlow
        every { viewModel.toCurrency } returns toCurrencyFlow
        every { viewModel.fromAmount } returns fromAmountFlow
        every { viewModel.toAmount } returns toAmountFlow
        every { viewModel.chooseFromCurrencyModalSheet } returns chooseFromSheetFlow
        every { viewModel.chooseToCurrencyModalSheet } returns chooseToSheetFlow
        every { viewModel.fromAmountExceeded } returns fromAmountExceededFlow
        every { viewModel.searchField } returns searchFieldFlow
        every { viewModel.exchangeResult } returns exchangeFlow

        composeTestRule.setContent {
            CurrencyExchangeScreen(
                viewModel = viewModel,
                connectionErrorPopupViewModel = connectionErrorPopupViewModel,
                onNavigateBack = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Swap")
            .assertHasClickAction()
            .performClick()

        composeTestRule.runOnIdle {
            verify { viewModel.swapCurrency() }
        }
    }

    @Test
    fun currencyExchangeScreen_canOpenReceivingCurrencyModalAndSelectCurrency() {
        // Arrange flows (same as in your tests)
        val fromCurrencyFlow = MutableStateFlow("PLN")
        val toCurrencyFlow = MutableStateFlow("UAH")
        val fromAmountFlow = MutableStateFlow(BigDecimal(100.0))
        val toAmountFlow = MutableStateFlow(BigDecimal(0.0))
        val chooseFromSheetFlow = MutableStateFlow(false)
        val chooseToSheetFlow = MutableStateFlow(false)
        val fromAmountExceededFlow = MutableStateFlow(false)
        val searchFieldFlow = MutableStateFlow("")
        val exchangeFlow = MutableStateFlow(ExchangeRate("PLN", "UAH", BigDecimal(1.2), BigDecimal(120.0), BigDecimal(120.0)))

        val connectionErrorPopupViewModel = mockk<ConnectionErrorPopupViewModel>(relaxed = true)
        every { connectionErrorPopupViewModel.isConnected } returns MutableStateFlow(true)
        every { connectionErrorPopupViewModel.showInternetConnectionError } returns MutableStateFlow(false)

        val viewModel = mockk<CurrencyExchangeViewModel>(relaxed = true)
        every { viewModel.fromCurrency } returns fromCurrencyFlow
        every { viewModel.toCurrency } returns toCurrencyFlow
        every { viewModel.fromAmount } returns fromAmountFlow
        every { viewModel.toAmount } returns toAmountFlow
        every { viewModel.chooseFromCurrencyModalSheet } returns chooseFromSheetFlow
        every { viewModel.chooseToCurrencyModalSheet } returns chooseToSheetFlow
        every { viewModel.fromAmountExceeded } returns fromAmountExceededFlow
        every { viewModel.searchField } returns searchFieldFlow
        every { viewModel.exchangeResult } returns exchangeFlow

        composeTestRule.setContent {
            CurrencyExchangeScreen(
                viewModel = viewModel,
                connectionErrorPopupViewModel = connectionErrorPopupViewModel,
                onNavigateBack = {},
            )
        }

        // Act → Click receiving currency "UAH"
        composeTestRule.onNodeWithText("UAH").performClick()

        // Trigger modal visibility
        composeTestRule.runOnIdle { chooseToSheetFlow.value = true }

        // Assert modal appears
        composeTestRule.onNodeWithText("Sending to").assertIsDisplayed()

        // Click "EUR"
        composeTestRule.onNodeWithText("Euro • EUR").performClick()

        composeTestRule.runOnIdle {
            verify { viewModel.onToCurrencySelected("EUR") }
            verify { viewModel.hideChooseToCurrencySheet() }
        }
    }

    @Test
    fun currencyExchangeScreen_editSendingAmount_updatesViewModel() {
        // Arrange minimal flows
        val fromCurrencyFlow = MutableStateFlow("PLN")
        val toCurrencyFlow = MutableStateFlow("UAH")
        val fromAmountFlow = MutableStateFlow(BigDecimal(100.0))
        val toAmountFlow = MutableStateFlow(BigDecimal(0.0))

        val viewModel = mockk<CurrencyExchangeViewModel>(relaxed = true)
        every { viewModel.fromCurrency } returns fromCurrencyFlow
        every { viewModel.toCurrency } returns toCurrencyFlow
        every { viewModel.fromAmount } returns fromAmountFlow
        every { viewModel.toAmount } returns toAmountFlow
        every { viewModel.chooseFromCurrencyModalSheet } returns MutableStateFlow(false)
        every { viewModel.chooseToCurrencyModalSheet } returns MutableStateFlow(false)
        every { viewModel.fromAmountExceeded } returns MutableStateFlow(false)
        every { viewModel.searchField } returns MutableStateFlow("")
        every { viewModel.exchangeResult } returns MutableStateFlow(null)

        val connectionErrorPopupViewModel = mockk<ConnectionErrorPopupViewModel>(relaxed = true)
        every { connectionErrorPopupViewModel.isConnected } returns MutableStateFlow(true)
        every { connectionErrorPopupViewModel.showInternetConnectionError } returns MutableStateFlow(false)

        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel, connectionErrorPopupViewModel, {})
        }

        // Act
        composeTestRule
            .onNodeWithText("100.00") // formatted
            .performTextInput("1")

        // Assert
        composeTestRule.runOnIdle {
            verify { viewModel.updateFromAmount("1100.00") }
        }
    }

    @Test
    fun currencyExchangeScreen_showsWarningWhenFromAmountExceeded() {
        val fromAmountExceededFlow = MutableStateFlow(true)

        val viewModel = mockk<CurrencyExchangeViewModel>(relaxed = true)
        every { viewModel.fromAmountExceeded } returns fromAmountExceededFlow
        every { viewModel.fromCurrency } returns MutableStateFlow("PLN")
        every { viewModel.currencyLimits } returns mapOf("PLN" to BigDecimal(20000))
        every { viewModel.toCurrency } returns MutableStateFlow("UAH")
        every { viewModel.fromAmount } returns MutableStateFlow(BigDecimal(0))
        every { viewModel.toAmount } returns MutableStateFlow(BigDecimal(0))
        every { viewModel.chooseFromCurrencyModalSheet } returns MutableStateFlow(false)
        every { viewModel.chooseToCurrencyModalSheet } returns MutableStateFlow(false)
        every { viewModel.searchField } returns MutableStateFlow("")
        every { viewModel.exchangeResult } returns MutableStateFlow(null)

        val connectionErrorPopupViewModel = mockk<ConnectionErrorPopupViewModel>(relaxed = true)
        every { connectionErrorPopupViewModel.isConnected } returns MutableStateFlow(true)
        every { connectionErrorPopupViewModel.showInternetConnectionError } returns MutableStateFlow(false)

        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel, connectionErrorPopupViewModel, {})
        }

        composeTestRule.onNodeWithText("Maximum sending amount: 20000 PLN").assertIsDisplayed()
    }

    @Test
    fun currencyExchangeScreen_showsExchangeRate() {
        val exchangeFlow =
            MutableStateFlow(
                ExchangeRate("PLN", "UAH", BigDecimal("1.23"), BigDecimal.ZERO, BigDecimal.ZERO),
            )

        val viewModel = mockk<CurrencyExchangeViewModel>(relaxed = true)
        every { viewModel.exchangeResult } returns exchangeFlow
        every { viewModel.fromCurrency } returns MutableStateFlow("PLN")
        every { viewModel.toCurrency } returns MutableStateFlow("UAH")
        every { viewModel.fromAmount } returns MutableStateFlow(BigDecimal(10))
        every { viewModel.toAmount } returns MutableStateFlow(BigDecimal(12.3))
        every { viewModel.chooseFromCurrencyModalSheet } returns MutableStateFlow(false)
        every { viewModel.chooseToCurrencyModalSheet } returns MutableStateFlow(false)
        every { viewModel.fromAmountExceeded } returns MutableStateFlow(false)
        every { viewModel.searchField } returns MutableStateFlow("")

        val connectionErrorPopupViewModel = mockk<ConnectionErrorPopupViewModel>(relaxed = true)
        every { connectionErrorPopupViewModel.isConnected } returns MutableStateFlow(true)
        every { connectionErrorPopupViewModel.showInternetConnectionError } returns MutableStateFlow(false)

        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel, connectionErrorPopupViewModel, {})
        }

        composeTestRule.onNodeWithText("1 PLN = 1.23 UAH").assertIsDisplayed()
    }
}
