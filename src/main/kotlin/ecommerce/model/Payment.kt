package ecommerce.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class Payment(
    val amount: Long,
    val currency: String = "eur",
    val paymentMethod: String,
    val paymentIntentId: String,
    val clientSecret: String,
    var paymentIntentStatus: String,
    var failureReason: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
)
