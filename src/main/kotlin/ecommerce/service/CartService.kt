package ecommerce.service

import ecommerce.dto.AllCartItemsResponse
import ecommerce.dto.CartItemRequest
import ecommerce.dto.CartItemResponse
import ecommerce.exception.NotFoundException
import ecommerce.repository.CartRepository
import ecommerce.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) {
    fun getAllItems(memberId: Long): AllCartItemsResponse {
        val cartId = cartRepository.findOrCreateCartId(memberId)
        val items = cartRepository.showAllItemsInCart(cartId)
        return AllCartItemsResponse(cartId, items)
    }

    fun addItem(
        memberId: Long,
        request: CartItemRequest,
    ): CartItemResponse {
        val cartId = cartRepository.findOrCreateCartId(memberId)
        if (!productRepository.existsById(request.productId)) {
            throw NotFoundException("Product with ${request.productId} not found")
        }
        return cartRepository.addItemToCart(request.productId, request.quantity, cartId)
    }

    fun deleteItem(
        memberId: Long,
        request: CartItemRequest,
    ): CartItemResponse? {
        val cartId = cartRepository.findOrCreateCartId(memberId)
        if (!productRepository.existsById(request.productId)) {
            throw NotFoundException("Product with ${request.productId} not found")
        }
        return cartRepository.removeItemFromCart(request.productId, request.quantity, cartId)
    }
}
