package onlytrade.app.viewmodel.home.ui

import kotlinx.coroutines.flow.MutableSharedFlow

object HomeNav {
    var events = MutableSharedFlow<Event>()
        private set

    suspend fun emit(event: Event) = events.emit(event)


    sealed class Event {
        data object RefreshHome : Event()
    }
}