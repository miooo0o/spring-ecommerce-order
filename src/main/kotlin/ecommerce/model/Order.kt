package ecommerce.model

import ecommerce.model.mapper.toOrderItems
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order private constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    val member: Member,
    @OneToMany(
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val items: MutableList<OrderItem> = mutableListOf(),
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    @Column(nullable = false, scale = 2)
    private var _totalMajor: BigDecimal = BigDecimal.ZERO

    val totalMajor: BigDecimal get() = _totalMajor
    val totalMinor: Long get() = toMinor()

    fun addItems(items: List<OrderItem>) {
        require(items.isNotEmpty()) { "Items must not be empty" }

        items.forEach { addItem(it) }
        recalcTotalMajor()
    }

    private fun addItem(item: OrderItem) {
        items.add(item)
    }

    private fun addItemsFromCart(cart: Cart): Order {
        require(cart.items.isNotEmpty()) { "Items must not be empty" }

        this.items.addAll(cart.items.toOrderItems())
        recalcTotalMajor()
        return this
    }

    private fun recalcTotalMajor() {
        val sum =
            items.fold(BigDecimal.ZERO) { acc, item ->
                acc.plus(item.unitPrice * BigDecimal(item.quantity))
            }
        require(sum >= MIN_AMOUNT_BIG_DECIMAL) { "minimum total amount must be 0.5" }
        _totalMajor = sum
    }

    fun toMinor(): Long {
        return _totalMajor
            .multiply(BigDecimal.TEN.pow(MINOR_SCALE))
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }

    companion object {
        private const val MINOR_SCALE = 2
        const val MIN_AMOUNT_DOUBLE = 0.50
        val MIN_AMOUNT_BIG_DECIMAL = BigDecimal(0.50)

        // TODO: mapper?
        fun fromCart(cart: Cart): Order {
            val order = Order(member = cart.member)
            return order.addItemsFromCart(cart)
        }
    }
}
