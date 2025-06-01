package onlytrade.app.viewmodel.login.di

import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.login.repository.data.remote.api.LoginApi
import onlytrade.app.viewmodel.login.ui.KycViewModel
import onlytrade.app.viewmodel.login.ui.LoginViewModel
import onlytrade.app.viewmodel.login.ui.usecase.EmailLoginUseCase
import onlytrade.app.viewmodel.login.ui.usecase.KycUseCase
import onlytrade.app.viewmodel.login.ui.usecase.PhoneLoginUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val loginModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::KycViewModel)
    factoryOf(::PhoneLoginUseCase)
    factoryOf(::EmailLoginUseCase)
    factoryOf(::KycUseCase)
    factoryOf(::LoginRepository)
    factoryOf(::LoginApi)
}

