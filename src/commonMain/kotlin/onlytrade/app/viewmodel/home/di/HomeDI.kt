package onlytrade.app.viewmodel.home.di

import onlytrade.app.viewmodel.home.ui.HomeViewModel
import onlytrade.app.viewmodel.home.usecase.GetProductsUseCase
import onlytrade.app.viewmodel.product.repository.ProductRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object HomeDI {
    val module = module {
        viewModelOf(::HomeViewModel)
        factoryOf(::GetProductsUseCase)
        factoryOf(::ProductRepository)
    }
}