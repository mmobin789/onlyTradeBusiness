package onlytrade.app.viewmodel.trades.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.product.offer.ui.usecase.GetOffersMadeUseCase
import onlytrade.app.viewmodel.product.offer.ui.usecase.GetOffersReceivedUseCase
import onlytrade.app.viewmodel.trades.ui.state.MyTradesUiState
import onlytrade.app.viewmodel.trades.ui.state.MyTradesUiState.Idle
import onlytrade.app.viewmodel.trades.ui.state.MyTradesUiState.LoadingOffersMade
import onlytrade.app.viewmodel.trades.ui.state.MyTradesUiState.LoadingOffersReceived
import onlytrade.app.viewmodel.trades.ui.state.MyTradesUiState.NoOffersMade
import onlytrade.app.viewmodel.trades.ui.state.MyTradesUiState.NoOffersReceived
import onlytrade.app.viewmodel.trades.ui.state.MyTradesUiState.OffersMade
import onlytrade.app.viewmodel.trades.ui.state.MyTradesUiState.OffersMadeError
import onlytrade.app.viewmodel.trades.ui.state.MyTradesUiState.OffersReceived
import onlytrade.app.viewmodel.trades.ui.state.MyTradesUiState.OffersReceivedError

class MyTradesViewModel(
    private val getOffersMadeUseCase: GetOffersMadeUseCase,
    private val getOffersReceivedUseCase: GetOffersReceivedUseCase
) : ViewModel() {

    var uiState: MutableStateFlow<MyTradesUiState> = MutableStateFlow(Idle)
        private set

    private var refreshMyTrades = false


    fun idle() {
        uiState.value = Idle
    }

    init {
        getOffersMade()
        viewModelScope.launch {
            MyTradesNav.events.collect { event ->
                refreshMyTrades = event == MyTradesNav.Event.RefreshMyTrades
            }
        }
    }

    fun refreshMyTradesPage() {
        if (refreshMyTrades) {
            refreshMyTrades = false
            getOffersMade()
            getOffersReceived()
        }
    }

    fun getOffersMade() {
        uiState.value = LoadingOffersMade

        viewModelScope.launch {
            uiState.value = when (val result =
                getOffersMadeUseCase()) {
                is GetOffersMadeUseCase.Result.Offers -> OffersMade(result.offers)
                GetOffersMadeUseCase.Result.OffersNotFound -> NoOffersMade
                is GetOffersMadeUseCase.Result.Error -> OffersMadeError(error = result.error)

            }
        }
    }

    fun getOffersReceived() {
        uiState.value = LoadingOffersReceived

        viewModelScope.launch {
            uiState.value = when (val result =
                getOffersReceivedUseCase()) {
                is GetOffersReceivedUseCase.Result.Offers -> OffersReceived(result.offers)
                GetOffersReceivedUseCase.Result.OffersNotFound -> NoOffersReceived
                is GetOffersReceivedUseCase.Result.Error -> OffersReceivedError(error = result.error)
            }
        }
    }
}