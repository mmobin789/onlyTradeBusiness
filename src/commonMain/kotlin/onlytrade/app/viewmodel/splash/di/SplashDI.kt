package onlytrade.app.viewmodel.splash.di

import onlytrade.app.viewmodel.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val splashModule = module {
    viewModelOf(::SplashViewModel)
}
