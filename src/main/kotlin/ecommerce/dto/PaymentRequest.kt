package ecommerce.dto

class PaymentRequest(
    val amount: Int,
    val currency: String,
    val paymentMethod: String,
)
