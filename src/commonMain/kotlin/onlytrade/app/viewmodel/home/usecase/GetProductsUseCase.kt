package onlytrade.app.viewmodel.home.usecase

import onlytrade.app.viewmodel.product.repository.ProductRepository

class GetProductsUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke() {

    }
}