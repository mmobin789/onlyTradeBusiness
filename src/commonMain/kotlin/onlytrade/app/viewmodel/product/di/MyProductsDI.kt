package onlytrade.app.viewmodel.product.di

import onlytrade.app.viewmodel.product.ui.MyProductsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val myProductsModule = module {
    viewModelOf(::MyProductsViewModel)
}
