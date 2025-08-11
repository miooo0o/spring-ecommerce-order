package ecommerce.dto

import ecommerce.model.Product
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

class ProductRequest(
    @field:Pattern(
        regexp = "^[a-zA-Z0-9()\\[\\]+\\-&/_ ]+$",
        message = "Only letters, digits and these special characters are allowed: () [] + - & / _",
    )
    @field:Size(max = 15, message = "Product name cannot exceed 15 characters")
    val name: String = "",
    @field:Positive(message = "Price must be > 0")
    val price: Long = 0L,
    @field:Pattern(regexp = "^https?://.*", message = "URL must start with https:// or http://")
    val imageUrl: String = "",
    @field:Size(min = 1, message = "Product needs minimum one option")
    val options: List<OptionRequest>? = null,
) {
    fun toProduct(id: Long = 0L): Product {
        return Product(this.name, this.price, this.imageUrl, id)
    }
}
