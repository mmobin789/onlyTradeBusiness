package onlytrade.app.viewmodel.trades.di

import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.GetOffersApi
import onlytrade.app.viewmodel.product.offer.ui.usecase.GetOffersMadeUseCase
import onlytrade.app.viewmodel.product.offer.ui.usecase.GetOffersReceivedUseCase
import onlytrade.app.viewmodel.product.offer.ui.usecase.RejectOfferUseCase
import onlytrade.app.viewmodel.trades.ui.MyTradesViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val myTradesModule = module {
    viewModelOf(::MyTradesViewModel)
    factoryOf(::GetOffersMadeUseCase)
    factoryOf(::GetOffersReceivedUseCase)
    factoryOf(::RejectOfferUseCase)
    factoryOf(::GetOffersApi)
}
