import app.cash.sqldelight.db.SqlDriver
import onlytrade.db.OnlyTradeDB


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DatabaseDriverFactory): OnlyTradeDB {
    val driver = driverFactory.createDriver()
    val database = OnlyTradeDB(driver)
    return database
}