package ecommerce.dto

class CartItemResponse(
    var quantity: Int,
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val productImageUrl: String,
)
