package ecommerce.dto

class OrderResponse(
    val orderId: Long,
    val orderItems: List<OrderItemResponse>,
    val currency: String,
)
