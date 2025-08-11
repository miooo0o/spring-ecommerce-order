package ecommerce.model

import ecommerce.exception.NotFoundException

enum class Currency(val minMinorUnit: Long, val minorUnitFactor: Int) {
    EUR(minMinorUnit = 50, minorUnitFactor = 100);

    companion object {
        fun fromCode(code: String): Currency =
            entries.find { it.name.equals(code, ignoreCase = true) }
                ?: throw NotFoundException("Unsupported currency: $code")

        val DEFAULT = EUR
    }
}
