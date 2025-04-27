package onlytrade.app.viewmodel.product.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.Idle
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.LoadingDetail
import onlytrade.app.viewmodel.product.ui.state.ProductDetailUiState.ProductFound
import onlytrade.app.viewmodel.product.ui.usecase.GetProductDetailUseCase

class ProductDetailViewModel(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val loginRepository: LoginRepository
) :
    ViewModel() {


    var uiState: MutableStateFlow<ProductDetailUiState> = MutableStateFlow(Idle)
        private set

    //todo call this.
    fun getProductDetail(productId: Long) {
        uiState.value = LoadingDetail
        viewModelScope.launch {
            val product = getProductDetailUseCase(productId)
            uiState.value = ProductFound(product)
        }
    }

    /**
     * Each product has it's user's id.
     * If product's user id is same as logged in user's id,then it's logged in user's product.
     */
    fun isMyProduct(userId: Long) = loginRepository.user()?.id == userId

}