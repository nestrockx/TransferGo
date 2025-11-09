package com.wegielek.feature.fxRatesConverter.di

import com.wegielek.feature.fxRatesConverter.data.network.NetworkObserver
import com.wegielek.feature.fxRatesConverter.data.remote.ExchangeRateApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val forceHttpsInterceptor =
    Interceptor { chain ->
        val request = chain.request()
        val httpsUrl =
            request.url
                .newBuilder()
                .scheme("https")
                .host("my.transfergo.com")
                .build()

        val httpsRequest = request.newBuilder().url(httpsUrl).build()
        chain.proceed(httpsRequest)
    }

val currencyConverterNetworkModule =
    module {
        single {
            val logging =
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

            OkHttpClient
                .Builder()
                .addInterceptor(logging)
                .build()
        }

        single {
            Retrofit
                .Builder()
                .baseUrl("https://my.transfergo.com/api/")
                .client(get())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        single<ExchangeRateApi> {
            get<Retrofit>().create(ExchangeRateApi::class.java)
        }

        single { NetworkObserver(get()) }
    }
