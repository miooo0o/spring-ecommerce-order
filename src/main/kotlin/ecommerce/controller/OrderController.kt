package ecommerce.controller

import ecommerce.annotation.LoginMember
import ecommerce.dto.OrderRequest
import ecommerce.dto.OrderResponse
import ecommerce.dto.RegisteredMember
import ecommerce.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @PostMapping
    fun createOrder(
        @LoginMember member: RegisteredMember,
        request: OrderRequest,
    ): ResponseEntity<OrderResponse> {
        val response = orderService.createOrder(member.id, request)
        return ResponseEntity.created(URI("/api/order/${response.orderId}")).body(response)
    }
//
//    @GetMapping
//    fun showOrder(
//        @LoginMember member: RegisteredMember,
//    ): ResponseEntity<OrderResponse> {
//    }
}
