package com.wegielek.feature.fxRatesConverter.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wegielek.feature.fxRatesConverter.data.network.NetworkObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ConnectionErrorPopupViewModel(
    private val networkObserver: NetworkObserver,
) : ViewModel() {
    val isConnected =
        networkObserver.isConnected
            .stateIn(viewModelScope, SharingStarted.Lazily, true)

    var showInternetConnectionError = MutableStateFlow(false)
        private set

    fun showInternetConnectionError() {
        showInternetConnectionError.value = true
    }

    fun hideInternetConnectionError() {
        showInternetConnectionError.value = false
    }
}
