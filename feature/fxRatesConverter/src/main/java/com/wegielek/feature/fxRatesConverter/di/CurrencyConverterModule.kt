package com.wegielek.feature.fxRatesConverter.di

import com.wegielek.feature.fxRatesConverter.data.repository.ExchangeRatesRepositoryImpl
import com.wegielek.feature.fxRatesConverter.domain.repository.ExchangeRatesRepository
import org.koin.dsl.module

val currencyConverterRepositoryModule =
    module {
        single<ExchangeRatesRepository> { ExchangeRatesRepositoryImpl(get()) }
    }
