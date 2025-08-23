package ecommerce.dto

class CheckoutRequest(
    val orderId: Long,
    val amount: Long,
    val currency: String,
    val paymentMethod: String,
)

class PaymentIntent(
    val id: String,
    val amount: Long,
    val status: String,
    val currency: String,
    val paymentMethod: String,
    val clientSecret: String,
    val created: Long?,
)

class CheckoutResponse(
    val orderId: Long,
    val paymentStatus: String,
)
