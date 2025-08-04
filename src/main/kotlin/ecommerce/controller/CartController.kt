package ecommerce.controller

import ecommerce.annotation.LoginMember
import ecommerce.dto.AllCartItemsResponse
import ecommerce.dto.CartItemRequest
import ecommerce.dto.CartItemResponse
import ecommerce.dto.RegisteredMember
import ecommerce.service.CartService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/cart")
class CartController(private val cartService: CartService) {
    @GetMapping
    fun getAllItems(
        @LoginMember member: RegisteredMember,
    ): ResponseEntity<AllCartItemsResponse> {
        val cart = cartService.findCart(member.id)
        val body = AllCartItemsResponse(cart.id, cart.items.map { it.toResponse() })
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @PostMapping
    fun addItem(
        @LoginMember member: RegisteredMember,
        @RequestBody request: CartItemRequest,
    ): ResponseEntity<CartItemResponse> {
        val response = cartService.addItem(member.id, request).toResponse()
        return ResponseEntity.created(URI("/api/cart/items/${response.productId}")).body(response)
    }
//
//    @DeleteMapping
//    fun deleteItem(
//        @LoginMember member: RegisteredMember,
//        @RequestBody request: CartItemRequest,
//    ): ResponseEntity<CartItemResponse?> {
//
//        return ResponseEntity.ok().body(response)
//    }
}
