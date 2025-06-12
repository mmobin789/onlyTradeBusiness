package onlytrade.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.admin.ui.AdminUiState
import onlytrade.app.viewmodel.admin.ui.AdminUiState.GetApprovalProductsApiError
import onlytrade.app.viewmodel.admin.ui.AdminUiState.GetApprovalUsersApiError
import onlytrade.app.viewmodel.admin.ui.AdminUiState.Idle
import onlytrade.app.viewmodel.admin.ui.AdminUiState.LoadingProducts
import onlytrade.app.viewmodel.admin.ui.AdminUiState.LoadingUsers
import onlytrade.app.viewmodel.admin.ui.AdminUiState.LoggedOut
import onlytrade.app.viewmodel.admin.ui.AdminUiState.ProductNotFound
import onlytrade.app.viewmodel.admin.ui.AdminUiState.ProductVerified
import onlytrade.app.viewmodel.admin.ui.AdminUiState.ProductsNotFound
import onlytrade.app.viewmodel.admin.ui.AdminUiState.UsersNotFound
import onlytrade.app.viewmodel.admin.ui.AdminUiState.VerifyProductApiError
import onlytrade.app.viewmodel.admin.ui.AdminUiState.VerifyingProduct
import onlytrade.app.viewmodel.admin.usecase.GetApprovalProductsUseCase
import onlytrade.app.viewmodel.admin.usecase.GetApprovalUsersUseCase
import onlytrade.app.viewmodel.admin.usecase.VerifyProductUseCase
import onlytrade.app.viewmodel.login.repository.data.db.User
import onlytrade.app.viewmodel.product.repository.data.db.Product
import onlytrade.app.viewmodel.profile.usecase.LogoutUseCase


class AdminViewModel(
    private val verifyProductsUseCase: VerifyProductUseCase,
    private val getApprovalProductsUseCase: GetApprovalProductsUseCase,
    private val getApprovalUsersUseCase: GetApprovalUsersUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    var uiState: MutableStateFlow<AdminUiState> = MutableStateFlow(Idle)
        private set

    var productsUiState: MutableStateFlow<List<Product>> = MutableStateFlow(emptyList())
        private set

    var usersUiState: MutableStateFlow<List<User>> = MutableStateFlow(emptyList())
        private set

    private var latestProductPage: List<Product>? = null
    private var latestUserPage: List<User>? = null

    private var refreshUsers = false

    init {
        viewModelScope.launch {
            AdminNav.events.collect { event ->
                refreshUsers = event == AdminNav.Event.RefreshAdminScreen
            }
        }
        getUsers()

    }


    /**
     * This would only reset the UI state flow.
     * namely uiState not ProductsList.
     */
    fun idle() {
        uiState.value = Idle
    }

    fun refreshUsers() {
        if (refreshUsers) {
            refreshUsers = false
            getUsers()
        }
    }

    fun getProducts() {
        uiState.value = LoadingProducts
        usersUiState.value = emptyList()

        viewModelScope.launch {
            when (val result = getApprovalProductsUseCase()) {
                is GetApprovalProductsUseCase.Result.ApprovalProducts -> {
                    productsUiState.value = result.products.also {
                        latestProductPage = it
                        idle()
                    }
                }

                GetApprovalProductsUseCase.Result.ProductsNotFound -> {
                    uiState.value = ProductsNotFound
                }

                is GetApprovalProductsUseCase.Result.Error -> {
                    uiState.value = GetApprovalProductsApiError(error = result.error)
                }

            }
        }
    }

    fun getUsers() {
        uiState.value = LoadingUsers
        productsUiState.value = emptyList()

        viewModelScope.launch {
            when (val result = getApprovalUsersUseCase()) {
                is GetApprovalUsersUseCase.Result.ApprovalUsers -> {
                    usersUiState.value = result.users.also {
                        latestUserPage = it
                        idle()
                    }
                }

                GetApprovalUsersUseCase.Result.UsersNotFound -> {
                    uiState.value = UsersNotFound
                }

                is GetApprovalUsersUseCase.Result.Error -> {
                    uiState.value = GetApprovalUsersApiError(error = result.error)
                }

            }
        }
    }

    fun verifyProduct(productId: Long) {
        uiState.value = VerifyingProduct

        viewModelScope.launch {
            uiState.value = when (val result = verifyProductsUseCase(productId)) {
                VerifyProductUseCase.Result.ProductApproved -> ProductVerified
                VerifyProductUseCase.Result.ProductNotFound -> ProductNotFound
                is VerifyProductUseCase.Result.Error -> VerifyProductApiError(error = result.error)
            }
        }
    }


    fun logOut() {
        viewModelScope.launch {
            uiState.value = LoggedOut
            logoutUseCase()

        }
    }

}