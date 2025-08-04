package ecommerce.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

/**
 * Table carts {
 *   member_id bigint [ref: > members.id]
 *   id bigint [pk, increment]
 * }
 */
@Entity
@Table(name = "carts")
class Cart(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    val member: Member,
    @OneToMany(
        mappedBy = "cart",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val items: MutableList<CartItem> = mutableListOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    // TODO: check if @PreUpdate is really working here
    fun addItem(
        product: Product,
        quantity: Int,
    ): CartItem {
        require(quantity > 0) { "Item quantity must be greater than zero." }
        val existingItem = items.find { it.product.id == product.id }
        return when (existingItem) {
            null -> {
                val newItem = CartItem(product = product, cart = this, quantity = quantity)
                items.add(newItem)

                newItem
            }

            else -> {
                existingItem.quantity += quantity

                existingItem
            }
        }
    }

    // TODO: if quantity gets to 0, shall we remove the item entirely?
    fun removeItem(
        product: Product,
        quantity: Int,
    ) {
        require(quantity > 0) { "Item quantity must be greater than zero." }
        val existingItem = items.find { it.product.id == product.id } ?: throw IllegalArgumentException("Item not found.")
        var quantityToRemove = quantity
        if (existingItem.quantity < quantity) quantityToRemove = existingItem.quantity

        existingItem.quantity -= quantityToRemove
    }
}
