package onlytrade.app

import kotlinx.coroutines.CoroutineDispatcher
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()
actual val IODispatcher: CoroutineDispatcher
    get() = TODO("Not yet implemented")