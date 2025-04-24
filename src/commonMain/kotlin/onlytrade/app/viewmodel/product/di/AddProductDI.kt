package onlytrade.app.viewmodel.product.di

import onlytrade.app.viewmodel.product.repository.ProductRepository
import onlytrade.app.viewmodel.product.repository.data.remote.api.AddProductApi
import onlytrade.app.viewmodel.product.ui.AddProductViewModel
import onlytrade.app.viewmodel.product.ui.usecase.AddProductUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val addProductModule = module {
    viewModelOf(::AddProductViewModel)
    factoryOf(::AddProductUseCase)
    factoryOf(::ProductRepository)
    factoryOf(::AddProductApi)
}
