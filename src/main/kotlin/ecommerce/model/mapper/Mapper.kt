package ecommerce.model.mapper

import ecommerce.dto.CartItemResponse
import ecommerce.dto.CheckoutRequest
import ecommerce.dto.OptionResponse
import ecommerce.model.CartItem
import ecommerce.model.Option
import ecommerce.model.Order
import ecommerce.model.OrderItem
import java.math.BigDecimal

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
            quantity = option.availableStock,
        )
}

fun List<CartItem>.toOrderItems() = map { it.toOrderItem() }

private fun CartItem.toOrderItem() =
    OrderItem(
        option = this.option,
        productName = this.product.name,
        unitPrice = BigDecimal(this.product.price),
        quantity = this.quantity,
    )

fun Order.toPaymentRequest(paymentMethod: String): CheckoutRequest {
    return CheckoutRequest(
        orderId = this.id,
        amount = this.totalMinor,
        currency = "eur",
        paymentMethod = paymentMethod,
    )
}
