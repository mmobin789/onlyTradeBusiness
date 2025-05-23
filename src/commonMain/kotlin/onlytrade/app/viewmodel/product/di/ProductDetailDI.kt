package onlytrade.app.viewmodel.product.di

import onlytrade.app.viewmodel.product.offer.repository.OfferRepository
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.AddOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.DeleteOfferApi
import onlytrade.app.viewmodel.product.offer.ui.usecase.WithdrawOfferUseCase
import onlytrade.app.viewmodel.product.ui.ProductDetailViewModel
import onlytrade.app.viewmodel.product.ui.usecase.OfferUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productDetailModule = module {
    viewModelOf(::ProductDetailViewModel)
    factoryOf(::OfferUseCase)
    factoryOf(::WithdrawOfferUseCase)
    factoryOf(::OfferRepository)
    factoryOf(::AddOfferApi)
    factoryOf(::DeleteOfferApi)
}
