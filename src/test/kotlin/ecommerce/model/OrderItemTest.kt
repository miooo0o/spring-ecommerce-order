package ecommerce.model

import ecommerce.BasicTestFixture
import ecommerce.BasicTestFixture.createBrushWithOptions
import ecommerce.OrderTestFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class OrderItemTest {

    @Test
    fun `should create order item without throwing exception`() {
        assertDoesNotThrow {
            val member = BasicTestFixture.MINA
            val product = createBrushWithOptions()
            val orderTestFixture = OrderTestFixture(member, listOf(product))
        }
    }

    @ValueSource(doubles = [0.0, -1.0, 0.45])
    @ParameterizedTest
    fun `should throw when unit price is invalid`(price: Double) {
        val member = BasicTestFixture.MINA
        val product = createBrushWithOptions()
        val orderTestFixture = OrderTestFixture(member, listOf(product))

        val exception = assertThrows<IllegalArgumentException> {
            val optionIndex = orderTestFixture.optionIndex
            orderTestFixture.products.map { product ->
                OrderItem(
                    orderTestFixture.order,
                    product.options[orderTestFixture.optionIndex],
                    product.name + product.options[optionIndex].name,
                    (price * 100).toLong(),
                    product.options[optionIndex].quantity + 1,
                )
            }
        }
        assertThat(exception.message).isEqualTo("Unit price must be positive")
    }

    @ValueSource(ints = [0, -1, -9999999])
    @ParameterizedTest
    fun `should throw when quantity is invalid`(quantity: Int) {
        val member = BasicTestFixture.MINA
        val product = createBrushWithOptions()
        val orderTestFixture = OrderTestFixture(member, listOf(product))

        val exception = assertThrows<IllegalArgumentException> {
            val optionIndex = orderTestFixture.optionIndex
            orderTestFixture.products.map { product ->
                OrderItem(
                    orderTestFixture.order,
                    product.options[orderTestFixture.optionIndex],
                    product.name + product.options[optionIndex].name,
                    (product.price * 100).toLong(),
                    quantity,
                )
            }
        }
        assertThat(exception.message).isEqualTo("Quantity must be positive")
    }

    @Test
    fun `should throw when quantity exceeds available option quantity`() {
        val member = BasicTestFixture.MINA
        val product = createBrushWithOptions()
        val orderTestFixture = OrderTestFixture(member, listOf(product))

        val exception = assertThrows<IllegalArgumentException> {
            val optionIndex = orderTestFixture.optionIndex
            orderTestFixture.products.map { product ->
                OrderItem(
                    orderTestFixture.order,
                    product.options[orderTestFixture.optionIndex],
                    product.name + product.options[optionIndex].name,
                    (product.price * 100).toLong(),
                    product.options[optionIndex].quantity + 1,
                )
            }
        }
        assertThat(exception.message).isEqualTo("Quantity must be small or equal with option.quantity")
    }

}
