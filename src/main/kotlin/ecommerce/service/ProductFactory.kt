package ecommerce.service

import ecommerce.dto.ProductRequest
import ecommerce.model.Option
import ecommerce.model.Product
import org.springframework.stereotype.Component

interface ProductFactory {
    fun from(request: ProductRequest): Product
}

@Component
class DefaultProductFactory : ProductFactory {
    override fun from(request: ProductRequest): Product {
        require(!request.options.isNullOrEmpty()) { "Product must have at least one option" }

        val optionEntities = request.options.map { Option(name = it.name, quantity = it.quantity) }
        val product =
            Product(
                name = request.name,
                price = request.price,
                imageUrl = request.imageUrl,
            ).apply { addOptions(optionEntities) }
        return product
    }
}
