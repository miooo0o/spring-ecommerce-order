package ecommerce.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
class Payment(
    val amount: Int,
    val currency: String,
    val paymentMethod: String,
    @CreationTimestamp
    var createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    var lastUpdatedAt: LocalDateTime? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
)