package onlytrade.app.viewmodel.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.home.ui.HomeUiState.GetProductsApiError
import onlytrade.app.viewmodel.home.ui.HomeUiState.Idle
import onlytrade.app.viewmodel.home.ui.HomeUiState.LoadingProducts
import onlytrade.app.viewmodel.home.ui.HomeUiState.ProductsNotFound
import onlytrade.app.viewmodel.home.usecase.GetProductsUseCase
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.repository.data.db.Product


class HomeViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    loginRepository: LoginRepository
) : ViewModel() {

    private val loadedPages = hashSetOf<Int>()

    val productPageSizeExpected = 10

    var productsPageNo = 1
        private set

    //val products = mutableListOf<Product>() // scaling list of products.

    var uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(Idle)
        private set


    var productList: MutableStateFlow<List<Product>> = MutableStateFlow(emptyList())
        private set

    val isUserLoggedIn = loginRepository.isUserLoggedIn()

    fun idle() {
        uiState.value = Idle
    }


    fun getProducts() {
        /**
         * This checks if the product page requested is already loaded on ui.
         */
        if (loadedPages.add(productsPageNo).not()) {
            return
        }

        uiState.value = LoadingProducts

        viewModelScope.launch {
            when (val result =
                getProductsUseCase(
                    pageNo = productsPageNo,
                    pageSize = productPageSizeExpected
                )) {
                is GetProductsUseCase.Result.GetProducts -> {

                    val productPage = result.products

                    if (productPage.size == productPageSizeExpected)
                        productsPageNo++

                    productList.value += productPage


                    idle()

                }


                GetProductsUseCase.Result.ProductsNotFound -> {
                    loadedPages.remove(productsPageNo)
                    uiState.value = ProductsNotFound
                }

                is GetProductsUseCase.Result.Error -> {
                    loadedPages.remove(productsPageNo)
                    uiState.value = GetProductsApiError(error = result.error)
                }

            }
        }
    }

}