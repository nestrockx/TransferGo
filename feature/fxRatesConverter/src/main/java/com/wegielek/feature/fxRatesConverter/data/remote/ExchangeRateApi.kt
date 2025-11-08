package com.wegielek.feature.fxRatesConverter.data.remote

import com.wegielek.feature.fxRatesConverter.data.model.ExchangeRateDto
import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigDecimal

interface ExchangeRateApi {
    @GET("fx-rates/")
    suspend fun getExchangeRate(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: BigDecimal,
    ): ExchangeRateDto?
}
