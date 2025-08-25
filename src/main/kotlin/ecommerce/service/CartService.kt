package ecommerce.service

import ecommerce.dto.CartItemRequest
import ecommerce.dto.CartItemResponse
import ecommerce.exception.CartProcessingException
import ecommerce.exception.NotFoundException
import ecommerce.model.Cart
import ecommerce.model.CartItem
import ecommerce.model.mapper.CartItemMapper
import ecommerce.repository.CartItemRepository
import ecommerce.repository.CartRepository
import ecommerce.repository.MemberRepository
import ecommerce.repository.OptionRepository
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CartService(
    private val cartRepository: CartRepository,
    private val memberRepository: MemberRepository,
    private val cartItemRepository: CartItemRepository,
    private val optionRepository: OptionRepository,
    private val entityManager: EntityManager,
) {
    fun findCart(memberId: Long): Cart {
        return cartRepository.findCartByMemberId(memberId)
            ?: throw RuntimeException("not found something ... ")
    }

    fun addItem(
        memberId: Long,
        request: CartItemRequest,
    ): CartItem {
        val cart = getOrCreateCartByMemberId(memberId)
        val option =
            optionRepository.findById(request.optionId)
                .orElseThrow { NotFoundException("option not found") }

        val item = cart.addItem(option, request.quantity)
        cartRepository.save(cart)
        return item
    }

    fun deleteItem(
        memberId: Long,
        request: CartItemRequest,
    ) {
        val cart = getOrCreateCartByMemberId(memberId)
        val option =
            optionRepository.findById(request.optionId)
                .orElseThrow { NotFoundException("option not found") }

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

    private fun getOrCreateCartByMemberId(memberId: Long): Cart {
        val member =
            memberRepository.findById(memberId)
                .orElseThrow { NotFoundException("member not found") }
        return cartRepository.findCartByMemberId(memberId)
            ?: cartRepository.save(Cart(member))
    }

    fun getCartForOrder(memberId: Long): Cart {
        val cart =
            cartRepository.findCartByMemberId(memberId)
                ?: throw NotFoundException("Can not find cart by memberId: $memberId")

        validateCartOrThrow(cart)
        return cart
    }

    private fun validateCartOrThrow(cart: Cart) {
        val errors = validateCart(cart)
        if (errors.isNotEmpty()) {
            throw CartProcessingException("Cart validation failed", errors)
        }
    }

    private fun validateCart(cart: Cart): List<String> {
        val errors = mutableListOf<String>()

        if (cart.items.isEmpty()) errors.add("Cart items is empty.")

        cart.items.forEach { item ->
            if (item.quantity <= 0) {
                errors.add("Invalid quantity: ${item.quantity}. Quantity must be greater than 0.")
            }
            if (item.option.availableStock < item.quantity) {
                errors.add(
                    "${item.product.name}: Not enough stock available. " +
                        "Only ${item.option.availableStock} left, but ${item.quantity} requested.",
                )
            }
        }
        return errors
    }

    fun clearCart(memberId: Long) {
        val cart = getOrCreateCartByMemberId(memberId)
        cart.items.forEach { cartItem ->
            entityManager.remove(cartItem)
        }
        cart.clear()
    }
}
