package onlytrade.app.viewmodel.home.di

import onlytrade.app.viewmodel.home.ui.HomeViewModel
import onlytrade.app.viewmodel.home.ui.usecase.GetProductsUseCase
import onlytrade.app.viewmodel.product.repository.ProductRepository
import onlytrade.app.viewmodel.product.repository.data.remote.api.GetProductsApi
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val homeModule = module {
    viewModelOf(::HomeViewModel)
    factoryOf(::GetProductsUseCase)
    factoryOf(::ProductRepository)
    factoryOf(::GetProductsApi)
}
