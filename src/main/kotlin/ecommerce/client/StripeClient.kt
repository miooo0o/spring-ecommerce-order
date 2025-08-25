package ecommerce.client

import ecommerce.dto.CheckoutRequest
import ecommerce.dto.PaymentIntent
import ecommerce.exception.StripePaymentException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class StripeClient(private val stripeProperties: StripeProperties) {
    private val restClient = RestClient.create()

    fun makePayment(request: CheckoutRequest): PaymentIntent {
        val body =
            listOf(
                "amount=${request.amount}",
                "currency=${request.currency}",
                "payment_method=${request.paymentMethod}",
                // TODO: confirm(true),
                "automatic_payment_methods[enabled]=true",
                "automatic_payment_methods[allow_redirects]=never",
            ).joinToString("&")

        return try {
            val response =
                restClient.post()
                    .uri(stripeProperties.createPaymentIntentUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${stripeProperties.secretKey}")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .toEntity(PaymentIntent::class.java)

            val paymentIntent = requireNotNull(response.body)

            validatePaymentIntent(request, paymentIntent)
            paymentIntent
        } catch (e: Exception) {
            throw StripePaymentException("Stripe error: ${e.message}")
        }
    }

    fun validatePaymentIntent(
        request: CheckoutRequest,
        paymentIntent: PaymentIntent,
    ) {
        require(paymentIntent.id.isNotBlank()) { "PaymentIntent id can't be blank" }
        require(paymentIntent.amount > 0) { "Payment amount must be greater than zero" }
        require(paymentIntent.amount == request.amount) { "PaymentIntent amount should same as requested amount" }
        require(paymentIntent.status.isNotBlank()) { "Payment status can't be blank" }
        checkNotNull(paymentIntent.created != null) { "PaymentIntent created can't be null" }
    }
}
