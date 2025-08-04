package ecommerce.model

import jakarta.persistence.*

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
        cascade = [CascadeType.PERSIST, CascadeType.REMOVE],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val items: MutableList<CartItem> = mutableListOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    // TODO: check if @PreUpdate is really working here
    fun addItem(product: Product, quantity: Int) {
        require(quantity > 0) { "Item quantity must be greater than zero." }
        val existingItem = items.find { it.product == product }
        when (existingItem) {
            null -> {
                val newItem = CartItem(product = product, cart = this, quantity = quantity)
                items.add(newItem)
            }

            else ->
                existingItem?.quantity += quantity
        }
    }

    // TODO: if quantity gets to 0, shall we remove the item entirely?
    fun removeItem(product: Product, quantity: Int) {
        require(quantity > 0) { "Item quantity must be greater than zero." }
        val existingItem = items.find { it.product == product } ?: throw IllegalArgumentException("Item not found.")
        var quantityToRemove = quantity
        if (existingItem.quantity < quantity) quantityToRemove = existingItem.quantity

        existingItem.quantity -= quantityToRemove
    }
}
