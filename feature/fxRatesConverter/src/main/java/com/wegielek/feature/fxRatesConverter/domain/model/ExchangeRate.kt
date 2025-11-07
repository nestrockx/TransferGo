package com.wegielek.feature.fxRatesConverter.domain.model

data class ExchangeRate(
    val from: String,
    val to: String,
    val rate: Double,
    val fromAmount: Double,
    val toAmount: Double,
)
