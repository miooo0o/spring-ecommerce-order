package ecommerce.dto

class CartItemResponse(
    val productId: Long,
    val quantity: Int,
    val productName: String,
    val productPrice: Long,
    val productImageUrl: String,
)
