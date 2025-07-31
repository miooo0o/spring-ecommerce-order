package ecommerce.controller

import ecommerce.annotation.LoginMember
import ecommerce.dto.AllCartItemsResponse
import ecommerce.dto.CartItemRequest
import ecommerce.dto.CartItemResponse
import ecommerce.dto.RegisteredMember
import ecommerce.service.CartService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/cart")
class CartController(private val cartService: CartService) {
    @GetMapping
    fun getAllItems(
        @LoginMember member: RegisteredMember,
    ): ResponseEntity<AllCartItemsResponse> {
        val allCartItemsResponse = cartService.getAllItems(member.id)
        return ResponseEntity.status(HttpStatus.OK).body(allCartItemsResponse)
    }

    @PostMapping
    fun addItem(
        @LoginMember member: RegisteredMember,
        @RequestBody request: CartItemRequest,
    ): ResponseEntity<CartItemResponse> {
        val cartItem = cartService.addItem(member.id, request)
        val response = cartItem.toResponse()
        return ResponseEntity.created(URI("/api/cart/items/${response.productId}")).body(response)
    }

    @DeleteMapping
    fun deleteItem(
        @LoginMember member: RegisteredMember,
        @RequestBody request: CartItemRequest,
    ): ResponseEntity<CartItemResponse?> {
        val cartItem = cartService.deleteItem(member.id, request)
        if (cartItem == null) {
            return ResponseEntity.noContent().build()
        }
        val response = cartItem.toResponse()
        return ResponseEntity.ok().body(response)
    }
}
