package ecommerce.dto

import jakarta.validation.constraints.Min

class CartItemRequest(
    @field:Min(1)
    val productId: Long,
    @field:Min(1)
    val quantity: Long,
)
