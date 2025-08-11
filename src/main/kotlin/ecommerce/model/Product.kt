package ecommerce.model

import ecommerce.dto.PendingOption
import ecommerce.dto.PendingProduct
import ecommerce.exception.DuplicateOptionNameException
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "products")
class Product(
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "price", nullable = false)
    val price: Long,
    @Column(name = "image_url", nullable = false)
    val imageUrl: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val options: MutableList<Option> = mutableListOf()
) {
    init {
        require(options.isNotEmpty()) { "Product must have at least one option." }
    }

    fun addOptions(newOptions: List<Option>): Product {
        require(options.isNotEmpty()) { "Product must have at least one option." }

        newOptions.forEach { this.addOption(it) }
        return this
    }

    private fun addOption(newOption: Option): Product {
        require(options.none { it.name == newOption.name }) {
            throw DuplicateOptionNameException("Duplicate option name: ${newOption.name}")
        }

        options.add(newOption)
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as Product

        return id != 0L && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun withOption(pendingProduct: PendingProduct, pendingOptions: List<PendingOption>): Product {
            val product = Product(
                name = pendingProduct.name,
                price = pendingProduct.price,
                imageUrl = pendingProduct.imageUrl
            )

            val options = pendingOptions.map {
                Option(
                    name = it.name,
                    quantity = it.quantity,
                    optionalPrice = it.optionPrice,
                )
            }

            product.options.addAll(options)
            return product
        }

        internal fun toDummy(name: String, price: Long, imageUrl: String, pendingOptions: List<PendingOption>): Product {
            return Product(name = name, price = price, imageUrl = imageUrl)
        }
    }
}
