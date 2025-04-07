package onlytrade.app

import kotlinx.coroutines.CoroutineDispatcher

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect val IODispatcher: CoroutineDispatcher