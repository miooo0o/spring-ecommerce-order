package ecommerce.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

// TODO to BigDecimal(10, 2)

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "price", nullable = false)
    var price: Double,
    @Column(name = "image_url", nullable = false)
    var imageUrl: String,
) {
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var options: MutableList<Option> = mutableListOf()

//    init {
//        require(options.isNotEmpty()) { "Product name must not be blank" }
//    }

    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as Product

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: System.identityHashCode(this)
    }
}
