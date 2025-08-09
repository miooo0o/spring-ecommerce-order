package ecommerce.controller

import ecommerce.annotation.LoginMember
import ecommerce.dto.CartItemRequest
import ecommerce.dto.CartItemResponse
import ecommerce.dto.CartResponse
import ecommerce.dto.RegisteredMember
import ecommerce.model.mapper.CartItemMapper
import ecommerce.service.CartService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/cart")
class CartController(private val cartService: CartService) {
    @GetMapping
    fun getAllItems(
        @LoginMember member: RegisteredMember,
    ): ResponseEntity<CartResponse> {
        val cart = cartService.findCart(member.id)
        val body = CartResponse(cart.id, cart.items.map { CartItemMapper.toResponse(it) })
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @GetMapping("/wishlist")
    fun getAllItemsAsPages(
        @LoginMember member: RegisteredMember,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): Page<CartItemResponse> {
        return cartService.getPages(member.id, page, size)
    }

    @PostMapping
    fun addItem(
        @LoginMember member: RegisteredMember,
        @RequestBody request: CartItemRequest,
    ): ResponseEntity<CartItemResponse> {
        val cartItem = cartService.addItem(member.id, request)
        val response = CartItemMapper.toResponse(cartItem)
        return ResponseEntity.created(URI("/api/${cartItem.cart.id}/items/${response.productId}")).body(response)
    }

    @DeleteMapping
    fun deleteItem(
        @LoginMember member: RegisteredMember,
        @RequestBody request: CartItemRequest,
    ): ResponseEntity<Void> {
        cartService.deleteItem(member.id, request)
        return ResponseEntity.noContent().build()
    }
}
