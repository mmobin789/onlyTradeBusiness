package onlytrade.app

import kotlinx.coroutines.CoroutineDispatcher

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()
actual val IODispatcher: CoroutineDispatcher
    get() = TODO("Not yet implemented")