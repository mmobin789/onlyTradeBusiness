package onlytrade.app.viewmodel.product.ui.usecase

import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.repository.ProductRepository

class GetProductDetailUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(productId: Long) = withContext(IODispatcher) {
        /**
         * This call will always succeed because product detail can't happen or accessed without product.
         * so result will always be returned.
         */
        productRepository.getProduct(productId = productId)
    }
}