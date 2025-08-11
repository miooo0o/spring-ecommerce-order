package ecommerce.model

import ecommerce.MemberTestFixture
import ecommerce.MemberTestFixture.PAINTING_SAD_HUMAN
import ecommerce.MemberTestFixture.createBrushWithOptions
import ecommerce.MemberTestFixture.createCanvas
import ecommerce.MemberTestFixture.createProductWithOptions
import ecommerce.OrderTestFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class OrderTest {
    @Test
    fun `create order and order item for unit test`() {
        assertDoesNotThrow {
            val member = MemberTestFixture.MINA
            val product = createBrushWithOptions()
            val order = Order(member)
            val orderItem =
                OrderItem(
                    order,
                    option = product.options[0],
                    itemName = product.name + product.options[0].name,
                    unitPrice = Money(BigDecimal(product.price)),
                    quantity = 1,
                )
        }

        assertDoesNotThrow {
            val member = MemberTestFixture.MINA
            val product = createBrushWithOptions()
            val orderTestFixture = OrderTestFixture(member, listOf(product))
        }
    }

    @Test
    fun `should add all given items to order`() {
        val orderTestFixture =
            OrderTestFixture(
                MemberTestFixture.MINA,
                listOf(createBrushWithOptions()),
            )

        val order = orderTestFixture.order
        val itemsList = orderTestFixture.validOrderItemsList

        assertThat(order.items).isEmpty()

        itemsList.forEach { item ->
            order.addItem(
                option = item.option,
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                itemName = item.itemName
            )
        }
        assertThat(order.items).isNotEmpty()
        assertThat(itemsList).isEqualTo(order.items.toList())
    }

    @Test
    fun `should add only items belonging to this order`() {
        val fixtureByMina =
            OrderTestFixture(
                MemberTestFixture.MINA,
                listOf(createBrushWithOptions()),
            )

        val fixtureByPetra =
            OrderTestFixture(
                MemberTestFixture.PETRA,
                listOf(
                    createProductWithOptions(PAINTING_SAD_HUMAN),
                    createProductWithOptions(createCanvas()),
                ),
            )

        val orderByPetra = fixtureByPetra.order
        val itemListBelongToMina = fixtureByMina.validOrderItemsList

        assertThat(orderByPetra.items).isEmpty()

        assertThrows<IllegalArgumentException> {
           itemListBelongToMina.forEach { item ->
               orderByPetra.addItem(
                   option = item.option,
                   quantity = item.quantity,
                   unitPrice = item.unitPrice,
                   itemName = item.itemName
               )
           }
        }
    }
}
