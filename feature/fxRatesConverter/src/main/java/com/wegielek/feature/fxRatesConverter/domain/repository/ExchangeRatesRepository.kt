package com.wegielek.feature.fxRatesConverter.domain.repository

import com.wegielek.feature.fxRatesConverter.domain.model.ExchangeRate

interface ExchangeRatesRepository {
    suspend fun getRates(
        from: String,
        to: String,
        amount: Double,
    ): ExchangeRate
}
