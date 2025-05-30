package onlytrade.app.viewmodel.product.ui.nav

import kotlinx.coroutines.flow.MutableSharedFlow

object ProductDetailNav {
    var events = MutableSharedFlow<Event>()
        private set

    suspend fun emit(event: Event) = events.emit(event)


    sealed class Event {
        data class TradeProducts(val productIds: LinkedHashSet<Long>) : Event()
    }
}