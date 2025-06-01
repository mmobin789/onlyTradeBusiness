package onlytrade.app.viewmodel.product.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.component.AppScope
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.AddProductFailed
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.CategoryNotSelected
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.DescriptionBlank
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.EstPriceBlank
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.EstPriceLow
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.Idle
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.ImagesNotSelected
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.LessImagesSelected
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.Loading
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.MoreImagesSelected
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.ProductInReview
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.SubcategoryNotSelected
import onlytrade.app.viewmodel.product.ui.state.AddProductUiState.TitleBlank
import onlytrade.app.viewmodel.product.ui.usecase.AddProductUseCase
import onlytrade.app.viewmodel.product.ui.usecase.AddProductUseCase.Result

class AddProductViewModel(private val addProductUseCase: AddProductUseCase) : ViewModel() {
    var uiState: MutableStateFlow<AddProductUiState> = MutableStateFlow(Idle)
        private set

    fun idle() {
        uiState.value = Idle
    }

    private fun loading() {
        uiState.value = Loading
    }


    fun addProduct(
        title: String,
        categoryId: Long,
        subcategoryId: Long,
        description: String,
        estPrice: String,
        images: List<ByteArray>
    ) {
        if (title.isBlank()) {
            uiState.value = TitleBlank
            return
        }

        if (categoryId < 0) {
            uiState.value = CategoryNotSelected
            return
        }

        if (subcategoryId < 0) {
            uiState.value = SubcategoryNotSelected
            return
        }


        if (description.isBlank()) {
            uiState.value = DescriptionBlank
            return
        }

        if (estPrice.isBlank()) {
            uiState.value = EstPriceBlank
            return
        }

        val estPriceD = estPrice.toDouble()

        if (estPriceD < 500) {
            uiState.value = EstPriceLow
            return
        }

        if (images.isEmpty()) {
            uiState.value = ImagesNotSelected
            return
        }

        if (images.size < 3) {
            uiState.value = LessImagesSelected
            return
        }

        if (images.size > 6) {
            uiState.value = MoreImagesSelected
            return
        }

        loading()

        AppScope.launch {
            uiState.value = when (val result = addProductUseCase(
                name = title,
                description = description,
                categoryId = categoryId,
                subcategoryId = subcategoryId,
                estPrice = estPriceD,
                productImages = images
            )) {
                Result.OK -> ProductInReview
                is Result.Error -> AddProductFailed(error = result.error)
            }
        }
    }

}