package ecommerce.exception

import ecommerce.dto.StripeErrorInfo

sealed class StripeException(message: String, override val cause: Throwable? = null) : RuntimeException(message) {
    class Server(info: StripeErrorInfo, cause: Throwable? = null) :
        StripeException("Stripe server error: ${info.message} (code: ${info.code})", cause)

    class Client(info: StripeErrorInfo, cause: Throwable? = null) :
        StripeException("Stripe client error: ${info.message} (code: ${info.code})", cause)

    class Other(override val message: String, override val cause: Throwable? = null) : StripeException(message, cause)
}
