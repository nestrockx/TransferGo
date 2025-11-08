package com.wegielek.feature.fxRatesConverter.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wegielek.feature.fxRatesConverter.domain.model.ExchangeRate
import com.wegielek.feature.fxRatesConverter.domain.usecase.GetExchangeRateUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CurrencyExchangeViewModel(
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
) : ViewModel() {
    val logTag = "CurrencyExchangeViewmodel"

    val currencyLimits =
        mapOf(
            "PLN" to BigDecimal(20000.0),
            "EUR" to BigDecimal(5000.0),
            "GBP" to BigDecimal(1000.0),
            "UAH" to BigDecimal(50000.0),
        )

    var fromAmountExceeded = MutableStateFlow(false)
        private set

    var fromCurrency = MutableStateFlow("PLN")
        private set
    var toCurrency = MutableStateFlow("UAH")
        private set
    var fromAmount = MutableStateFlow(BigDecimal(300.0))
        private set
    var toAmount = MutableStateFlow(BigDecimal(0.0))
        private set

    private val _exchangeResult = MutableStateFlow<ExchangeRate?>(null)
    val exchangeResult: StateFlow<ExchangeRate?> = _exchangeResult.asStateFlow()

    var chooseFromCurrencyModalSheet = MutableStateFlow(false)
        private set

    var chooseToCurrencyModalSheet = MutableStateFlow(false)
        private set

    fun showChooseSendingCurrencySheet() {
        chooseFromCurrencyModalSheet.value = true
    }

    fun hideChooseFromCurrencySheet() {
        chooseFromCurrencyModalSheet.value = false
    }

    fun showChooseReceivingCurrencySheet() {
        chooseToCurrencyModalSheet.value = true
    }

    fun hideChooseToCurrencySheet() {
        chooseToCurrencyModalSheet.value = false
    }

    fun onToCurrencySelected(currency: String) {
        if (currency == fromCurrency.value) {
            swapCurrency()
        } else {
            toCurrency.value = currency
        }
    }

    fun onFromCurrencySelected(currency: String) {
        if (currency == toCurrency.value) {
            swapCurrency()
        } else {
            fromCurrency.value = currency
        }
    }

    fun updateFromAmount(amount: String) {
        var correctedAmount = amount
        if (!amount.contains(".")) {
            val len = amount.length
            correctedAmount =
                if (len > 2) {
                    amount.take(len - 2) + "." + amount.substring(len - 2)
                } else {
                    "0." + amount.padStart(2, '0')
                }
        }
        val normalizedAmount =
            correctedAmount
                .replace(" ", "")
                .replace(",", "")
        val updatedFromAmount = normalizedAmount.toBigDecimalOrNull() ?: return
        fromAmount.value = updatedFromAmount
    }

    fun updateToAmount(amount: String) {
        var correctedAmount = amount
        if (!amount.contains(".")) {
            val len = amount.length
            correctedAmount =
                if (len > 2) {
                    amount.take(len - 2) + "." + amount.substring(len - 2)
                } else {
                    "0." + amount.padStart(2, '0')
                }
        }
        val normalizedAmount =
            correctedAmount
                .replace(" ", "")
                .replace(",", "")
        val updatedToAmount = normalizedAmount.toBigDecimalOrNull() ?: return
        toAmount.value = updatedToAmount
    }

    fun swapCurrency() {
        val tmp = fromCurrency.value
        fromCurrency.value = toCurrency.value
        toCurrency.value = tmp
    }

    fun getExchangeRate(reversed: Boolean = false) {
        if (fromCurrency.value.isEmpty() || toCurrency.value.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (reversed) {
                    val reversedExchangeResult =
                        getExchangeRateUseCase(
                            toCurrency.value,
                            fromCurrency.value,
                            toAmount.value,
                        )
                    fromAmount.value = reversedExchangeResult?.toAmount ?: BigDecimal(0.0)
                } else {
                    _exchangeResult.value =
                        getExchangeRateUseCase(
                            fromCurrency.value,
                            toCurrency.value,
                            fromAmount.value,
                        )
                    toAmount.value = _exchangeResult.value?.toAmount ?: BigDecimal(0.0)
                }
                validateFromAmount()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(logTag, "Error: ${e.message}")
            }
        }
    }

    private fun validateFromAmount() {
        val limit = currencyLimits[fromCurrency.value] ?: return
        fromAmountExceeded.value = fromAmount.value > limit
    }

    var searchField = MutableStateFlow("")

    fun updateSearchField(value: String) {
        if (value.length > 20) return
        searchField.value = value
    }
}
