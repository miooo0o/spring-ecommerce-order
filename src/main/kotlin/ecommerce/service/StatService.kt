package ecommerce.service

import ecommerce.dto.MemberStatsResponse
import ecommerce.dto.ProductStatsResponse
import ecommerce.repository.CartItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@Service
class StatService(
    private val cartItemRepository: CartItemRepository,
) {
    fun getActiveMembersInThePast7Days(): List<MemberStatsResponse> {
        val threshold = LocalDateTime.now().minusDays(7)

        val cartItems = cartItemRepository.findDistinctByCreatedAtAfter(threshold)

        val members =
            cartItems
                .map { it.cart.member }
                .distinctBy { it.id }

        return members.map { MemberStatsResponse(it.id!!, it.email, it.name) }
    }

    fun getTop5ProductsInThePast30Days(): List<ProductStatsResponse> {
        val threshold = LocalDateTime.now().minusDays(30)
        val items = cartItemRepository.findDistinctByUpdatedAtAfter(threshold)
        return items
            .groupBy { it.product }
            .map { (product, list) ->
                ProductStatsResponse(
                    productName = product.name,
                    productQuantity = list.sumOf { it.quantity },
                    mostRecent = list.maxOf { it.createdAt ?: LocalDateTime.MIN },
                )
            }
            .sortedWith(
                compareByDescending<ProductStatsResponse> { it.productQuantity }
                    .thenByDescending { it.mostRecent },
            )
            .take(5)
    }
}
