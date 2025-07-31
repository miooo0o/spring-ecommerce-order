package ecommerce.model

data class Cart(
    val cartId: Long? = null,
    val userId: Long,
    val products: List<CartItem>,
)
