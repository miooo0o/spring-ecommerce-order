package ecommerce.dto

import ecommerce.model.CartItem
import ecommerce.model.Product

class CartItemResponse(
    var quantity: Long,
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val productImageUrl: String,
) {
    fun toCartItem(): CartItem {
        val product = Product(productId, productName, productPrice, productImageUrl)
        return CartItem(product, quantity.toInt())
    }
}
