package ecommerce.dto

class CartResponse(
    val cartId: Long,
    val items: List<CartItemResponse> = emptyList(),
)
