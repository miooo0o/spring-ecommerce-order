package ecommerce.model.mapper

import ecommerce.dto.CartItemResponse
import ecommerce.dto.CartResponse
import ecommerce.dto.OptionResponse
import ecommerce.model.Cart
import ecommerce.model.CartItem
import ecommerce.model.Option

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

object OptionMapper {
    fun toOptionResponse(option: Option) =
        OptionResponse(
            id = option.id,
            name = option.name,
            quantity = option.quantity,
        )
}
