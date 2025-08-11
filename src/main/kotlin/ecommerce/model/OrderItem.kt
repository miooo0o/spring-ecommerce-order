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
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    val order: Order,
    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "option_id")
    val option: Option,
    @Column(nullable = false)
    @Positive
    val unitPrice: BigDecimal,
    @Column(nullable = false)
    @Positive
    val quantity: Int,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    init {
        require(unitPrice > BigDecimal.ZERO) { "Unit price must be positive" }
        require(quantity > 0) { "Quantity must be positive" }
    }
}
