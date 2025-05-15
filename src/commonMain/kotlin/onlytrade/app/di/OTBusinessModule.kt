package onlytrade.app.di

import DatabaseDriverFactory
import com.russhwolf.settings.Settings
import createDatabase
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import onlytrade.app.AppConfig
import onlytrade.app.viewmodel.home.di.homeModule
import onlytrade.app.viewmodel.login.di.loginModule
import onlytrade.app.viewmodel.product.di.addProductModule
import onlytrade.app.viewmodel.product.di.myProductsModule
import onlytrade.app.viewmodel.product.di.productDetailModule
import onlytrade.app.viewmodel.profile.di.profileModule
import onlytrade.app.viewmodel.splash.di.splashModule
import onlytrade.app.viewmodel.trades.di.myTradesModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

object OTBusinessModule {
    private var diInit = false


    fun run(
        platformInit: KoinApplication.() -> Unit,
        databaseDriverFactory: DatabaseDriverFactory
    ) {

        if (diInit)
            return

        val commonModule = module {

            single {
                AppConfig
            }
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
            single { createDatabase(databaseDriverFactory) }
        }

        startKoin {
            platformInit()
            modules(
                commonModule,
                splashModule,
                loginModule,
                homeModule,
                addProductModule,
                myProductsModule,
                productDetailModule,
                myTradesModule,
                profileModule
            )
            diInit = true
        }
    }
}