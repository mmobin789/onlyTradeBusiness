package onlytrade.app.viewmodel.profile.di

import onlytrade.app.viewmodel.profile.ui.ProfileViewModel
import onlytrade.app.viewmodel.profile.usecase.GetProfileUseCase
import onlytrade.app.viewmodel.profile.usecase.LogoutUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
    factoryOf(::GetProfileUseCase)
    factoryOf(::LogoutUseCase)
}