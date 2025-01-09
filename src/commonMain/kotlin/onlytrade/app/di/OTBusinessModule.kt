package onlytrade.app.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import onlytrade.app.viewmodel.login.di.LoginDI
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

object OTBusinessModule {
    private var diInit = false
    private val commonModule = module {
        single {
            HttpClient {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Napier.v("HTTP Client", null, message)
                        }

                    }
                    level = LogLevel.ALL
                }.also {
                    Napier.base(DebugAntilog())
                }
            }
        }
    }

    fun run(platformInit: KoinApplication.() -> Unit) {

        if (diInit)
            return

        startKoin {
            platformInit()
            modules(commonModule, LoginDI.module)
            diInit = true
        }
    }
}