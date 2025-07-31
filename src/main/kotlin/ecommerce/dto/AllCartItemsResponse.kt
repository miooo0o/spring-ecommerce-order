package ecommerce.dto

class AllCartItemsResponse(
    val cartId: Long,
    val items: List<CartItemResponse> = emptyList(),
)
