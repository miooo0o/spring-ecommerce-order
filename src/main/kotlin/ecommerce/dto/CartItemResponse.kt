package ecommerce.dto

import ecommerce.model.*

class CartItemResponse(
    var quantity: Long,
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val productImageUrl: String,
) {
    fun toCartItem(): CartItem {
        val product = Product(productId, productName, productPrice, productImageUrl)
        val cart = mockCart(productId)

        return CartItem(
            id = CartItemId(),
            cart = cart,
            product = product,
            quantity.toInt()
        )
    }

    companion object {
        fun mockCart(productId: Long): Cart {
            return Cart(
                id = null,
                member = mockMember(productId)
            )
        }

        fun mockMember(productId: Long): Member {
            return Member(
                productId,
                "mock@me.com",
                "",
                Role.USER.name
            )
        }
    }
}
