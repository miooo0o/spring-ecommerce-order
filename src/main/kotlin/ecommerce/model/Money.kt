package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.math.BigDecimal
import java.math.RoundingMode

@Embeddable
data class Money(
    @Column(precision = 19, scale = 2, nullable = false)
    val amount: BigDecimal = BigDecimal.ZERO,
    @Enumerated(EnumType.STRING)
    @Column(length = 3, nullable = false)
    val currency: Currency = Currency.EUR,
) {
    init {
        require(amount > BigDecimal.ZERO) { "amount must be positive." }
        require(amount > BigDecimal.ZERO) { "Amount must be positive." }
        require(toMinorUnitLong() >= currency.minMinorUnit) {
            "Amount must be at least ${currency.minMinorUnit} minor units " +
                "(${currency.minMinorUnit.toDouble() / currency.minorUnitFactor} ${currency.name})"
        }
    }

    fun plus(other: Money): Money {
        require(currency == other.currency) { "Currency mismatch" }
        return copy(amount = amount + other.amount)
    }

    fun times(qty: Int): Money = copy(amount = amount.multiply(BigDecimal(qty)))

    fun toMinorUnitLong(): Long =
        amount
            .multiply(BigDecimal(currency.minorUnitFactor))
            .setScale(0, RoundingMode.HALF_UP).longValueExact()

    companion object {
        val ZERO = Money(BigDecimal.ZERO, Currency.EUR)

        fun of(
            amount: String,
            currency: Currency = Currency.EUR,
        ) = Money(BigDecimal(amount), currency)
    }
}
