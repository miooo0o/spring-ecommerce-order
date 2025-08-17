package ecommerce.controller

import ecommerce.annotation.LoginMember
import ecommerce.dto.OrderRequest
import ecommerce.dto.RegisteredMember
import ecommerce.service.OrderService
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/order")
@Controller
class OrderController(private val orderService: OrderService) {
    @Transactional
    fun placeOrder(
        @LoginMember member: RegisteredMember,
        @Valid request: OrderRequest,
    ) {
        orderService.processOrder(member.id, request)
    }
}
