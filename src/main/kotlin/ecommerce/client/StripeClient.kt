package ecommerce.client

import com.stripe.exception.ApiConnectionException
import com.stripe.exception.ApiException
import com.stripe.exception.AuthenticationException
import com.stripe.exception.CardException
import com.stripe.exception.IdempotencyException
import com.stripe.exception.InvalidRequestException
import com.stripe.exception.PermissionException
import com.stripe.exception.RateLimitException
import com.stripe.exception.SignatureVerificationException
import com.stripe.exception.StripeException
import ecommerce.dto.CheckoutRequest
import ecommerce.dto.PaymentIntent
import ecommerce.dto.StripeErrorInfo
import ecommerce.exception.StripeApiException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.Duration

@Component
class StripeClient(private val stripeProperties: StripeProperties) {
    private val restClient =
        RestClient.builder()
            .requestFactory(
                SimpleClientHttpRequestFactory().apply {
                    setConnectTimeout(Duration.ofMillis(500))
                    setReadTimeout(Duration.ofSeconds(10))
                },
            )
            .build()

    fun makePayment(request: CheckoutRequest): PaymentIntent {
        val body =
            listOf(
                "amount=${request.amount}",
                "currency=${request.currency}",
                "payment_method=${request.paymentMethod}",
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
        } catch (e: StripeException) {
            val info = parseStripeError(e)
            when (e) {
                is CardException,
                is PermissionException,
                is RateLimitException,
                is InvalidRequestException,
                is AuthenticationException,
                is IdempotencyException,
                is SignatureVerificationException,
                -> throw StripeApiException.Client(info, e)

                is ApiConnectionException,
                is ApiException,
                -> throw StripeApiException.Server(info, e)

                else -> throw StripeApiException.Other(info, e)
            }
        } catch (e: Exception) {
            throw StripeApiException.Other(parseStripeError(e), e)
        }
    }

    fun parseStripeError(e: Exception): StripeErrorInfo {
        return when (e) {
            is CardException ->
                StripeErrorInfo(
                    message = e.userMessage,
                    code = e.code,
                    type = "card_error",
                    declineCode = e.declineCode,
                    charge = e.charge,
                )

            is InvalidRequestException ->
                StripeErrorInfo(
                    message = e.userMessage,
                    code = e.code,
                    type = "invalid_request_error",
                    param = e.param,
                )

            is AuthenticationException ->
                StripeErrorInfo(
                    message = e.userMessage,
                    code = e.code,
                    type = "authentication_error",
                )

            is StripeException ->
                StripeErrorInfo(
                    message = e.userMessage ?: "Stripe error occurred",
                    code = e.code,
                    requestId = e.requestId,
                )

            else ->
                StripeErrorInfo(
                    message = e.message ?: "Unknown Stripe error occurred",
                )
        }
    }

    private fun validatePaymentIntent(
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
