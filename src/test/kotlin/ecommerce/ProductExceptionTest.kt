package ecommerce

import ecommerce.dto.ProductRequest
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ProductExceptionTest {
    private lateinit var validator: Validator

    @BeforeEach
    fun setup() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    @ParameterizedTest
    @ValueSource(strings = ["abcdefghijklmopqrst", "=*abc", "0123456789abcdefghijkl"])
    fun `invalid product name`(name: String) {
        val violations = validator.validate(ProductRequest(name = name))
        assertThat(violations.isNotEmpty())
    }

    @ParameterizedTest
    @ValueSource(strings = ["abcdefghijklmnop", "abc", "()[]+-&/_", ""])
    fun `valid product name`(name: String) {
        val violations = validator.validate(ProductRequest(name = name))
        assertThat(violations.isEmpty())
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, -1.2])
    fun `invalid product price`(price: Double) {
        val violations = validator.validate(ProductRequest(price = price))
        assertThat(violations.isNotEmpty())
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.1, 1.2])
    fun `valid product price`(price: Double) {
        val violations = validator.validate(ProductRequest(price = price))
        assertThat(violations.isEmpty())
    }

    @ParameterizedTest
    @ValueSource(strings = ["htttps://localhost:8080", "abchttp://localhost:8080"])
    fun `invalid url`(url: String) {
        val violations = validator.validate(ProductRequest(imageUrl = url))
        assertThat(violations.isNotEmpty())
    }

    @ParameterizedTest
    @ValueSource(strings = ["https://localhost:8080", "http://localhost:8080"])
    fun `valid url`(url: String) {
        val violations = validator.validate(ProductRequest(imageUrl = url))
        assertThat(violations.isEmpty())
    }
}
