package onlytrade.app.viewmodel.trades.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.component.AppScope
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.offer.repository.OfferRepository
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import onlytrade.app.viewmodel.product.offer.ui.usecase.AcceptOfferUseCase
import onlytrade.app.viewmodel.product.offer.ui.usecase.CompleteOfferUseCase
import onlytrade.app.viewmodel.product.offer.ui.usecase.RejectOfferUseCase
import onlytrade.app.viewmodel.product.offer.ui.usecase.WithdrawOfferUseCase
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.AcceptingOffer
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.CompletingOffer
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.Idle
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.LoadingOfferMade
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.LoadingOfferReceived
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.OfferAcceptApiError
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.OfferAccepted
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.OfferCompleteApiError
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.OfferCompleted
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.OfferDeleteApiError
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.OfferDeleted
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.OfferMade
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.OfferNotMade
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.OfferNotReceived
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.OfferReceived
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.OfferRejected
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.RejectingOffer
import onlytrade.app.viewmodel.trades.ui.state.TradeDetailUiState.WithdrawingOffer

class TradeDetailViewModel(
    private val withdrawOfferUseCase: WithdrawOfferUseCase,
    private val rejectOfferUseCase: RejectOfferUseCase,
    private val acceptOfferUseCase: AcceptOfferUseCase,
    private val completeOfferUseCase: CompleteOfferUseCase,
    loginRepository: LoginRepository,
    private val offerRepository: OfferRepository
) : ViewModel() {

    var uiState: MutableStateFlow<TradeDetailUiState> = MutableStateFlow(Idle)
        private set

    private val user = loginRepository.user()

    fun idle() {
        uiState.value = Idle
    }


    fun checkOffer(offer: Offer) {

        if (isMyProduct(offer.offerMakerId).not()) {
            getOfferMade(offer.offerReceiverProduct.id)
        } else {
            getOfferReceived(offer.offerReceiverProduct.id)
        }
    }

    fun checkOfferAccepted(offerId: Long) {
        viewModelScope.launch {
            uiState.value =
                withContext(IODispatcher) { offerRepository.getOfferAccepted(offerId) }?.let {
                    OfferAccepted
                } ?: Idle

        }
    }


    fun withdrawOffer(offerReceiverProductId: Long) {
        uiState.value = WithdrawingOffer
        AppScope.launch {
            uiState.value = when (val result = withdrawOfferUseCase(
                offerMakerId = user!!.id, offerReceiverProductId = offerReceiverProductId
            )) {
                WithdrawOfferUseCase.Result.OfferDeleted -> OfferDeleted
                WithdrawOfferUseCase.Result.OfferNotFound -> OfferRejected
                is WithdrawOfferUseCase.Result.Error -> OfferDeleteApiError(result.error)
            }
        }
    }

    fun acceptOffer(offer: Offer) {
        uiState.value = AcceptingOffer
        AppScope.launch {
            uiState.value = when (val result = acceptOfferUseCase(offer)) {
                AcceptOfferUseCase.Result.OfferAccepted -> OfferAccepted
                AcceptOfferUseCase.Result.OfferNotFound -> OfferDeleted
                is AcceptOfferUseCase.Result.Error -> OfferAcceptApiError(result.error)
            }
        }
    }

    fun rejectOffer(offer: Offer) {
        uiState.value = RejectingOffer
        AppScope.launch {
            uiState.value =
                when (val result = rejectOfferUseCase(offer)) {
                    RejectOfferUseCase.Result.OfferDeleted -> OfferRejected
                    RejectOfferUseCase.Result.OfferNotFound -> OfferDeleted
                    is RejectOfferUseCase.Result.Error -> OfferDeleteApiError(result.error)
                }
        }
    }

    fun completeOffer(offer: Offer) {
        uiState.value = CompletingOffer
        AppScope.launch {
            uiState.value = when (val result = completeOfferUseCase(offer)) {
                CompleteOfferUseCase.Result.OfferCompleted -> OfferCompleted
                CompleteOfferUseCase.Result.OfferNotFound -> OfferDeleted
                is CompleteOfferUseCase.Result.Error -> OfferCompleteApiError(result.error)
            }
        }
    }


    private fun getOfferMade(offerReceiverProductId: Long) {

        uiState.value = LoadingOfferMade

        viewModelScope.launch {
            offerRepository.getOfferMade(
                offerMakerId = user!!.id, offerReceiverProductId
            ).let { offer ->
                uiState.value = if (offer == null) OfferNotMade else OfferMade(offer = offer)
            }
        }
    }

    private fun getOfferReceived(offerReceiverProductId: Long) {

        uiState.value = LoadingOfferReceived

        viewModelScope.launch {
            offerRepository.getOfferReceived(
                offerReceiverId = user!!.id, offerReceiverProductId
            ).let { offer ->
                uiState.value = if (offer == null) OfferNotReceived else OfferReceived
            }
        }
    }

    /**
     * Memory-check.
     * This is performed before local db check.
     */
    fun receivedOffer(offerReceiverId: Long) = user?.id == offerReceiverId

    /**
     * Memory-check.
     * This is performed before local db check.
     */
    fun madeOffer(offerMakerId: Long) = user?.id == offerMakerId

    /**
     * Memory-check
     * This is performed before local db check.
     * Each product has it's user's id.
     * If product's user id is same as logged in user's id,then it's logged in user's product.
     */
    private fun isMyProduct(productUserId: Long) = user?.id == productUserId

}