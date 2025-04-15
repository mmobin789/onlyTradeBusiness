package onlytrade.app.viewmodel.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.home.ui.HomeUiState.GetProductsApiError
import onlytrade.app.viewmodel.home.ui.HomeUiState.Idle
import onlytrade.app.viewmodel.home.ui.HomeUiState.LoadingProducts
import onlytrade.app.viewmodel.home.ui.HomeUiState.ProductList
import onlytrade.app.viewmodel.home.usecase.GetProductsUseCase


class HomeViewModel(private val getProductsUseCase: GetProductsUseCase) : ViewModel() {
    private var productsPageNo = 1
    var uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(Idle)
        private set

    fun getProducts() {
        uiState.value = LoadingProducts
        viewModelScope.launch {
            uiState.value = when (val result = getProductsUseCase(pageNo = productsPageNo)) {
                is GetProductsUseCase.Result.GetProducts -> {
                    ProductList(result.products.apply {
                        if (isEmpty().not())
                            productsPageNo++
                    })
                }

                is GetProductsUseCase.Result.Error -> {
                    GetProductsApiError(error = result.error)
                }

            }
        }
    }

}