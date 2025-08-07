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
    @ManyToOne(fetch = FetchType.LAZY)
    val product: Product,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    init {
        require(quantity in 1 until MAX_QUANTITY) { "quantity must be in range 1.. $MAX_QUANTITY" }
        require(name.length <= MAX_NAME_LENGTH) { "name length must be <= 50 characters" }
        require(name.all { it.isLetterOrDigit() || it in ALLOWED_SPECIAL_CHARS })
        product.options.add(this) // TODO: separate relation
    }

    fun decreaseQuantity(quantity: Int) {
        require(quantity > 0) { "quantity must be positive" }
        require(this.quantity > quantity) { "cannot decrease quantity of $quantity times" }

        this.quantity -= quantity
    }

    fun increaseQuantity(quantity: Int) {
        require(this.quantity + quantity < MAX_QUANTITY) { "quantity must be positive" }
        this.quantity += quantity
    }

    companion object {
        val ALLOWED_SPECIAL_CHARS = setOf('(', ')', '[', ']', '+', '-', '&', '/', '_')
        const val MAX_QUANTITY = 100_000_000
        const val MAX_NAME_LENGTH = 50
    }
}
