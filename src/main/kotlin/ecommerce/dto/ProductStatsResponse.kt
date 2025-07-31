package ecommerce.dto

import java.time.LocalDateTime

class ProductStatsResponse(
    val productName: String,
    val productQuantity: Long,
    val mostRecent: LocalDateTime,
)
