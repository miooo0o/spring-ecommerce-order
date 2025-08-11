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
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

@Entity
@Table(name = "order_items")
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    val order: Order,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id")
    val option: Option,
    @field:Size(max = 100)
    @field:NotBlank
    @Column(nullable = false)
    val itemName: String,
    @Column(nullable = false)
    @Positive
    val unitPrice: Long,
    @Column(nullable = false)
    @Positive
    val quantity: Int,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    init {
        require(itemName.isNotEmpty()) { "Product name can not be empty" }
        require(unitPrice > 0L) { "Unit price must be positive" }
        require(unitPrice > Order.MIN_CALCULATED_AMOUNT * 100) { "Unit price must be positive" }
        require(quantity > 0) { "Quantity must be positive" }
        require(quantity <= option.quantity) { "Quantity must be small or equal with option.quantity" }
    }
}

// TODO: Length at productName?
