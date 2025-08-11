package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "order_items")
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false)
    val order: Order,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "option_id", nullable = false)
    val option: Option,
    @Column(nullable = false)
    val quantity: Int,
    @Embedded val unitPrice: Money,
    @Column(nullable = false) val itemName: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    val lineTotal: Money = unitPrice.times(quantity)

    init {
        require(itemName.isNotEmpty()) { "Product name can not be empty" }
        require(quantity > 0) { "Quantity must be positive" }
        require(quantity <= option.quantity) { "Quantity must be small or equal with option.quantity" }
    }
}

// TODO: Length at productName?
