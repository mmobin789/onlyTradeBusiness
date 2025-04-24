package onlytrade.app.viewmodel.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.home.ui.HomeUiState.GetProductsApiError
import onlytrade.app.viewmodel.home.ui.HomeUiState.Idle
import onlytrade.app.viewmodel.home.ui.HomeUiState.LoadingProducts
import onlytrade.app.viewmodel.home.ui.HomeUiState.ProductsNotFound
import onlytrade.app.viewmodel.home.ui.usecase.GetProductsUseCase
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.repository.data.db.Product


class HomeViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    loginRepository: LoginRepository
) : ViewModel() {

    var uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(Idle)
        private set

    var productList: MutableStateFlow<List<Product>> = MutableStateFlow(emptyList())
        private set

    private val loadedPages = hashSetOf<Int>()

    private var productsNotFound = false

    val productPageSizeExpected = 10

    private var productsPageNo = 1

    val isUserLoggedIn = loginRepository.isUserLoggedIn()

    /**
     * This would only reset the non-paginated UI state flow.
     * namely uiState not ProductsList.
     */
    fun idle() {
        uiState.value = Idle
    }


    fun getProducts(tryAgain: Boolean = false) {


        if (tryAgain) {
            productsNotFound = false
            removeLoadedPage()
        }

        /**
         * This checks if the product page requested is already loaded on ui or if products not found.
         */
        if (loadedPages.add(productsPageNo).not() || productsNotFound) {
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
                    productsNotFound = false

                    val productPage = result.products

                    if (productPage.size == productPageSizeExpected)
                        productsPageNo++

                    productList.value += productPage


                    idle()

                }


                GetProductsUseCase.Result.ProductsNotFound -> {
                    productsNotFound = true
                    removeLoadedPage()
                    uiState.value = ProductsNotFound
                }

                is GetProductsUseCase.Result.Error -> {
                    productsNotFound = true
                    removeLoadedPage()
                    uiState.value = GetProductsApiError(error = result.error)
                }

            }
        }
    }

    private fun removeLoadedPage() = loadedPages.remove(productsPageNo)
}