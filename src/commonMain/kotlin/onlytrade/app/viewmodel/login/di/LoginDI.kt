package onlytrade.app.viewmodel.login.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import onlytrade.app.viewmodel.login.LoginViewModel
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.login.repository.data.remote.LoginWebApi
import onlytrade.app.viewmodel.login.usecase.EmailLoginUseCase
import onlytrade.app.viewmodel.login.usecase.MobileLoginUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object LoginDI {

    val module = module {
        viewModelOf(::LoginViewModel)
        singleOf(::LoginWebApi)
        factoryOf(::LoginRepository)
        single {
            CoroutineScope(Dispatchers.IO)
        }
        factoryOf(::MobileLoginUseCase)
        factoryOf(::EmailLoginUseCase)

    }

}