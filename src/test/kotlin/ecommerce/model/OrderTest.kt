package ecommerce.model

import ecommerce.BasicTestFixture
import ecommerce.BasicTestFixture.PAINTING_SAD_HUMAN
import ecommerce.BasicTestFixture.createBrushWithOptions
import ecommerce.BasicTestFixture.createCanvas
import ecommerce.BasicTestFixture.createProductWithOptions
import ecommerce.OrderTestFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class OrderTest {
    @Test
    fun `create order and order item for unit test`() {
        assertDoesNotThrow {
            val member = BasicTestFixture.MINA
            val product = createBrushWithOptions()
            val order = Order(member)
            val orderItem =
                OrderItem(
                    order,
                    product.options[0],
                    product.name + product.options[0].name,
                    (product.price * 100).toLong(),
                    1,
                )
        }

        assertDoesNotThrow {
            val member = BasicTestFixture.MINA
            val product = createBrushWithOptions()
            val orderTestFixture = OrderTestFixture(member, listOf(product))
        }
    }

    @Test
    fun `should add all given items to order`() {
        val orderTestFixture =
            OrderTestFixture(
                BasicTestFixture.MINA,
                listOf(createBrushWithOptions()),
            )

        val order = orderTestFixture.order
        val itemsList = orderTestFixture.validOrderItemsList

        assertThat(order.orderItems).isEmpty()

        order.addItems(itemsList)

        assertThat(order.orderItems).isNotEmpty()
        assertThat(itemsList).isEqualTo(order.orderItems.toList())
    }

    @Test
    fun `should add only items belonging to this order`() {
        val fixtureByMina =
            OrderTestFixture(
                BasicTestFixture.MINA,
                listOf(createBrushWithOptions()),
            )

        val fixtureByPetra =
            OrderTestFixture(
                BasicTestFixture.PETRA,
                listOf(
                    createProductWithOptions(PAINTING_SAD_HUMAN),
                    createProductWithOptions(createCanvas()),
                ),
            )

        val orderByPetra = fixtureByPetra.order
        val itemListBelongToMina = fixtureByMina.validOrderItemsList

        assertThat(orderByPetra.orderItems).isEmpty()

        assertThrows<IllegalArgumentException> {
            orderByPetra.addItems(itemListBelongToMina)
        }
    }
}
