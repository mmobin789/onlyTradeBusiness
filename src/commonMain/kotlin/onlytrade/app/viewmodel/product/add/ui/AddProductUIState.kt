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
    data class LessImagesSelected(val difference: Int) : AddProductUIState()
    data class MoreImagesSelected(val difference: Int) : AddProductUIState()
    data class AddProductFailed(val error: String? = null) : AddProductUIState()
    data class ProductInReview(val data: String) : AddProductUIState()
}