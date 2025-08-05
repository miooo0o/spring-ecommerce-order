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
    @field:Min(value = 1, message = "Price must be > 0")
    @field:Max(value = 99_999_999, message = "Price must be > 0")
    val quantity: Int,
)
