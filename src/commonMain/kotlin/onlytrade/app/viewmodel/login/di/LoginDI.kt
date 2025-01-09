package onlytrade.app.viewmodel.login.di

import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.login.repository.data.remote.LoginWebApi
import onlytrade.app.viewmodel.login.ui.LoginViewModel
import onlytrade.app.viewmodel.login.usecase.EmailLoginUseCase
import onlytrade.app.viewmodel.login.usecase.MobileLoginUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object LoginDI {

    val module = module {
        viewModelOf(::LoginViewModel)
        factoryOf(::LoginWebApi)
        factoryOf(::LoginRepository)
        factoryOf(::MobileLoginUseCase)
        factoryOf(::EmailLoginUseCase)

    }

}