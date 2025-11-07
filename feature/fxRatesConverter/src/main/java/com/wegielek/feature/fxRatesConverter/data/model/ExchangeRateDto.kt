package com.wegielek.feature.fxRatesConverter.data.model

import com.wegielek.feature.fxRatesConverter.domain.model.ExchangeRate

data class ExchangeRateDto(
    val from: String,
    val to: String,
    val rate: Double,
    val fromAmount: Double,
    val toAmount: Double,
) {
    fun toDomain() = ExchangeRate(from, to, rate, fromAmount, toAmount)
}
