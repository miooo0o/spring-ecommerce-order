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

    fun decreaseStock(stock: Int) {
        require(stock > 0) { "stock must be positive" }
        require(this.availableStock > stock) { "cannot decrease quantity of $stock times" }

        this.availableStock -= stock
    }

    fun increaseStock(stock: Int) {
        require(this.availableStock + stock < MAX_STOCK) { "stock must be positive" }

        this.availableStock += stock
    }

    companion object {
        const val MAX_STOCK = 100_000_000
    }
}
