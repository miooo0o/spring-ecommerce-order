package ecommerce.dto

class ProductResponse(
    val productId: Long,
    val price: Double,
    val imageUrl: String,
    val optionNames: List<String>,
    val upsertStatus: UpsertStatus,
)

enum class UpsertStatus {
    CREATED,
    UPDATED,
}
