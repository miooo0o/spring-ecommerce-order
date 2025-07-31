package ecommerce.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
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
    @OneToOne
    @JoinColumn(name = "member_id", unique = true)
    val member: Member,
    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<CartItem> = mutableListOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
)
