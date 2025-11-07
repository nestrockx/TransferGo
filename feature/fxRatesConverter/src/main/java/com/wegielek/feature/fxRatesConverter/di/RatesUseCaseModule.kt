package com.wegielek.feature.fxRatesConverter.di

import com.wegielek.feature.fxRatesConverter.domain.usecase.GetExchangeRateUseCase
import org.koin.dsl.module

val ratesUseCaseModule =
    module {
        single { GetExchangeRateUseCase(get()) }
    }
