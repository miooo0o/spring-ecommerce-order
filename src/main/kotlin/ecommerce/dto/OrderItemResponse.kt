package ecommerce.dto

class OrderItemResponse(
    val orderItemId: Long,
    val itemName: String,
    val unitPrice: Long,
    val quantity: Int,
)
