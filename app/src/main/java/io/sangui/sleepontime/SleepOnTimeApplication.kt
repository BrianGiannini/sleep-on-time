package io.sangui.sleepontime

import android.app.Application
import io.sangui.sleepontime.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SleepOnTimeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SleepOnTimeApplication)
            modules(appModule)
        }
    }
}