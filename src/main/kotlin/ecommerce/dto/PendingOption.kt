package ecommerce.dto

import ecommerce.exception.DuplicateOptionNameException

class PendingOption(
    val name: String,
    var quantity: Int,
    val optionPrice: Long = 0L,
) {
    companion object {
        fun from(request: ProductRequest): List<PendingOption> {
            require(!request.options.isNullOrEmpty()) { "Option list must not be empty" }
            return request.options.map {
                PendingOption(
                    name = it.name,
                    quantity = it.quantity,
                    optionPrice = it.optionPrice
                )
            }
        }
    }
}

class PendingProduct(
    val name: String,
    val price: Long,
    val imageUrl: String,
) {
    companion object {
        fun from(request: ProductRequest): PendingProduct {
            return PendingProduct(
                name = request.name,
                price = request.price,
                imageUrl = request.imageUrl
            )
        }
    }
}