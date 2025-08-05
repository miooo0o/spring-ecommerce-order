package ecommerce.model

import ecommerce.config.DatabaseFixture.createCanvas
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class OptionTest {
    @ParameterizedTest
    @ValueSource(strings = ["()[]+-&/_", "abcd1234", "abc"])
    fun `test allowed special characters`(name: String) {
        assertDoesNotThrow { Option(name = name, product = createCanvas(), quantity = 3) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["abc?", "!abc", "a@bc"])
    fun `test not allowed special characters`(name: String) {
        assertThrows<IllegalArgumentException> { Option(name = name, product = createCanvas(), quantity = 3) }
    }

    @Test
    fun `names can include up to 50 characters`() {
        assertDoesNotThrow {
            Option(name = "a".repeat(49), product = createCanvas(), quantity = 3)
        }
    }

    @Test
    fun `names can not include more than 50 characters`() {
        assertThrows<IllegalArgumentException> { Option(name = "a".repeat(51), product = createCanvas(), quantity = 3) }
    }
}
