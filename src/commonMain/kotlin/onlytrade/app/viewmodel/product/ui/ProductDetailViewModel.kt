package onlytrade.app.viewmodel.product.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.offer.repository.OfferRepository
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.CheckMyOffer
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.GuestUser
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.Idle
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.MakeOfferFail
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.MakingOffer
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.MyOfferPlaced
import onlytrade.app.viewmodel.product.ui.usecase.OfferUseCase

class ProductDetailViewModel(
    private val offerUseCase: OfferUseCase,
    private val loginRepository: LoginRepository,
    private val offerRepository: OfferRepository
) : ViewModel() {

    var uiState: MutableStateFlow<ProductDetailUiState> = MutableStateFlow(Idle)
        private set

    private val user = loginRepository.user()

    fun idle() {
        uiState.value = Idle
    }

    fun getMyOffer(offerReceiverProductId: Long) {

        if (loginRepository.isUserLoggedIn().not()) {
            uiState.value = GuestUser
            return
        }

        uiState.value = CheckMyOffer

        viewModelScope.launch {
            withContext(IODispatcher) {
                offerRepository.getMyOffer(
                    offerMakerId = user!!.id,
                    offerReceiverProductId
                )
            }?.run {
                uiState.value = MyOfferPlaced(this)
            }
        }
    }

    fun makeOffer(productId: Long, offerReceiverId: Long, offeredProductIds: HashSet<Long>) {
        uiState.value = MakingOffer
        viewModelScope.launch {
            uiState.value = when (val result = offerUseCase(
                offerReceiverId = offerReceiverId,
                offerReceiverProductId = productId,
                offeredProductIds = offeredProductIds,
            )) {
                is OfferUseCase.Result.Error -> MakeOfferFail
                is OfferUseCase.Result.OfferMade -> MyOfferPlaced(result.offer)
            }
        }
    }

    /**
     * Memory-check.
     * This is performed before local db check.
     */
    fun gotAnOffer(offerReceiverId: Long) = user?.id == offerReceiverId

    /**
     * Memory-check.
     * This is performed before local db check.
     */
    fun hasMyOffer(offerMakerId: Long) = user?.id == offerMakerId

    /**
     * Each product has it's user's id.
     * If product's user id is same as logged in user's id,then it's logged in user's product.
     */
    fun isMyProduct(productUserId: Long) = user?.id == productUserId

}