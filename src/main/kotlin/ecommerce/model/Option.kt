package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class Option(
    @Column(nullable = false)
    val name: String = "",
    @Column(nullable = false)
    var quantity: Int = 1,
    @ManyToOne(fetch = FetchType.EAGER)
    var product: Product,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    init {
        require(quantity in 1 until 100_000_000) { "quantity must be in range 1..100_000_000" }
        require(name.length <= 50) { "name length must be <= 50 characters" }
        require(name.all { it.isLetterOrDigit() || it in allowedSpecialChars })
        if (product.options.isNotEmpty()) {
            require(product.options.all { it.name != this.name }) { "duplicate name ${product.name} found" }
        }
        product.options.add(this)
    }

    fun decreaseQuantity(quantity: Int) {
        require(quantity > 0) { "quantity must be positive" }
        if (this.quantity - quantity <= 0) {
            this.quantity = 1
        } else {
            this.quantity -= quantity
        }
    }

    companion object {
        val allowedSpecialChars = setOf('(', ')', '[', ']', '+', '-', '&', '/', '_')
    }
}
