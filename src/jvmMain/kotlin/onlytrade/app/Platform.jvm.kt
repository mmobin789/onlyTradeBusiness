package onlytrade.app

import kotlinx.coroutines.CoroutineDispatcher

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()
actual val IODispatcher: CoroutineDispatcher
    get() = TODO("Not yet implemented")