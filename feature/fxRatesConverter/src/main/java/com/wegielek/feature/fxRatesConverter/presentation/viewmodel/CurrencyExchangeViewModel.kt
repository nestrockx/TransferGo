package com.wegielek.feature.fxRatesConverter.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wegielek.feature.fxRatesConverter.data.network.NetworkObserver
import com.wegielek.feature.fxRatesConverter.domain.model.ExchangeRate
import com.wegielek.feature.fxRatesConverter.domain.usecase.GetExchangeRateUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CurrencyExchangeViewModel(
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
) : ViewModel() {
    val logTag = "CurrencyExchangeViewmodel"

    var fromCurrency = MutableStateFlow("PLN")
        private set
    var toCurrency = MutableStateFlow("UAH")
        private set
    var fromAmount = MutableStateFlow(300.0)
        private set
    var toAmount = MutableStateFlow(0.0)
        private set

    private val _exchangeResult = MutableStateFlow<ExchangeRate?>(null)
    val exchangeResult: StateFlow<ExchangeRate?> = _exchangeResult.asStateFlow()

    fun onToCurrencySelected(currency: String) {
        toCurrency.value = currency
    }

    fun onFromCurrencySelected(currency: String) {
        fromCurrency.value = currency
    }

    fun updateFromAmount(amount: String) {
        val updatedFromAmount = amount.toDoubleOrNull() ?: return
        fromAmount.value = updatedFromAmount
        getRate()
    }

    fun updateToAmount(amount: String) {
        val updatedToAmount = amount.toDoubleOrNull() ?: return
        toAmount.value = updatedToAmount
        getRate(true)
    }

    fun swapCurrency() {
        val tmp = fromCurrency.value
        fromCurrency.value = toCurrency.value
        toCurrency.value = tmp
    }

    init {
        getRate()
    }

    fun getRate(flipped: Boolean = false) {
        if (fromCurrency.value.isEmpty() || toCurrency.value.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _exchangeResult.value = getExchangeRateUseCase(fromCurrency.value, toCurrency.value, fromAmount.value)
                if (flipped) {
                    fromAmount.value = _exchangeResult.value?.toAmount ?: 0.0
                } else {
                    toAmount.value = _exchangeResult.value?.toAmount ?: 0.0
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(logTag, "Error: ${e.message}")
            }
        }
    }
}
