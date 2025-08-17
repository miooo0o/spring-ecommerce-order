package ecommerce.dto

class ErrorResponse(
    val status: Int,
    val message: String?,
    val errors: List<String>? = null,
    val path: String? = null,
)
