package ecommerce.repository

import ecommerce.dto.MemberStatsResponse
import ecommerce.dto.ProductStatsResponse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class StatRepository(private val jdbcTemplate: JdbcTemplate) {
    fun getActiveMembersInThePast7Days(): List<MemberStatsResponse> {
        val query = """
            SELECT DISTINCT
                m.id AS member_id,
                m.email,
                m.name
            FROM
                cart_items ci
            JOIN
                carts c ON ci.cart_id = c.cart_id
            JOIN
                members m ON c.user_id = m.id
            WHERE
                ci.created_at >= DATEADD('DAY', -7, CURRENT_DATE);
        """

        return jdbcTemplate.query(query) { rs, _ ->
            MemberStatsResponse(
                memberId = rs.getLong("member_id"),
                email = rs.getString("email"),
                name = rs.getString("name") ?: "",
            )
        }
    }

    fun getTop5ProductsInThePast30Days(): List<ProductStatsResponse> {
        val query = """
            SELECT
                p.name AS product_name,
                COUNT(ci.quantity) AS product_quantity,
                MAX(ci.created_at) AS most_recent
            FROM
                cart_items ci
            JOIN
                products p ON ci.product_id = p.id
            WHERE
                ci.created_at >= DATEADD('DAY', -30, CURRENT_DATE)
            GROUP BY
                p.name
            ORDER BY
                product_quantity DESC,
                most_recent DESC
            LIMIT 5;
        """

        return jdbcTemplate.query(query) { rs, _ ->
            ProductStatsResponse(
                productName = rs.getString("product_name"),
                productQuantity = rs.getLong("product_quantity"),
                mostRecent = rs.getTimestamp("most_recent").toLocalDateTime(),
            )
        }
    }
}
