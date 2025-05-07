package onlytrade.app

/**
 * To switch between server environments use this.
 */
object AppConfig {
    private const val DEV_MODE = true
    val baseUrl =
        if (DEV_MODE) "https://onlytrade-dev-9c057a85ddfa.herokuapp.com" else "https://onlytrade.co"
}