package com.wegielek.feature.fxRatesConverter.domain.repository

import com.wegielek.feature.fxRatesConverter.domain.model.ExchangeRate
import java.math.BigDecimal

interface ExchangeRatesRepository {
    suspend fun getExchangeRate(
        from: String,
        to: String,
        amount: BigDecimal,
    ): ExchangeRate?
}
