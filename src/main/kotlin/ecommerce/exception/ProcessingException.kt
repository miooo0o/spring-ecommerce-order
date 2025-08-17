package ecommerce.exception

abstract class ProcessingException(
    message: String,
    val errors: List<String>? = null,
) : RuntimeException(message) {
    fun getDetailErrors(): List<String> = errors ?: emptyList()
}

class OrderProcessingException(
    message: String,
    errors: List<String>? = null,
) : ProcessingException(message, errors)

class CartProcessingException(
    message: String,
    errors: List<String>? = null,
) : ProcessingException(message, errors)
