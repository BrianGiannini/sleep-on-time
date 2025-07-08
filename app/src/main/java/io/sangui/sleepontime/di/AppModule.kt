package io.sangui.sleepontime.di

import com.google.gson.Gson
import io.sangui.sleepontime.data.DataStoreManager
import io.sangui.sleepontime.data.TimeManager
import io.sangui.sleepontime.ui.ParameterViewModel
import io.sangui.sleepontime.ui.TimeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Gson() }
    single { DataStoreManager(androidContext(), get()) }
    single { TimeManager(androidContext()) }
    viewModel { TimeViewModel(get(), get()) }
    viewModel { ParameterViewModel(get()) }
}