package com.wegielek.feature.fxRatesConverter.di

import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.CurrencyExchangeViewmodel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ratesViewModelModule =
    module {
        viewModel { CurrencyExchangeViewmodel(get()) }
    }
