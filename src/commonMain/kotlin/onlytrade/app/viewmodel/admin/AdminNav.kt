package onlytrade.app.viewmodel.admin

import kotlinx.coroutines.flow.MutableSharedFlow

object AdminNav {
    var events = MutableSharedFlow<Event>()
        private set

    suspend fun emit(event: Event) = events.emit(event)


    sealed class Event {
        data object RefreshAdminScreen : Event()
    }
}