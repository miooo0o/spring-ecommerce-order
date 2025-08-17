package ecommerce.model

import ecommerce.BasicTestFixture
import ecommerce.BasicTestFixture.createBrushWithOptions
import ecommerce.OrderTestFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal

class OrderTest {
    @Test
    fun `create order and order item for unit test`() {
        assertDoesNotThrow {
            val member = BasicTestFixture.MINA
            val products = listOf(createBrushWithOptions())
            val cart =
                Cart(member).apply {
                    products.forEach { product ->
                        addItem(
                            option = product.options[0],
                            quantity = 1,
                        )
                    }
                }
            val order = Order.fromCart(cart, currency = "EUR")
        }

        assertDoesNotThrow {
            OrderTestFixture(
                BasicTestFixture.MINA,
                listOf(createBrushWithOptions()),
            )
        }
    }

    @Test
    fun `should add all given items to order`() {
        val member = BasicTestFixture.MINA
        val products = listOf(createBrushWithOptions())
        val cart =
            Cart(member).apply {
                products.forEach { product ->
                    addItem(
                        option = product.options[0],
                        quantity = 1,
                    )
                }
            }
        val order = Order.fromCart(cart, currency = "EUR")

        val orderItemListsManual: List<OrderItem> =
            cart.items.map { cartItem ->
                OrderItem(
                    option = cartItem.option,
                    productName = cartItem.product.name,
                    unitPrice = BigDecimal(cartItem.product.price),
                    quantity = cartItem.quantity,
                )
            }

        assertThat(order.items.map { it.productName })
            .containsExactlyElementsOf(orderItemListsManual.map { it.productName })

        assertThat(order.items.map { it.unitPrice })
            .containsExactlyElementsOf(orderItemListsManual.map { it.unitPrice })

        assertThat(order.items.map { it.quantity })
            .containsExactlyElementsOf(orderItemListsManual.map { it.quantity })
    }
}
