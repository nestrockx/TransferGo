package com.wegielek.feature.fxRatesConverter.data.model

import com.wegielek.feature.fxRatesConverter.domain.model.ExchangeRate
import java.math.BigDecimal

data class ExchangeRateDto(
    val from: String,
    val to: String,
    val rate: BigDecimal,
    val fromAmount: BigDecimal,
    val toAmount: BigDecimal,
) {
    fun toDomain() = ExchangeRate(from, to, rate, fromAmount, toAmount)
}
