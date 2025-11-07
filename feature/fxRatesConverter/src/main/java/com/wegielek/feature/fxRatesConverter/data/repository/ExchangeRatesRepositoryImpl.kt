package com.wegielek.feature.fxRatesConverter.data.repository

import com.wegielek.feature.fxRatesConverter.data.remote.ExchangeRateApi
import com.wegielek.feature.fxRatesConverter.domain.model.ExchangeRate
import com.wegielek.feature.fxRatesConverter.domain.repository.ExchangeRatesRepository

class ExchangeRatesRepositoryImpl(
    private val exchangeRateApi: ExchangeRateApi,
) : ExchangeRatesRepository {
    override suspend fun getRates(
        from: String,
        to: String,
        amount: Double,
    ): ExchangeRate = exchangeRateApi.getRate(from, to, amount).toDomain()
}
