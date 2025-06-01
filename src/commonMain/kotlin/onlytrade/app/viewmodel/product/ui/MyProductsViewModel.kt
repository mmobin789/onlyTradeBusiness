package onlytrade.app.viewmodel.product.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.component.AppScope
import onlytrade.app.viewmodel.home.ui.usecase.GetProductsUseCase
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.repository.data.db.Product
import onlytrade.app.viewmodel.product.ui.state.MyProductsUiState
import onlytrade.app.viewmodel.product.ui.state.MyProductsUiState.AddOfferApiError
import onlytrade.app.viewmodel.product.ui.state.MyProductsUiState.GetProductsApiError
import onlytrade.app.viewmodel.product.ui.state.MyProductsUiState.Idle
import onlytrade.app.viewmodel.product.ui.state.MyProductsUiState.LoadingProducts
import onlytrade.app.viewmodel.product.ui.state.MyProductsUiState.MakingOffer
import onlytrade.app.viewmodel.product.ui.state.MyProductsUiState.OfferMade
import onlytrade.app.viewmodel.product.ui.state.MyProductsUiState.OffersExceeded
import onlytrade.app.viewmodel.product.ui.state.MyProductsUiState.ProductsNotFound
import onlytrade.app.viewmodel.product.ui.state.MyProductsUiState.SelectionActive
import onlytrade.app.viewmodel.product.ui.usecase.OfferUseCase

class MyProductsViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val offerUseCase: OfferUseCase,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val pickedProductIds = linkedSetOf<Long>()

    var uiState: MutableStateFlow<MyProductsUiState> = MutableStateFlow(Idle)
        private set


    var productList: MutableStateFlow<List<Product>> = MutableStateFlow(emptyList())
        private set

    private val loadedPages = hashSetOf<Int>()

    private var productsNotFound = false

    val productPageSizeExpected = 10

    private var productsPageNo = 1

    /**
     * This would only reset the non-paginated UI state flow.
     * namely uiState not ProductsList.
     */
    fun idle() {
        uiState.value = Idle
    }

    fun getProducts() {

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
                    pageSize = productPageSizeExpected,
                    userId = loginRepository.user()?.id
                )) {
                is GetProductsUseCase.Result.ProductPage -> {
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

    fun makeOffer(productId: Long, offerReceiverId: Long) {

        if (pickedProductIds.isEmpty())
            return

        uiState.value = MakingOffer

        AppScope.launch {

            uiState.value = when (val result = offerUseCase(
                offerReceiverId = offerReceiverId,
                offerReceiverProductId = productId,
                offeredProductIds = pickedProductIds,
            )) {
                OfferUseCase.Result.OffersExceeded -> {
                    OffersExceeded
                }

                is OfferUseCase.Result.Error -> {
                    AddOfferApiError(result.error)
                }

                is OfferUseCase.Result.OfferMade -> {
                    OfferMade(result.offer)
                }

            }
        }
    }

    private fun removeLoadedPage() = loadedPages.remove(productsPageNo)

    fun selectProduct(id: Long): Boolean {
        if (pickedProductIds.add(id).not()) {
            pickedProductIds.remove(id)

            if (pickedProductIds.isEmpty())
                uiState.value = Idle

            return false
        }
        uiState.value = SelectionActive
        return true
    }

}