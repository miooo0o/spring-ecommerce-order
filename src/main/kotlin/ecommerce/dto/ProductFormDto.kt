package ecommerce.dto

data class ProductFormDto(
    val id: Long? = null,
    val name: String = "",
    val price: Double = 0.0,
    var imageUrl: String = "",
)
