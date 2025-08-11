package ecommerce.model

import ecommerce.MemberTestFixture
import ecommerce.MemberTestFixture.createBrushWithOptions
import ecommerce.OrderTestFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

class OrderItemTest {
    @Test
    fun `should create order item without throwing exception`() {
        assertDoesNotThrow {
            val member = MemberTestFixture.MINA
            val product = createBrushWithOptions()
            val orderTestFixture = OrderTestFixture(member, listOf(product))
        }
    }

    @ValueSource(doubles = [0.0, -1.0, 0.45])
    @ParameterizedTest
    fun `should throw when unit price is invalid`(price: Double) {
        val member = MemberTestFixture.MINA
        val product = createBrushWithOptions()
        val orderTestFixture = OrderTestFixture(member, listOf(product))

        val exception =
            assertThrows<IllegalArgumentException> {
                val optionIndex = orderTestFixture.optionIndex
                orderTestFixture.products.map { product ->
                    OrderItem(
                        order = orderTestFixture.order,
                        option = product.options[orderTestFixture.optionIndex],
                        itemName = product.name + product.options[optionIndex].name,
                        unitPrice = Money(BigDecimal(price)),
                        quantity = product.options[optionIndex].quantity,
                    )
                }
            }
        assertThat(exception.message).isEqualTo("Unit price must be positive")
    }

    @ValueSource(ints = [0, -1, -9999999])
    @ParameterizedTest
    fun `should throw when quantity is invalid`(quantity: Int) {
        val member = MemberTestFixture.MINA
        val product = createBrushWithOptions()
        val orderTestFixture = OrderTestFixture(member, listOf(product))

        val exception =
            assertThrows<IllegalArgumentException> {
                val optionIndex = orderTestFixture.optionIndex
                orderTestFixture.products.map { product ->
                    OrderItem(
                        order = orderTestFixture.order,
                        option = product.options[orderTestFixture.optionIndex],
                        itemName = product.name + product.options[optionIndex].name,
                        unitPrice = Money(BigDecimal(product.price)),
                        quantity = quantity,
                    )
                }
            }
        assertThat(exception.message).isEqualTo("Quantity must be positive")
    }

    @Test
    fun `should throw when quantity exceeds available option quantity`() {
        val member = MemberTestFixture.MINA
        val product = createBrushWithOptions()
        val orderTestFixture = OrderTestFixture(member, listOf(product))

        val exception =
            assertThrows<IllegalArgumentException> {
                val optionIndex = orderTestFixture.optionIndex
                orderTestFixture.products.map { product ->
                    OrderItem(
                        order = orderTestFixture.order,
                        option = product.options[orderTestFixture.optionIndex],
                        itemName = product.name + product.options[optionIndex].name,
                        unitPrice = Money(BigDecimal(product.price)),
                        quantity = product.options[optionIndex].quantity + 1,
                    )
                }
            }
        assertThat(exception.message).isEqualTo("Quantity must be small or equal with option.quantity")
    }
}
