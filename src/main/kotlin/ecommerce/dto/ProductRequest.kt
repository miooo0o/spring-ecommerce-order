package ecommerce.dto

import ecommerce.model.Product
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class ProductRequest(
    @field:Pattern(
        regexp = "^[a-zA-Z0-9()\\[\\]+\\-&/_ ]+$",
        message = "Only letters, digits and these special characters are allowed: () [] + - & / _",
    )
    @field:Size(max = 15, message = "Product name cannot exceed 15 characters")
    var name: String = "",
    @field:Positive(message = "Price must be > 0")
    var price: Double = 0.0,
    @field:Pattern(regexp = "^https?://.*", message = "URL must start with https:// or http://")
    var imageUrl: String = "",
) {
    fun toProduct(): Product {
        return Product(null, this.name, this.price, this.imageUrl)
    }
}
