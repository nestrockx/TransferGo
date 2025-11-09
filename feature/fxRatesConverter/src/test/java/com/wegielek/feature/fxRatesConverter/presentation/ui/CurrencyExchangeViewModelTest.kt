import android.util.Log
import app.cash.turbine.test
import com.wegielek.feature.fxRatesConverter.domain.model.ExchangeRate
import com.wegielek.feature.fxRatesConverter.domain.usecase.GetExchangeRateUseCase
import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.CurrencyExchangeViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyExchangeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockUseCase: GetExchangeRateUseCase
    private lateinit var viewModel: CurrencyExchangeViewModel

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0

        mockUseCase = mockk()
        coEvery {
            mockUseCase(any(), any(), any())
        } returns
            ExchangeRate(
                from = "PLN",
                to = "UAH",
                fromAmount = BigDecimal(1),
                toAmount = BigDecimal(2),
                rate = BigDecimal(1.0),
            )

        viewModel =
            CurrencyExchangeViewModel(
                mockUseCase,
                ioDispatcher = testDispatcher,
            )
    }

    @Test
    fun `getExchangeRate emits success`() =
        runTest {
            val expected =
                ExchangeRate(
                    from = "PLN",
                    to = "UAH",
                    rate = BigDecimal(10.0),
                    fromAmount = BigDecimal(300),
                    toAmount = BigDecimal(3000),
                )

            coEvery { mockUseCase("PLN", "UAH", BigDecimal(300)) } returns expected

            viewModel.exchangeResult.test {
                viewModel.getExchangeRate()

                // Run all coroutines
                testDispatcher.scheduler.advanceUntilIdle()

                // Initial state
                assertEquals(null, awaitItem())

                // Emitted exchange
                assertEquals(expected, awaitItem())

                cancelAndConsumeRemainingEvents()
            }

            // Also check that toAmount is updated
            assertEquals(expected.toAmount, viewModel.toAmount.value)
        }

    @Test
    fun `getExchangeRate returns error for zero amount`() =
        runTest {
            coEvery {
                mockUseCase(any(), any(), BigDecimal.ZERO)
            } throws Exception("AMOUNT_TOO_LOW")

            viewModel.updateFromAmount("0") // sets fromAmount to 0

            viewModel.exchangeResult.test {
                viewModel.getExchangeRate(true)
                testDispatcher.scheduler.advanceUntilIdle()

                // No new value emitted because use case throws
                assertEquals(null, awaitItem())

                cancelAndConsumeRemainingEvents()
            }

            // toAmount should remain 0
            assertTrue(viewModel.toAmount.value.compareTo(BigDecimal.ZERO) == 0)
        }

    @Test
    fun `fromAmountExceeded is true when amount exceeds limit`() =
        runTest {
            // PLN limit is 20000
            viewModel.updateFromAmount("25000.00")

            // Trigger validation manually
            viewModel.getExchangeRate()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.fromAmountExceeded.value)
        }

    @Test
    fun `fromAmountExceeded is false when amount below limit`() =
        runTest {
            viewModel.updateFromAmount("15000")

            viewModel.getExchangeRate()
            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(viewModel.fromAmountExceeded.value)
        }

    @Test
    fun `swapCurrency swaps from and to currencies`() =
        runTest {
            val originalFrom = viewModel.fromCurrency.value
            val originalTo = viewModel.toCurrency.value

            viewModel.swapCurrency()

            assertEquals(originalFrom, viewModel.toCurrency.value)
            assertEquals(originalTo, viewModel.fromCurrency.value)
        }

    // ----------------------------------------------------------------------------------------------
    @Test
    fun `updateFromAmount formats value correctly`() =
        runTest {
            viewModel.updateFromAmount("12345") // should become 123.45
            assertEquals(BigDecimal("123.45"), viewModel.fromAmount.value)
        }

    @Test
    fun `updateToAmount formats value and updates fromAmount when zero`() =
        runTest {
            viewModel.updateToAmount("000") // becomes 0.00
            assertEquals(BigDecimal("0.00"), viewModel.toAmount.value)
            assertEquals(BigDecimal("0.00"), viewModel.fromAmount.value)
        }

    @Test
    fun `getExchangeRate reversed updates fromAmount`() =
        runTest {
            val expected =
                ExchangeRate(
                    from = "UAH",
                    to = "PLN",
                    fromAmount = BigDecimal(100),
                    toAmount = BigDecimal(400),
                    rate = BigDecimal(4),
                )

            coEvery { mockUseCase("UAH", "PLN", BigDecimal(2)) } returns expected
            viewModel.toAmount.value = BigDecimal(2)

            viewModel.getExchangeRate(reversed = true)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(expected.toAmount, viewModel.fromAmount.value)
        }

    @Test
    fun `validateFromAmount updates when currency changes`() =
        runTest {
            // Set fromAmount to exceed UAH limit but not PLN limit
            viewModel.updateFromAmount("6000000") // → 60000.00
            testDispatcher.scheduler.advanceUntilIdle()

            // Still PLN, limit is 20000, so exceeded
            viewModel.getExchangeRate()
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(viewModel.fromAmountExceeded.value)

            // Change currency to EUR which has limit 5000
            viewModel.onFromCurrencySelected("EUR")
            viewModel.getExchangeRate()
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(viewModel.fromAmountExceeded.value)
        }

    @Test
    fun `show and hide modal sheets update state`() =
        runTest {
            viewModel.showChooseSendingCurrencySheet()
            assertTrue(viewModel.chooseFromCurrencyModalSheet.value)

            viewModel.hideChooseFromCurrencySheet()
            assertFalse(viewModel.chooseFromCurrencyModalSheet.value)

            viewModel.showChooseReceivingCurrencySheet()
            assertTrue(viewModel.chooseToCurrencyModalSheet.value)

            viewModel.hideChooseToCurrencySheet()
            assertFalse(viewModel.chooseToCurrencyModalSheet.value)
        }

    @Test
    fun `onToCurrencySelected swaps when equal to fromCurrency`() =
        runTest {
            viewModel.fromCurrency.value = "PLN"
            viewModel.toCurrency.value = "UAH"

            viewModel.onToCurrencySelected("PLN")

            assertEquals("PLN", viewModel.toCurrency.value)
            assertEquals("UAH", viewModel.fromCurrency.value)
        }

    @Test
    fun `searchField does not accept more than 20 characters`() =
        runTest {
            viewModel.updateSearchField("A".repeat(25))
            assertEquals("", viewModel.searchField.value)

            viewModel.updateSearchField("12345")
            assertEquals("12345", viewModel.searchField.value)
        }
}
