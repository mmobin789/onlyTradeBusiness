package onlytrade.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver

public class ProductQueries(
    driver: SqlDriver,
) : TransacterImpl(driver) {
    public fun <T : Any> selectPaged(
        `value`: Long,
        value_: Long,
        mapper: (
            id: Long,
            subcategoryId: Long,
            name: String,
            userId: Long,
            description: String,
            imageUrls: String,
            estPrice: Double,
        ) -> T,
    ): Query<T> = SelectPagedQuery(value, value_) { cursor ->
        mapper(
            cursor.getLong(0)!!,
            cursor.getLong(1)!!,
            cursor.getString(2)!!,
            cursor.getLong(3)!!,
            cursor.getString(4)!!,
            cursor.getString(5)!!,
            cursor.getDouble(6)!!
        )
    }

    public fun selectPaged(value_: Long, value__: Long): Query<Product> = selectPaged(
        value_,
        value__
    ) { id, subcategoryId, name, userId, description, imageUrls, estPrice ->
        Product(
            id,
            subcategoryId,
            name,
            userId,
            description,
            imageUrls,
            estPrice
        )
    }

    public fun <T : Any> selectUsersPaged(
        userId: Long,
        `value`: Long,
        value_: Long,
        mapper: (
            id: Long,
            subcategoryId: Long,
            name: String,
            userId: Long,
            description: String,
            imageUrls: String,
            estPrice: Double,
        ) -> T,
    ): Query<T> = SelectUsersPagedQuery(userId, value, value_) { cursor ->
        mapper(
            cursor.getLong(0)!!,
            cursor.getLong(1)!!,
            cursor.getString(2)!!,
            cursor.getLong(3)!!,
            cursor.getString(4)!!,
            cursor.getString(5)!!,
            cursor.getDouble(6)!!
        )
    }

    public fun selectUsersPaged(
        userId: Long,
        value_: Long,
        value__: Long,
    ): Query<Product> =
        selectUsersPaged(userId, value_, value__) { id, subcategoryId, name, userId_,
                                                    description, imageUrls, estPrice ->
            Product(
                id,
                subcategoryId,
                name,
                userId_,
                description,
                imageUrls,
                estPrice
            )
        }

    public fun insert(
        id: Long?,
        subcategoryId: Long,
        name: String,
        userId: Long,
        description: String,
        imageUrls: String,
        estPrice: Double,
    ) {
        driver.execute(
            1_482_150_896, """
        |INSERT INTO Product(id,subcategoryId, name, userId, description, imageUrls, estPrice)
        |VALUES (?, ?,?, ?, ?, ?, ?)
        """.trimMargin(), 7
        ) {
            bindLong(0, id)
            bindLong(1, subcategoryId)
            bindString(2, name)
            bindLong(3, userId)
            bindString(4, description)
            bindString(5, imageUrls)
            bindDouble(6, estPrice)
        }
        notifyQueries(1_482_150_896) { emit ->
            emit("Product")
        }
    }

    private inner class SelectPagedQuery<out T : Any>(
        public val `value`: Long,
        public val value_: Long,
        mapper: (SqlCursor) -> T,
    ) : Query<T>(mapper) {
        override fun addListener(listener: Query.Listener) {
            driver.addListener("Product", listener = listener)
        }

        override fun removeListener(listener: Query.Listener) {
            driver.removeListener("Product", listener = listener)
        }

        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
            driver.executeQuery(
                95_527_234, """
    |SELECT Product.id, Product.subcategoryId, Product.name, Product.userId, Product.description, Product.imageUrls, Product.estPrice FROM Product
    |ORDER BY id
    |LIMIT ? OFFSET ?
    """.trimMargin(), mapper, 2
            ) {
                bindLong(0, value)
                bindLong(1, value_)
            }

        override fun toString(): String = "Product.sq:selectPaged"
    }

    private inner class SelectUsersPagedQuery<out T : Any>(
        public val userId: Long,
        public val `value`: Long,
        public val value_: Long,
        mapper: (SqlCursor) -> T,
    ) : Query<T>(mapper) {
        override fun addListener(listener: Query.Listener) {
            driver.addListener("Product", listener = listener)
        }

        override fun removeListener(listener: Query.Listener) {
            driver.removeListener("Product", listener = listener)
        }

        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
            driver.executeQuery(
                1_042_691_360, """
    |SELECT Product.id, Product.subcategoryId, Product.name, Product.userId, Product.description, Product.imageUrls, Product.estPrice FROM Product
    |WHERE userId = ?
    |ORDER BY id
    |LIMIT ? OFFSET ?
    """.trimMargin(), mapper, 3
            ) {
                bindLong(0, userId)
                bindLong(1, value)
                bindLong(2, value_)
            }

        override fun toString(): String = "Product.sq:selectUsersPaged"
    }
}
