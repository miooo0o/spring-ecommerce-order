package ecommerce.dto

class StripeErrorInfo(
    val message: String,
    val code: String? = null,
    val type: String? = null,
    val declineCode: String? = null,
    val charge: String? = null,
    val param: String? = null,
    val requestId: String? = null,
)
