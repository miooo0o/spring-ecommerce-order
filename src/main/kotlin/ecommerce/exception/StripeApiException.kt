package ecommerce.exception

import ecommerce.dto.StripeErrorInfo

sealed class StripeApiException(message: String, override val cause: Throwable? = null) : RuntimeException(message) {
    class Client(info: StripeErrorInfo, cause: Throwable? = null) :
        StripeApiException("Stripe client error: ${info.message} (code: ${info.code})", cause)

    class Server(info: StripeErrorInfo, cause: Throwable? = null) :
        StripeApiException("Stripe server error: ${info.message} (code: ${info.code})", cause)

    class Other(info: StripeErrorInfo, cause: Throwable? = null) :
        StripeApiException("Stripe Unknown error: ${info.message} (code: ${info.code})", cause)
}
