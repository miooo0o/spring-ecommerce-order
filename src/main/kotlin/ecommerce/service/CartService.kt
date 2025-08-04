package ecommerce.service

import ecommerce.dto.CartItemRequest
import ecommerce.exception.NotFoundException
import ecommerce.model.Cart
import ecommerce.model.CartItem
import ecommerce.repository.CartItemRepository
import ecommerce.repository.CartRepository
import ecommerce.repository.MemberRepository
import ecommerce.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
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
        val product =
            productRepository.findById(request.productId).orElseThrow { NotFoundException() } // TODO: change exception

        val cart =
            cartRepository.findCartByMemberId(memberId)
                ?: cartRepository.save(Cart(member))

        val item = cart.addItem(product, request.quantity)
        cartRepository.save(cart) // TODO: check if we need to save
        return item
    }

    fun deleteItem(
        memberId: Long,
        request: CartItemRequest,
    ) {
        val member = memberRepository.findById(memberId).orElseThrow { NotFoundException() } // TODO: change exception
        val product =
            productRepository.findById(request.productId).orElseThrow { NotFoundException() } // TODO: change exception

        val cart =
            cartRepository.findCartByMemberId(memberId)
                ?: cartRepository.save(Cart(member))

        cart.removeItem(product, request.quantity)
        cartRepository.save(cart)
    }
}
