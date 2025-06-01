package onlytrade.app.viewmodel.profile.di

import onlytrade.app.viewmodel.login.repository.UserRepository
import onlytrade.app.viewmodel.login.repository.data.remote.api.GetUserDetailApi
import onlytrade.app.viewmodel.login.repository.data.remote.api.KycApi
import onlytrade.app.viewmodel.profile.ui.ProfileViewModel
import onlytrade.app.viewmodel.profile.usecase.GetUserDetailUseCase
import onlytrade.app.viewmodel.profile.usecase.LogoutUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
    factoryOf(::GetUserDetailUseCase)
    factoryOf(::UserRepository)
    factoryOf(::KycApi)
    factoryOf(::GetUserDetailApi)
    factoryOf(::LogoutUseCase)
}