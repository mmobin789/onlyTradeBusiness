package onlytrade.app.viewmodel.product.ui.usecase

import onlytrade.app.viewmodel.product.repository.ProductRepository

class GetProductDetailUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(productId: Int) {
        productRepository.g
    }
}