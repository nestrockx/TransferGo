package com.wegielek.feature.fxRatesConverter.domain.usecase

import com.wegielek.feature.fxRatesConverter.domain.repository.ExchangeRatesRepository

class GetExchangeRateUseCase(
    private val exchangeRatesRepository: ExchangeRatesRepository,
) {
    suspend operator fun invoke(
        from: String,
        to: String,
        amount: Double,
    ) = exchangeRatesRepository.getRates(from, to, amount)
}
