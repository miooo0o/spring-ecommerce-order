package ecommerce.dto

import java.time.LocalDateTime

class ProductStatsResponse(
    val productName: String,
    val productQuantity: Int,
    val mostRecent: LocalDateTime,
)
