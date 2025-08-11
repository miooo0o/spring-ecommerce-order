package ecommerce.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
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
    val items: MutableList<OrderItem> = mutableListOf(),
    val currency: Currency = Currency.EUR,
    @Embedded
    var total: Money = Money.ZERO,
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
    fun addItem(
        option: Option,
        quantity: Int,
        unitPrice: Money,
        itemName: String,
    ) {
        require(quantity > 0) { "quantity must be positive." }

        val line =
            OrderItem(
                order = this,
                option = option,
                quantity = quantity,
                unitPrice = unitPrice,
                itemName = itemName,
            )
        items += line
        total = total.plus(line.lineTotal)
    }

    fun snapshotOrderItemFrom(cart: Cart) {
        require(cart.items.isNotEmpty()) { "Cart is empty" }
        cart.items.forEach { cartItem ->
            require(cartItem.option.product.id == cartItem.product.id) { "Option does not belong to product" }
            require(cartItem.option.quantity >= cartItem.quantity) { "Out of stock for option ${cartItem.option.id}" }

            val name = "${cartItem.product.name}: ${cartItem.option.name}"
            val unitPrice = cartItem.option.effectivePrice()
            addItem(
                option = cartItem.option,
                quantity = cartItem.quantity,
                unitPrice = Money(BigDecimal(unitPrice), currency = currency),
                itemName = name
            )

            cartItem.option.decreaseQuantity(cartItem.quantity)
        }
    }
}
