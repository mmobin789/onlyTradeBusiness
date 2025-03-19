package onlytrade.app.viewmodel.product.add.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import onlytrade.app.viewmodel.product.add.ui.AddProductUIState.CategoryNotSelected
import onlytrade.app.viewmodel.product.add.ui.AddProductUIState.DescriptionBlank
import onlytrade.app.viewmodel.product.add.ui.AddProductUIState.EstPriceBlank
import onlytrade.app.viewmodel.product.add.ui.AddProductUIState.EstPriceLow
import onlytrade.app.viewmodel.product.add.ui.AddProductUIState.Idle
import onlytrade.app.viewmodel.product.add.ui.AddProductUIState.ImagesNotSelected
import onlytrade.app.viewmodel.product.add.ui.AddProductUIState.LessImagesSelected
import onlytrade.app.viewmodel.product.add.ui.AddProductUIState.Loading
import onlytrade.app.viewmodel.product.add.ui.AddProductUIState.MoreImagesSelected
import onlytrade.app.viewmodel.product.add.ui.AddProductUIState.TitleBlank
import onlytrade.app.viewmodel.product.usecase.AddProductUseCase

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
        categoryId: Int,
        subcategoryId: Int,
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

        if (description.isBlank()) {
            uiState.value = DescriptionBlank
            return
        }

        if (estPrice.isBlank()) {
            uiState.value = EstPriceBlank
            return
        }

        if (estPrice.toDouble() < 500) {
            uiState.value = EstPriceLow
            return
        }

        if (images.isEmpty()) {
            uiState.value = ImagesNotSelected
            return
        }

        if (images.size < 3) {
            uiState.value = LessImagesSelected(difference = 9 - images.size)
        }

        if (images.size > 9) {
            uiState.value = MoreImagesSelected(difference = images.size - 9)
        }

    }

}