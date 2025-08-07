package ecommerce.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class OptionRequest(
    @field:Pattern(
        regexp = "^[a-zA-Z0-9()\\[\\]+\\-&/_ ]+$",
        message = "Only letters, digits and these special characters are allowed: () [] + - & / _",
    )
    @field:Size(max = 50)
    val name: String,
    @field:Min(value = MIN_QUANTITY, message = "At least 1 quantity required")
    @field:Max(value = MAX_QUANTITY, message = "quantity cannot be bigger then ")
    val quantity: Int,
) {
    companion object {
        const val MAX_QUANTITY = 99_999_999L
        const val MIN_QUANTITY = 1L
    }
}
