package ecommerce.dto

import ecommerce.model.Cart
import ecommerce.model.CartItem
import ecommerce.model.Member
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

        return CartItem(
            cart = Cart(member = PETRA_USER),
            product = product,
        )
    }

    companion object {
        val PETRA_USER =
            Member(
                email = "letMeGo@deliveryhero.com",
                name = "Petra",
                password = "pizza",
                role = Role.USER.name,
            )
    }
}
