package onlytrade.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.admin.ui.VerifyProductUiState
import onlytrade.app.viewmodel.admin.ui.VerifyProductUiState.Idle
import onlytrade.app.viewmodel.admin.ui.VerifyProductUiState.ProductNotFound
import onlytrade.app.viewmodel.admin.ui.VerifyProductUiState.ProductVerified
import onlytrade.app.viewmodel.admin.ui.VerifyProductUiState.VerifyProductApiError
import onlytrade.app.viewmodel.admin.ui.VerifyProductUiState.VerifyingProduct
import onlytrade.app.viewmodel.admin.usecase.VerifyProductUseCase

class VerifyProductViewModel(
    private val verifyProductUseCase: VerifyProductUseCase
) : ViewModel() {
    var uiState: MutableStateFlow<VerifyProductUiState> = MutableStateFlow(Idle)
        private set

    fun idle() {
        uiState.value = Idle
    }

    fun verifyProduct(productId: Long) {
        uiState.value = VerifyingProduct

        viewModelScope.launch {
            uiState.value = when (val result = verifyProductUseCase(productId)) {
                VerifyProductUseCase.Result.ProductApproved -> ProductVerified.also {
                    AdminNav.emit(AdminNav.Event.RefreshAdminScreen)
                }

                VerifyProductUseCase.Result.ProductNotFound -> ProductNotFound
                is VerifyProductUseCase.Result.Error -> VerifyProductApiError(error = result.error)
            }
        }
    }

}