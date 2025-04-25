package onlytrade.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver

public class ProductQueries(
    driver: SqlDriver,
) : TransacterImpl(driver) {
    public fun <T : Any> getById(
        id: Long, mapper: (
            id: Long,
            categoryId: Long,
            subcategoryId: Long,
            name: String,
            userId: Long,
            description: String,
            imageUrls: String,
            estPrice: Double,
        ) -> T
    ): Query<T> = GetByIdQuery(id) { cursor ->
        mapper(
            cursor.getLong(0)!!,
            cursor.getLong(1)!!,
            cursor.getLong(2)!!,
            cursor.getString(3)!!,
            cursor.getLong(4)!!,
            cursor.getString(5)!!,
            cursor.getString(6)!!,
            cursor.getDouble(7)!!
        )
    }

    public fun getById(id: Long): Query<Product> =
        getById(id) { id_, categoryId, subcategoryId, name,
                      userId, description, imageUrls, estPrice ->
            Product(
                id_,
                categoryId,
                subcategoryId,
                name,
                userId,
                description,
                imageUrls,
                estPrice
            )
        }

    public fun <T : Any> selectPaged(
        `value`: Long,
        value_: Long,
        mapper: (
            id: Long,
            categoryId: Long,
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
            cursor.getLong(2)!!,
            cursor.getString(3)!!,
            cursor.getLong(4)!!,
            cursor.getString(5)!!,
            cursor.getString(6)!!,
            cursor.getDouble(7)!!
        )
    }

    public fun selectPaged(value_: Long, value__: Long): Query<Product> = selectPaged(
        value_,
        value__
    ) { id, categoryId, subcategoryId, name, userId, description, imageUrls, estPrice ->
        Product(
            id,
            categoryId,
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
            categoryId: Long,
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
            cursor.getLong(2)!!,
            cursor.getString(3)!!,
            cursor.getLong(4)!!,
            cursor.getString(5)!!,
            cursor.getString(6)!!,
            cursor.getDouble(7)!!
        )
    }

    public fun selectUsersPaged(
        userId: Long,
        value_: Long,
        value__: Long,
    ): Query<Product> = selectUsersPaged(userId, value_, value__) { id, categoryId, subcategoryId,
                                                                    name, userId_, description, imageUrls, estPrice ->
        Product(
            id,
            categoryId,
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
        categoryId: Long,
        subcategoryId: Long,
        name: String,
        userId: Long,
        description: String,
        imageUrls: String,
        estPrice: Double,
    ) {
        driver.execute(
            1_482_150_896, """
        |INSERT OR REPLACE INTO Product(id,categoryId,subcategoryId, name, userId, description, imageUrls, estPrice)
        |VALUES (?, ?,?,?, ?, ?, ?, ?)
        """.trimMargin(), 8
        ) {
            bindLong(0, id)
            bindLong(1, categoryId)
            bindLong(2, subcategoryId)
            bindString(3, name)
            bindLong(4, userId)
            bindString(5, description)
            bindString(6, imageUrls)
            bindDouble(7, estPrice)
        }
        notifyQueries(1_482_150_896) { emit ->
            emit("Product")
        }
    }

    public fun deleteAll() {
        driver.execute(-1_775_605_921, """DELETE FROM Product""", 0)
        notifyQueries(-1_775_605_921) { emit ->
            emit("Product")
        }
    }

    private inner class GetByIdQuery<out T : Any>(
        public val id: Long,
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
                964_221_425, """
    |SELECT Product.id, Product.categoryId, Product.subcategoryId, Product.name, Product.userId, Product.description, Product.imageUrls, Product.estPrice FROM Product
    |WHERE id = ?
    """.trimMargin(), mapper, 1
            ) {
                bindLong(0, id)
            }

        override fun toString(): String = "Product.sq:getById"
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
    |SELECT Product.id, Product.categoryId, Product.subcategoryId, Product.name, Product.userId, Product.description, Product.imageUrls, Product.estPrice FROM Product
    |ORDER BY id DESC
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
    |SELECT Product.id, Product.categoryId, Product.subcategoryId, Product.name, Product.userId, Product.description, Product.imageUrls, Product.estPrice FROM Product
    |WHERE userId = ?
    |ORDER BY id DESC
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
