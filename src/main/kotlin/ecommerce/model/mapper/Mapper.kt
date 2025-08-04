package ecommerce.model.mapper

import ecommerce.dto.CartItemResponse
import ecommerce.dto.CartResponse
import ecommerce.model.Cart
import ecommerce.model.CartItem

object CartMapper {
    fun toResponse(cart: Cart): CartResponse {
        return CartResponse(
            cartId = cart.id,
            items = cart.items.map { CartItemMapper.toResponse(it) },
        )
    }
}

object CartItemMapper {
    fun toResponse(cartItem: CartItem): CartItemResponse {
        return CartItemResponse(
            quantity = cartItem.quantity,
            productId = cartItem.product.id ?: 0,
            productName = cartItem.product.name,
            productPrice = cartItem.product.price,
            productImageUrl = cartItem.product.imageUrl,
        )
    }
}
