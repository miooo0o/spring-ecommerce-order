package ecommerce.model

import ecommerce.exception.DuplicateOptionNameException
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "products")
class Product(
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "price", nullable = false)
    val price: Double,
    @Column(name = "image_url", nullable = false)
    val imageUrl: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val options: MutableList<Option> = mutableListOf()

    fun addOptions(newOptions: List<Option>): Product {
        newOptions.forEach { this.addOption(it) }
        return this
    }

    private fun addOption(newOption: Option): Product {
        require(options.none { it.name == newOption.name }) {
            throw DuplicateOptionNameException("Duplicate option name: ${newOption.name}")
        }

        newOption.product = this
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
}
