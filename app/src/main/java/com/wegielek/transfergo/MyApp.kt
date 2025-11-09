package com.wegielek.transfergo

import android.app.Application
import com.wegielek.feature.fxRatesConverter.di.currencyConverterNetworkModule
import com.wegielek.feature.fxRatesConverter.di.currencyConverterRepositoryModule
import com.wegielek.feature.fxRatesConverter.di.currencyConverterUseCaseModule
import com.wegielek.feature.fxRatesConverter.di.currencyConverterViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(
                currencyConverterNetworkModule,
                currencyConverterRepositoryModule,
                currencyConverterUseCaseModule,
                currencyConverterViewModelModule,
            )
        }
    }
}
