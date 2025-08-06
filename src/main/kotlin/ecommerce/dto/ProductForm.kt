package ecommerce.dto

data class ProductForm(
    val id: Long? = null,
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
)
