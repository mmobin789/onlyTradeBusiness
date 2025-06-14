package onlytrade.app.viewmodel.trades.ui.state

import onlytrade.app.viewmodel.login.repository.data.db.User
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer

sealed class TradeDetailUiState {
    data object Idle : TradeDetailUiState()
    data object LoadingOfferMade : TradeDetailUiState()
    data object LoadingOfferReceived : TradeDetailUiState()
    data object OfferReceived : TradeDetailUiState()
    data object OfferNotReceived : TradeDetailUiState()
    data class OfferMade(val offer: Offer) : TradeDetailUiState()
    data object OfferNotMade : TradeDetailUiState()
    data object WithdrawingOffer : TradeDetailUiState()
    data object RejectingOffer : TradeDetailUiState()
    data object AcceptingOffer : TradeDetailUiState()
    data object OfferDeleted : TradeDetailUiState()
    data object OfferRejected : TradeDetailUiState()
    data object OfferAccepted : TradeDetailUiState()
    data object LoadUserDetail : TradeDetailUiState()
    data class UserDetail(val user: User) : TradeDetailUiState()
    data class UserDetailApiError(val error: String) : TradeDetailUiState()
    data class OfferDeleteApiError(val error: String) : TradeDetailUiState()
    data class OfferAcceptApiError(val error: String) : TradeDetailUiState()
    data object CompletingOffer : TradeDetailUiState()
    data object OfferCompleted : TradeDetailUiState()
    data class OfferCompleteApiError(val error: String) : TradeDetailUiState()
}