package ecommerce.exception

sealed class OrderException(message: String, val errors: List<String>? = null) : RuntimeException(message)

class OrderStockConflictException(
    message: String,
    errors: List<String> = emptyList(),
) : OrderException(message, errors)
