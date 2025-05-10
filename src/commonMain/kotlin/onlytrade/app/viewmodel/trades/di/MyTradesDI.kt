package onlytrade.app.viewmodel.trades.di

import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.AcceptOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.CompleteOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.GetOffersApi
import onlytrade.app.viewmodel.product.offer.ui.usecase.AcceptOfferUseCase
import onlytrade.app.viewmodel.product.offer.ui.usecase.CompleteOfferUseCase
import onlytrade.app.viewmodel.product.offer.ui.usecase.GetOffersMadeUseCase
import onlytrade.app.viewmodel.product.offer.ui.usecase.GetOffersReceivedUseCase
import onlytrade.app.viewmodel.product.offer.ui.usecase.RejectOfferUseCase
import onlytrade.app.viewmodel.trades.ui.MyTradesViewModel
import onlytrade.app.viewmodel.trades.ui.TradeDetailViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val myTradesModule = module {
    viewModelOf(::MyTradesViewModel)
    viewModelOf(::TradeDetailViewModel)
    factoryOf(::GetOffersMadeUseCase)
    factoryOf(::GetOffersReceivedUseCase)
    factoryOf(::RejectOfferUseCase)
    factoryOf(::AcceptOfferUseCase)
    factoryOf(::CompleteOfferUseCase)
    factoryOf(::GetOffersApi)
    factoryOf(::AcceptOfferApi)
    factoryOf(::CompleteOfferApi)
}
