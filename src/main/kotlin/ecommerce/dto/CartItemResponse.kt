package ecommerce.dto

class CartItemResponse(
    var quantity: Long,
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val productImageUrl: String,
)
