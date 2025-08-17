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
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id")
    val option: Option,
    @field:Size(max = 100)
    @field:NotBlank
    @Column(nullable = false)
    val productName: String,
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
        require(productName.isNotEmpty()) { "Product name can not be empty" }
        require(unitPrice > BigDecimal.ZERO) { "Unit price must be positive" }
        require(unitPrice > BigDecimal(Order.MIN_CALCULATED_AMOUNT)) { "Unit price must be positive" }
        require(quantity > 0) { "Quantity must be positive" }
        require(quantity <= option.availableStock) { "Quantity must be small or equal with option.quantity" }
    }
}
