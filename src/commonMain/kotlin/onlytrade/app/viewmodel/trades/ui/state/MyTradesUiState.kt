package onlytrade.app.viewmodel.trades.ui.state

import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer

sealed class MyTradesUiState {
    data object Idle : MyTradesUiState()
    data object LoadingOffersMade : MyTradesUiState()
    data object NoOffersMade : MyTradesUiState()
    data class OffersMade(val offers: List<Offer>) : MyTradesUiState()
    data class OffersMadeError(val error: String) : MyTradesUiState()
    data object LoadingOffersReceived : MyTradesUiState()
    data object NoOffersReceived : MyTradesUiState()
    data class OffersReceived(val offers: List<Offer>) : MyTradesUiState()
    data class OffersReceivedError(val error: String) : MyTradesUiState()
    data object OfferDeleted : MyTradesUiState()
    data object OfferNotFound : MyTradesUiState()
    data class OfferDeleteApiError(val error: String) : MyTradesUiState()
}