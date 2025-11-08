package com.wegielek.feature.fxRatesConverter.domain.usecase

import com.wegielek.feature.fxRatesConverter.domain.repository.ExchangeRatesRepository
import java.math.BigDecimal

class GetExchangeRateUseCase(
    private val exchangeRatesRepository: ExchangeRatesRepository,
) {
    suspend operator fun invoke(
        from: String,
        to: String,
        amount: BigDecimal,
    ) = exchangeRatesRepository.getExchangeRate(from, to, amount)
}
