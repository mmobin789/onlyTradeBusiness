package onlytrade.app.viewmodel.product.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.product.ui.AddProductUIState.AddProductFailed
import onlytrade.app.viewmodel.product.ui.AddProductUIState.CategoryNotSelected
import onlytrade.app.viewmodel.product.ui.AddProductUIState.DescriptionBlank
import onlytrade.app.viewmodel.product.ui.AddProductUIState.EstPriceBlank
import onlytrade.app.viewmodel.product.ui.AddProductUIState.EstPriceLow
import onlytrade.app.viewmodel.product.ui.AddProductUIState.Idle
import onlytrade.app.viewmodel.product.ui.AddProductUIState.ImagesNotSelected
import onlytrade.app.viewmodel.product.ui.AddProductUIState.LessImagesSelected
import onlytrade.app.viewmodel.product.ui.AddProductUIState.Loading
import onlytrade.app.viewmodel.product.ui.AddProductUIState.MoreImagesSelected
import onlytrade.app.viewmodel.product.ui.AddProductUIState.ProductInReview
import onlytrade.app.viewmodel.product.ui.AddProductUIState.SubcategoryNotSelected
import onlytrade.app.viewmodel.product.ui.AddProductUIState.TitleBlank
import onlytrade.app.viewmodel.product.usecase.AddProductUseCase
import onlytrade.app.viewmodel.product.usecase.AddProductUseCase.Result

class AddProductViewModel(private val addProductUseCase: AddProductUseCase) : ViewModel() {
    var uiState: MutableStateFlow<AddProductUIState> = MutableStateFlow(Idle)
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

        if (images.size < 4) {
            uiState.value = LessImagesSelected
            return
        }

        if (images.size > 9) {
            uiState.value = MoreImagesSelected
            return
        }

        loading()

        viewModelScope.launch {
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