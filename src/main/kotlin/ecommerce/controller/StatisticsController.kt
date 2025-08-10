package ecommerce.controller

import ecommerce.annotation.Admin
import ecommerce.dto.MemberStatsResponse
import ecommerce.dto.ProductStatsResponse
import ecommerce.dto.RegisteredMember
import ecommerce.service.StatService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/statistics")
class StatisticsController(private val statService: StatService) {
    @GetMapping("/top-products")
    fun topProducts(
        @Admin admin: RegisteredMember,
    ): ResponseEntity<List<ProductStatsResponse>> {
        val stats = statService.getTop5ProductsInThePast30Days()
        return ResponseEntity.ok(stats)
    }

    @GetMapping("/active-members")
    fun activeMembers(
        @Admin admin: RegisteredMember,
    ): ResponseEntity<List<MemberStatsResponse>> {
        val stats = statService.getActiveMembersInThePast7Days()
        return ResponseEntity.ok(stats)
    }
}
