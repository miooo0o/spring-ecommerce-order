package ecommerce.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class TokenRequest(
    @field:NotBlank(message = "Field cannot be blank")
    @field:Email(message = "Invalid email")
    val email: String,
    @field:NotBlank(message = "Field cannot be blank")
    @field:Size(min = 8, max = 30, message = "Invalid password: must between 8 and 30 characters")
    val password: String,
)
