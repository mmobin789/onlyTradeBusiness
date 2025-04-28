package onlytrade.app.viewmodel.product.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.Idle
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.LoadingDetail
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.MakeOfferFail
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.MakingOffer
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.OfferMade
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.ProductFound
import onlytrade.app.viewmodel.product.ui.usecase.GetProductDetailUseCase
import onlytrade.app.viewmodel.product.ui.usecase.OfferUseCase

class ProductDetailViewModel(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val offerUseCase: OfferUseCase,
    private val loginRepository: LoginRepository
) : ViewModel() {

    var uiState: MutableStateFlow<ProductDetailUiState> = MutableStateFlow(Idle)
        private set

    private var productUserId = 0L

    fun makeOffer(offerReceiverId: Long, productIds: List<Long>) {
        uiState.value = MakingOffer
        viewModelScope.launch {
            uiState.value = when (offerUseCase(offerReceiverId, productIds)) {
                is OfferUseCase.Result.Error -> MakeOfferFail
                OfferUseCase.Result.OfferMade -> OfferMade
            }
        }
    }

    fun getProductDetail(productId: Long) {
        uiState.value = LoadingDetail
        viewModelScope.launch {
            val product = getProductDetailUseCase(productId)
            productUserId = product.userId
            uiState.value = ProductFound(product)
        }
    }

    fun isUserLoggedIn() = loginRepository.isUserLoggedIn()

    /**
     * Each product has it's user's id.
     * If product's user id is same as logged in user's id,then it's logged in user's product.
     */
    fun isMyProduct() = loginRepository.user()?.id == productUserId

}