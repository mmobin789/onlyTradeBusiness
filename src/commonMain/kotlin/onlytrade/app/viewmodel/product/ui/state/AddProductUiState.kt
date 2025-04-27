package onlytrade.app.viewmodel.product.ui.state

sealed class AddProductUiState {
    data object Idle : AddProductUiState()
    data object Loading : AddProductUiState()
    data object TitleBlank : AddProductUiState()
    data object CategoryNotSelected : AddProductUiState()
    data object SubcategoryNotSelected : AddProductUiState()
    data object DescriptionBlank : AddProductUiState()
    data object EstPriceBlank : AddProductUiState()
    data object EstPriceLow : AddProductUiState()
    data object ImagesNotSelected : AddProductUiState()
    data object LessImagesSelected : AddProductUiState()
    data object MoreImagesSelected : AddProductUiState()
    data class AddProductFailed(val error: String) : AddProductUiState()
    data object ProductInReview : AddProductUiState()
}