package onlytrade.app.viewmodel.product.di

import onlytrade.app.viewmodel.product.ui.ProductDetailViewModel
import onlytrade.app.viewmodel.product.ui.usecase.GetProductDetailUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productDetailModule = module {
    viewModelOf(::ProductDetailViewModel)
    factoryOf(::GetProductDetailUseCase)
}
