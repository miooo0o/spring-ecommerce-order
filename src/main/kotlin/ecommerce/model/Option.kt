package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "options")
open class Option(
    @Column(nullable = false)
    val name: String = "",
    @Column(nullable = false)
    var availableStock: Int = 1,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    @ManyToOne(fetch = FetchType.LAZY)
    lateinit var product: Product

    fun decreaseStock(quantity: Int): Option {
        require(quantity > 0) { "Quantity must be positive, but was: $quantity" }
        require(this.availableStock >= quantity) {
            "Insufficient stock: requested $quantity, but only $availableStock available"
        }

        this.availableStock -= quantity
        return this
    }

    companion object {
        const val MAX_STOCK = 100_000_000
    }
}
