package ecommerce.model

import ecommerce.dto.CartItemResponse

data class CartItem(
    val product: Product,
    val quantity: Int,
) {
    fun toResponse(): CartItemResponse {
        return CartItemResponse(
            quantity = quantity.toLong(),
            productId = product.id ?: 0,
            productName = product.name,
            productPrice = product.price,
            productImageUrl = product.imageUrl,
        )
    }
}
