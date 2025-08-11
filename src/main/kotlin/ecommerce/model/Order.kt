package ecommerce.model

import ecommerce.dto.OrderRequest
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
    val currency: String = ALLOWED_CURRENCY[0],
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
    @Column(nullable = false, scale = 2)
    private var _totalMajor: BigDecimal = BigDecimal.ZERO

    val totalMajor: BigDecimal get() = _totalMajor
    val totalMinor: Long get() = toMinor()

    init {
        require(ALLOWED_CURRENCY.any { it == currency })
    }

    fun addItems(items: List<OrderItem>) {
        require(items.isNotEmpty()) { "Items must not be empty" }

        items.forEach { addItem(it) }
        recalcTotalMajor()
    }

    private fun addItem(item: OrderItem) {
        require(item.order === this) { "Item does not belong to this order" }
        orderItems.add(item)
    }

    private fun recalcTotalMajor() {
        val sum =
            orderItems.fold(BigDecimal.ZERO) { acc, item ->
                acc.plus(BigDecimal(item.unitPrice) * BigDecimal(item.quantity))
            }
        require(sum >= BigDecimal(MIN_CALCULATED_AMOUNT)) { "minimum total amount must be 0.5" }
        _totalMajor = sum
    }

    fun toMinor(): Long {
        return _totalMajor
            .multiply(BigDecimal.TEN.pow(MINOR_SCALE))
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }

    companion object {
        private val ALLOWED_CURRENCY = listOf("EUR")
        private const val MINOR_SCALE = 2
        const val MIN_CALCULATED_AMOUNT = 0.50

        fun from(
            cart: Cart,
            request: OrderRequest,
        ): Order {
            return Order(
                member = cart.member,
                currency = request.currency.uppercase(),
            )
        }
    }
}
