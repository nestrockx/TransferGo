package com.wegielek.feature.fxRatesConverter.di

import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.ConnectionErrorPopupViewModel
import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.CurrencyExchangeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val currencyConverterViewModelModule =
    module {
        viewModel { CurrencyExchangeViewModel(get()) }
        viewModel { ConnectionErrorPopupViewModel(get()) }
    }
