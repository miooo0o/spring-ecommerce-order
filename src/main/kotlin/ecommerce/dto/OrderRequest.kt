package ecommerce.dto

class OrderRequest(
    val amount: Int,
    val currency: String,
    val paymentMethod: String,
)
