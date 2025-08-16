package ecommerce

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
    val order = Order(member)
    val validOrderItemsList: List<OrderItem> = products.map { it.toOrderItemWith(order) }

    private fun Product.toOrderItemWith(order: Order): OrderItem {
        return OrderItem(
            this.options[optionIndex],
            this.name + this.options[optionIndex].name,
            BigDecimal(this.price),
            1,
        )
    }
}
