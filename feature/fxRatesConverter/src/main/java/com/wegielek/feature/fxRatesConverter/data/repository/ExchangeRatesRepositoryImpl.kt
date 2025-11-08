package com.wegielek.feature.fxRatesConverter.data.repository

import com.wegielek.feature.fxRatesConverter.data.remote.ExchangeRateApi
import com.wegielek.feature.fxRatesConverter.domain.model.ExchangeRate
import com.wegielek.feature.fxRatesConverter.domain.repository.ExchangeRatesRepository
import java.math.BigDecimal

class ExchangeRatesRepositoryImpl(
    private val exchangeRateApi: ExchangeRateApi,
) : ExchangeRatesRepository {
    override suspend fun getExchangeRate(
        from: String,
        to: String,
        amount: BigDecimal,
    ): ExchangeRate? = exchangeRateApi.getExchangeRate(from, to, amount)?.toDomain()
}
