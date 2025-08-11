package ecommerce.model.mapper

import ecommerce.dto.CartItemResponse
import ecommerce.dto.OptionResponse
import ecommerce.dto.OrderItemResponse
import ecommerce.model.CartItem
import ecommerce.model.Option
import ecommerce.model.Order

object CartItemMapper {
    fun toResponse(cartItem: CartItem): CartItemResponse {
        return CartItemResponse(
            quantity = cartItem.quantity,
            productId = cartItem.product.id,
            productName = cartItem.product.name,
            productPrice = cartItem.product.price,
            productImageUrl = cartItem.product.imageUrl,
        )
    }
}

object OptionMapper {
    fun toOptionResponse(option: Option) =
        OptionResponse(
            optionId = option.id,
            name = option.name,
            quantity = option.quantity,
        )
}

object OrderItemMapper {
    fun toOrderItemResponse(order: Order): List<OrderItemResponse> {
        return order.orderItems.map { orderItem ->
            OrderItemResponse(
                orderItemId = orderItem.id,
                itemName = orderItem.itemName,
                unitPrice = orderItem.unitPrice,
                quantity = orderItem.quantity
            )
        }
    }
}
