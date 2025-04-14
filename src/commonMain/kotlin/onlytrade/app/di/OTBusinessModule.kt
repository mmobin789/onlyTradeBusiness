package onlytrade.app.di

import com.russhwolf.settings.Settings
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import onlytrade.app.viewmodel.home.di.HomeDI
import onlytrade.app.viewmodel.login.di.LoginDI
import onlytrade.app.viewmodel.product.add.di.AddProductDI
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

object OTBusinessModule {
    private var diInit = false
    private val commonModule = module {
        single {
            HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = 120000  // 120 seconds (increase as needed)
                    connectTimeoutMillis = 60000   // 60 seconds
                    socketTimeoutMillis = 120000   // 120 seconds
                }
                install(ContentNegotiation) {
                    json()
                }
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Napier.d(message = message)
                        }

                    }
                    level = LogLevel.ALL
                }.also {
                    Napier.base(DebugAntilog())
                }
            }
        }
        single {
            Settings() //key-value local storage for kmp based on minimal and default platform impl.
        }
    }

    fun run(platformInit: KoinApplication.() -> Unit) {

        if (diInit)
            return

        startKoin {
            platformInit()
            modules(commonModule, LoginDI.module, HomeDI.module, AddProductDI.module)
            diInit = true
        }
    }
}