package ecommerce.dto

class OrderRequest(
    val cartId: Long,
    val currency: String,
    val notes: String? = null,
)
