package onlytrade.app.viewmodel.product.di

import onlytrade.app.viewmodel.product.repository.data.remote.api.DeleteProductApi
import onlytrade.app.viewmodel.product.repository.data.remote.api.GetUserProductsApi
import onlytrade.app.viewmodel.product.ui.MyProductsViewModel
import onlytrade.app.viewmodel.product.ui.usecase.DeleteProductUseCase
import onlytrade.app.viewmodel.product.ui.usecase.OfferUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val myProductsModule = module {
    viewModelOf(::MyProductsViewModel)
    factoryOf(::OfferUseCase)
    factoryOf(::DeleteProductUseCase)
    factoryOf(::GetUserProductsApi)
    factoryOf(::DeleteProductApi)
}
