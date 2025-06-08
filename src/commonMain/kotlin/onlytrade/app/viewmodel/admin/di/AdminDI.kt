package onlytrade.app.viewmodel.admin.di

import onlytrade.app.viewmodel.admin.AdminViewModel
import onlytrade.app.viewmodel.admin.usecase.GetApprovalProductsUseCase
import onlytrade.app.viewmodel.admin.usecase.GetApprovalUsersUseCase
import onlytrade.app.viewmodel.admin.usecase.VerifyProductUseCase
import onlytrade.app.viewmodel.admin.usecase.VerifyUserUseCase
import onlytrade.app.viewmodel.login.repository.data.remote.api.GetApprovalUsersApi
import onlytrade.app.viewmodel.login.repository.data.remote.api.VerifyUserApi
import onlytrade.app.viewmodel.product.repository.data.remote.api.GetApprovalProductsApi
import onlytrade.app.viewmodel.product.repository.data.remote.api.VerifyProductApi
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val adminModule = module {
    viewModelOf(::AdminViewModel)
    factoryOf(::VerifyProductUseCase)
    factoryOf(::GetApprovalProductsUseCase)
    factoryOf(::VerifyUserUseCase)
    factoryOf(::GetApprovalUsersUseCase)
    factoryOf(::VerifyProductApi)
    factoryOf(::GetApprovalProductsApi)
    factoryOf(::VerifyUserApi)
    factoryOf(::GetApprovalUsersApi)
}