package onlytrade.app.viewmodel.product.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.component.AppScope
import onlytrade.app.viewmodel.home.ui.HomeNav
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.offer.repository.OfferRepository
import onlytrade.app.viewmodel.product.offer.ui.usecase.WithdrawOfferUseCase
import onlytrade.app.viewmodel.product.repository.data.db.Product
import onlytrade.app.viewmodel.product.ui.nav.ProductDetailNav
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.GuestUser
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.Idle
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.LoadingOfferMade
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.LoadingOfferReceived
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.MakeOfferFail
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.MakingOffer
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.OfferDeleteApiError
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.OfferMade
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.OfferNotMade
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.OfferNotReceived
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.OfferReceived
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.OfferRejected
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.OfferWithdrawn
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.OffersExceeded
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.WithdrawingOffer
import onlytrade.app.viewmodel.product.ui.usecase.OfferUseCase

class ProductDetailViewModel(
    private val offerUseCase: OfferUseCase,
    private val withdrawOfferUseCase: WithdrawOfferUseCase,
    private val loginRepository: LoginRepository,
    private val offerRepository: OfferRepository
) : ViewModel() {

    var uiState: MutableStateFlow<ProductDetailUiState> = MutableStateFlow(Idle)
        private set

    private val user = loginRepository.user()

    private lateinit var offeredProductIds: LinkedHashSet<Long>

    init {
        viewModelScope.launch {
            ProductDetailNav.events.collect { event ->
                when (event) {
                    is ProductDetailNav.Event.TradeProducts -> {
                        offeredProductIds = event.productIds
                    }
                }
            }
        }
    }

    fun idle() {
        uiState.value = Idle
    }

    private fun refreshHomeScreen() {
        viewModelScope.launch { HomeNav.emit(HomeNav.Event.RefreshHome) }
    }

    fun checkOffer(product: Product) {

        if (loginRepository.isUserLoggedIn().not()) {
            uiState.value = GuestUser
            return
        }

        if (isMyProduct(product.userId).not()) {
            getOfferMade(product.id)
        } else {
            getOfferReceived(product.id)
        }
    }


    fun withdrawOffer(offerReceiverProductId: Long) {
        uiState.value = WithdrawingOffer
        AppScope.launch {
            uiState.value = when (val result =
                withdrawOfferUseCase(
                    offerMakerId = user!!.id,
                    offerReceiverProductId = offerReceiverProductId
                )) {
                WithdrawOfferUseCase.Result.OfferDeleted -> OfferWithdrawn
                WithdrawOfferUseCase.Result.OfferNotFound -> OfferRejected
                is WithdrawOfferUseCase.Result.Error -> OfferDeleteApiError(result.error)
            }
            refreshHomeScreen()
        }
    }


    private fun getOfferMade(offerReceiverProductId: Long) {

        uiState.value = LoadingOfferMade

        viewModelScope.launch {
            withContext(IODispatcher) {
                offerRepository.getOfferMade(
                    offerMakerId = user!!.id,
                    offerReceiverProductId
                )
            }.let { offer ->
                uiState.value =
                    if (offer == null) OfferNotMade else OfferMade(offer = offer)
            }
        }
    }

    private fun getOfferReceived(offerReceiverProductId: Long) {

        uiState.value = LoadingOfferReceived

        viewModelScope.launch {
            withContext(IODispatcher) {
                offerRepository.getOfferReceived(
                    offerReceiverId = user!!.id,
                    offerReceiverProductId
                )
            }.let { offer ->
                uiState.value =
                    if (offer == null) OfferNotReceived else OfferReceived
            }
        }
    }

    fun makeOffer(productId: Long, offerReceiverId: Long) {
        if (::offeredProductIds.isInitialized.not() || offeredProductIds.isEmpty())
            return

        uiState.value = MakingOffer

        AppScope.launch {

            uiState.value = when (val result = offerUseCase(
                offerReceiverId = offerReceiverId,
                offerReceiverProductId = productId,
                offeredProductIds = offeredProductIds,
            )) {
                OfferUseCase.Result.OffersExceeded -> OffersExceeded
                is OfferUseCase.Result.Error -> MakeOfferFail
                is OfferUseCase.Result.OfferMade -> OfferMade(result.offer).apply {
                    offeredProductIds.clear()
                }
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