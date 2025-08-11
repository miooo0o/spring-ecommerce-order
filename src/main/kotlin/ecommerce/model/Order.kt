package ecommerce.model

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
class Order(
    @ManyToOne(fetch = FetchType.LAZY)
    val member: Member,
    @OneToMany(
        mappedBy = "order",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val orderItems: MutableList<OrderItem> = mutableListOf(),
    @Column(nullable = false, scale = 2)
    private var _total: BigDecimal = BigDecimal.ZERO,
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    @Column(nullable = false)
    var lastUpdatedAt: LocalDateTime? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    val totalAmount: BigDecimal
        get() = _total

    fun addItems(items: List<OrderItem>) {
        require(items.isNotEmpty()) { "Items must not be empty" }

        items.forEach { addItem(it) }
        recalcTotal()
    }

    private fun addItem(item: OrderItem) {
        require(item.order === this) { "Item does not belong to this order" }
        orderItems.add(item)
    }

    private fun recalcTotal() {
        _total =
            orderItems.fold(BigDecimal.ZERO) { acc, i ->
                acc + (i.unitPrice * BigDecimal(i.quantity))
            }
        require(totalAmount >= BigDecimal(0.50)) { "minimum total amount must be 0.5" }
    }

    val stripeAmount: Long
        get() = toMinorUnitLong()

    private fun toMinorUnitLong() =
        totalAmount
            .multiply(BigDecimal(100))
            .setScale(0, RoundingMode.HALF_UP)
            .toLong()

    // TODO: Separate to Money class
}
