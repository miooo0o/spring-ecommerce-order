package ecommerce.client

import com.fasterxml.jackson.databind.ObjectMapper
import ecommerce.dto.CheckoutRequest
import ecommerce.dto.PaymentIntent
import ecommerce.dto.StripeErrorInfo
import ecommerce.exception.StripeException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
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
        } catch (e: HttpClientErrorException) {
            throw StripeException.Client(
                parseStripeError(e.responseBodyAsString),
                e.cause,
            )
        } catch (e: HttpServerErrorException) {
            throw StripeException.Server(
                parseStripeError(e.responseBodyAsString),
                e.cause,
            )
        } catch (e: Exception) {
            throw StripeException.Other("Stripe error: ${e.message}", e.cause)
        }
    }

    fun parseStripeError(json: String?): StripeErrorInfo {
        return try {
            val obj = ObjectMapper().readTree(json)
            StripeErrorInfo(
                message = obj.get("error").get("message").asText(),
                code = obj.get("error").get("code").asText(),
            )
        } catch (e: Exception) {
            StripeErrorInfo(message = "Unable to parse Stripe error: ${e.message}")
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
