package ecommerce.service

import ecommerce.dto.CartItemRequest
import ecommerce.exception.NotFoundException
import ecommerce.model.Cart
import ecommerce.model.CartItem
import ecommerce.repository.CartItemRepositoryJPA
import ecommerce.repository.CartRepositoryJPA
import ecommerce.repository.MemberRepositoryJPA
import ecommerce.repository.ProductRepositoryJPA
import org.springframework.stereotype.Service

@Service
class CartServiceJPA(
    private val cartRepository: CartRepositoryJPA,
    private val cartItemRepository: CartItemRepositoryJPA,
    private val productRepository: ProductRepositoryJPA,
    private val memberRepository: MemberRepositoryJPA,
) {
    fun findCart(memberId: Long): Cart {
        return cartRepository.findCartByMemberId(memberId)
            ?: throw RuntimeException("not found something ... ") // TODO: change exception
    }

    fun addItem(
        memberId: Long,
        request: CartItemRequest,
    ): CartItem {
        val member = memberRepository.findById(memberId).orElseThrow { NotFoundException() } // TODO: change exception
        val product = productRepository.findById(request.productId).orElseThrow { NotFoundException() } // TODO: change exception

        val cart =
            cartRepository.findCartByMemberId(memberId)
                ?: cartRepository.save(Cart(member))

        val cartItem = CartItem(product, cart, request.quantity)
        cart.addItem(cartItem)
        cartItemRepository.save(cartItem) // TODO: chage if we need to save
        return cartItem
    }
}
