package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "cart_items")
class CartItem(
    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var product: Product,
    @JoinColumn(name = "option_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var option: Option,
    @JoinColumn(name = "cart_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var cart: Cart,
    @Column(nullable = false)
    var quantity: Int = 1,
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = createdAt,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    val member: Member get() = cart.member

    fun overrideOptionWith(option: Option, quantity: Int) {
        require(option !== this.option) { "match with assigned option found" }
        require(quantity > 0) { "quantity must be positive" }
        this.option = option
        this.quantity = quantity
        markAsUpdated()
    }

    fun changeQuantityTo(quantity: Int) {
        require(quantity > 0) { "quantity must be positive" }
        this.quantity = quantity
        markAsUpdated()
    }

    fun increaseQuantityBy(by: Int = 1) = changeQuantityTo(this.quantity + by)

    fun decreaseQuantityBy(by: Int = 1) = changeQuantityTo(this.quantity - by)

    private fun markAsUpdated() {
        this.updatedAt = LocalDateTime.now()
    }
}
