package ecommerce

import ecommerce.model.Cart
import ecommerce.model.Member
import ecommerce.model.Order
import ecommerce.model.OrderItem
import ecommerce.model.Product
import java.math.BigDecimal

class OrderTestFixture(
    val member: Member,
    val products: List<Product>,
    val optionIndex: Int = 0,
) {
    val cart: Cart =
        Cart(member).apply {
            products.forEach { product ->
                addItem(
                    option = product.options[optionIndex],
                    quantity = 1,
                )
            }
        }

    val order: Order = Order.fromCart(cart)

    val validOrderItemsList: List<OrderItem> =
        cart.items.map { cartItem ->
            OrderItem(
                option = cartItem.option,
                productName = cartItem.product.name,
                unitPrice = BigDecimal(cartItem.product.price),
                quantity = cartItem.quantity,
            )
        }
}
