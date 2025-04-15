package onlytrade.app.viewmodel.product.add.ui

sealed class AddProductUIState {
    data object Idle : AddProductUIState()
    data object Loading : AddProductUIState()
    data object TitleBlank : AddProductUIState()
    data object CategoryNotSelected : AddProductUIState()
    data object SubcategoryNotSelected : AddProductUIState()
    data object DescriptionBlank : AddProductUIState()
    data object EstPriceBlank : AddProductUIState()
    data object EstPriceLow : AddProductUIState()
    data object ImagesNotSelected : AddProductUIState()
    data object LessImagesSelected : AddProductUIState()
    data object MoreImagesSelected : AddProductUIState()
    data class AddProductFailed(val error: String) : AddProductUIState()
    data object ProductInReview : AddProductUIState()
}