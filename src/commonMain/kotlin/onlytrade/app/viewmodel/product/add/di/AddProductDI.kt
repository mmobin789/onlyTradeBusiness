package onlytrade.app.viewmodel.product.add.di

import onlytrade.app.viewmodel.product.add.ui.AddProductViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object AddProductDI {
    val module = module {
        viewModelOf(::AddProductViewModel)
    }
}