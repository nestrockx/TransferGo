package com.wegielek.transfergo

import android.app.Application
import com.wegielek.feature.fxRatesConverter.di.exchangeRatesRepositoryModule
import com.wegielek.feature.fxRatesConverter.di.ratesNetworkModule
import com.wegielek.feature.fxRatesConverter.di.ratesUseCaseModule
import com.wegielek.feature.fxRatesConverter.di.ratesViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(
                ratesNetworkModule,
                exchangeRatesRepositoryModule,
                ratesUseCaseModule,
                ratesViewModelModule,
            )
        }
    }
}
