package ecommerce.service

import ecommerce.dto.MemberStatsResponse
import ecommerce.dto.ProductStatsResponse
import ecommerce.repository.StatRepository
import org.springframework.stereotype.Service

@Service
class StatService(private val statRepository: StatRepository) {
    fun getActiveMembersInThePast7Days(): List<MemberStatsResponse> {
        return statRepository.getActiveMembersInThePast7Days()
    }

    fun getTop5ProductsInThePast30Days(): List<ProductStatsResponse> {
        return statRepository.getTop5ProductsInThePast30Days()
    }
}
