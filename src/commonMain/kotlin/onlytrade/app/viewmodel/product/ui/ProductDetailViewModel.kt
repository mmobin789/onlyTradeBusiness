package onlytrade.app.viewmodel.product.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.Idle
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.MakeOfferFail
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.MakingOffer
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.OfferMade
import onlytrade.app.viewmodel.product.ui.usecase.OfferUseCase

class ProductDetailViewModel(
    private val offerUseCase: OfferUseCase,
    private val loginRepository: LoginRepository
) : ViewModel() {

    var uiState: MutableStateFlow<ProductDetailUiState> = MutableStateFlow(Idle)
        private set

    private val user = loginRepository.user()

    fun idle() {
        uiState.value = Idle
    }

    fun makeOffer(productId: Long, offerReceiverId: Long, offeredProductIds: HashSet<Long>) {
        uiState.value = MakingOffer
        viewModelScope.launch {
            uiState.value = when (offerUseCase(
                offerReceiverId = offerReceiverId,
                offerReceiverProductId = productId,
                offeredProductIds = offeredProductIds,
            )) {
                is OfferUseCase.Result.Error -> MakeOfferFail
                OfferUseCase.Result.OfferMade -> OfferMade
            }
        }
    }

    fun isUserLoggedIn() = loginRepository.isUserLoggedIn()

    fun gotAnOffer(offerReceiverId: Long) = user?.id == offerReceiverId

    fun hasMyOffer(offerMakerId: Long) = user?.id == offerMakerId

    /**
     * Each product has it's user's id.
     * If product's user id is same as logged in user's id,then it's logged in user's product.
     */
    fun isMyProduct(productUserId: Long) = user?.id == productUserId

}