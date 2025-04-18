package onlytrade.app.viewmodel.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.home.ui.HomeUiState.GetProductsApiError
import onlytrade.app.viewmodel.home.ui.HomeUiState.Idle
import onlytrade.app.viewmodel.home.ui.HomeUiState.LoadingProducts
import onlytrade.app.viewmodel.home.ui.HomeUiState.ProductList
import onlytrade.app.viewmodel.home.ui.HomeUiState.ProductsNotFound
import onlytrade.app.viewmodel.home.usecase.GetProductsUseCase
import onlytrade.app.viewmodel.login.repository.LoginRepository


class HomeViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    loginRepository: LoginRepository
) : ViewModel() {

    val productPageSizeExpected = 20

    private var productPageSizeActual = 0
        private set

    val expectedPageLoaded =
        productPageSizeExpected == productPageSizeActual // this means only 1 page exists.

    var productsPageNo = 1
        private set
    private var allProductsLoaded = false
    var uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(Idle)
        private set

    val isUserLoggedIn = loginRepository.isUserLoggedIn()

    init {
        getProducts()
    }

    fun idle() {
        uiState.value = Idle
    }


    fun getProducts() {
        uiState.value = LoadingProducts
        viewModelScope.launch {
            uiState.value = when (val result =
                getProductsUseCase(pageNo = productsPageNo, pageSize = productPageSizeExpected)) {
                is GetProductsUseCase.Result.GetProducts -> {
                    ProductList(result.products.apply {
                        if (size == productPageSizeExpected && allProductsLoaded.not())
                            productsPageNo++

                        productPageSizeActual = size
                    })
                }

                GetProductsUseCase.Result.ProductsNotFound -> ProductsNotFound.apply {
                    allProductsLoaded = true
                }

                is GetProductsUseCase.Result.Error -> {
                    GetProductsApiError(error = result.error)
                }

            }
        }
    }

}