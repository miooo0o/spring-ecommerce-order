package ecommerce.repository

import ecommerce.model.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CartRepository : JpaRepository<Cart, Long> {
    fun findCartByMemberId(memberId: Long): Cart?

    @Query(GET_ALL_PROPERTIES,)
    fun findCartWithAllByMemberId(
        @Param("memberId") memberId: Long,
    ): Cart?

    companion object {
        private const val GET_ALL_PROPERTIES = """
              SELECT DISTINCT c
              FROM Cart c
              JOIN FETCH c.member m
              LEFT JOIN FETCH c.items i
              LEFT JOIN FETCH i.product p
              WHERE m.id = :memberId
            """
    }
}
