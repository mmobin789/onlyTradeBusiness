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

    fun idle() {
        uiState.value = Idle
    }

    init {
        getOffersMade()
    }

    fun getOffersMade() {
        uiState.value = LoadingOffersMade

        viewModelScope.launch {
            when (val result =
                getOffersMadeUseCase()) {
                is GetOffersMadeUseCase.Result.Offers -> {
                    uiState.value = OffersMade(result.offers)
                }


                GetOffersMadeUseCase.Result.OffersNotFound -> {
                    uiState.value = NoOffersMade
                }

                is GetOffersMadeUseCase.Result.Error -> {
                    uiState.value = OffersMadeError(error = result.error)
                }

            }
        }
    }

    fun getOffersReceived() {
        uiState.value = LoadingOffersMade

        viewModelScope.launch {
            when (val result =
                getOffersReceivedUseCase()) {
                is GetOffersReceivedUseCase.Result.Offers -> {
                    uiState.value = OffersReceived(result.offers)
                }


                GetOffersReceivedUseCase.Result.OffersNotFound -> {
                    uiState.value = NoOffersReceived
                }

                is GetOffersReceivedUseCase.Result.Error -> {
                    uiState.value = OffersReceivedError(error = result.error)
                }

            }
        }
    }

}