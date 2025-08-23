package ecommerce.controller

import ecommerce.annotation.LoginMember
import ecommerce.dto.CheckoutRequest
import ecommerce.dto.CheckoutResponse
import ecommerce.dto.RegisteredMember
import ecommerce.service.CheckoutService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/checkout")
@Controller
class CheckoutController(
    private val paymentService: CheckoutService,
) {
    @PostMapping("/")
    fun checkout(
        @LoginMember member: RegisteredMember,
        @RequestBody @Valid request: CheckoutRequest,
    ): ResponseEntity<CheckoutResponse> {
        val response = paymentService.checkout(member, request)
        return ResponseEntity.ok(response)
    }
}
