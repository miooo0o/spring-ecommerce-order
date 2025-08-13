package ecommerce.service

import ecommerce.dto.CartItemRequest
import ecommerce.dto.CartItemResponse
import ecommerce.exception.NotFoundException
import ecommerce.model.Cart
import ecommerce.model.CartItem
import ecommerce.model.mapper.CartItemMapper
import ecommerce.repository.CartItemRepository
import ecommerce.repository.CartRepository
import ecommerce.repository.MemberRepository
import ecommerce.repository.OptionRepository
import ecommerce.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CartService(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val cartItemRepository: CartItemRepository,
    private val optionRepository: OptionRepository,
) {
    fun findCart(memberId: Long): Cart {
        return cartRepository.findCartByMemberId(memberId)
            ?: throw RuntimeException("not found something ... ")
    }

    fun addItem(
        memberId: Long,
        request: CartItemRequest,
    ): CartItem {
        val member = memberRepository.findById(memberId).orElseThrow { NotFoundException("member not found") }
        val option = optionRepository.findById(request.optionId).orElseThrow { NotFoundException("option not found") }
        val cart =
            cartRepository.findCartByMemberId(memberId)
                ?: cartRepository.save(Cart(member))

        val item = cart.addItem(option, request.quantity)
        cartRepository.save(cart)
        return item
    }

    fun deleteItem(
        memberId: Long,
        request: CartItemRequest,
    ) {
        val member = memberRepository.findById(memberId).orElseThrow { NotFoundException("member not found") }
        val option = optionRepository.findById(request.optionId).orElseThrow { NotFoundException("option not found") }
        val cart =
            cartRepository.findCartByMemberId(memberId)
                ?: cartRepository.save(Cart(member))

        cart.removeItem(option)
        cartRepository.save(cart)
    }

    fun getPages(
        memberId: Long,
        page: Int,
        size: Int,
    ): Page<CartItemResponse> {
        val pageable = PageRequest.of(page, size, Sort.by("product.name"))
        return cartItemRepository
            .findByCartMemberId(memberId, pageable)
            .map(CartItemMapper::toResponse)
    }
}
