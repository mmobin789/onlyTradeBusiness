package onlytrade.app.viewmodel.home

import androidx.lifecycle.ViewModel
import onlytrade.app.viewmodel.home.usecase.GetProductsUseCase

class HomeViewModel(private val getProductsUseCase: GetProductsUseCase) : ViewModel() {

    fun getProducts(pageNo: Int) {

    }

}