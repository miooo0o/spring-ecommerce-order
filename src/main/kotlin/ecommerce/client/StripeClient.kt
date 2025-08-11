package ecommerce.client

import ecommerce.dto.OrderRequest
import ecommerce.dto.OrderResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class StripeClient(private val stripeProperties: StripeProperties) {
    private val restClient = RestClient.create()

    fun createCheckoutSession(request: OrderRequest): OrderResponse? {
        val body =
            listOf(
                "amount=${request.amount}",
                "currency=${request.currency}",
                "payment_method=${request.paymentMethod}",
                "confirm=true",
                "automatic_payment_methods[enabled]=true",
                "automatic_payment_methods[allow_redirects]=never",
            ).joinToString("&")

        return try {
            val response =
                restClient.post()
                    .uri("https://api.stripe.com/v1/payment_intents")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${stripeProperties.secretKey}")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .toEntity(OrderResponse::class.java)

            response.body
        } catch (e: Exception) {
            throw IllegalArgumentException("Stripe error: ${e.message}")
        }
    }
}
