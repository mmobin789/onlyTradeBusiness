package onlytrade.app.viewmodel.trades.ui

import kotlinx.coroutines.flow.MutableSharedFlow

object MyTradesNav {
    var events = MutableSharedFlow<Event>()
        private set

    suspend fun emit(event: Event) = events.emit(event)


    sealed class Event {
        data object RefreshMyTrades : Event()
    }
}