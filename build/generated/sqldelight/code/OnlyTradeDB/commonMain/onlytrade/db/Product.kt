package onlytrade.db

public data class Product(
    public val id: Long,
    public val subcategoryId: Long,
    public val name: String,
    public val userId: Long,
    public val description: String,
    public val imageUrls: String,
    public val estPrice: Double,
)
