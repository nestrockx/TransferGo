package com.wegielek.feature.fxRatesConverter.domain.model

import java.math.BigDecimal

data class ExchangeRate(
    val from: String,
    val to: String,
    val rate: BigDecimal,
    val fromAmount: BigDecimal,
    val toAmount: BigDecimal,
)
