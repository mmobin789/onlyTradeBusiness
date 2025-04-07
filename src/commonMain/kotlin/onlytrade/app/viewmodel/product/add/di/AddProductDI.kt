package onlytrade.app.viewmodel.product.add.di

import onlytrade.app.viewmodel.product.add.ui.AddProductViewModel
import onlytrade.app.viewmodel.product.repository.ProductRepository
import onlytrade.app.viewmodel.product.repository.data.remote.ProductApi
import onlytrade.app.viewmodel.product.usecase.AddProductUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object AddProductDI {
    val module = module {
        viewModelOf(::AddProductViewModel)
        factoryOf(::AddProductUseCase)
        factoryOf(::ProductRepository)
        factoryOf(::ProductApi)
    }
}