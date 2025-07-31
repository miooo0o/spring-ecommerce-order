package ecommerce.repository

import ecommerce.model.Member
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class MemberRepository(private val jdbcTemplate: JdbcTemplate) {
    private val memberRowMapper =
        RowMapper { rs, _ ->
            Member(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("name"),
                rs.getString("password"),
                rs.getString("role"),
            )
        }

    fun registerMember(member: Member): Boolean {
        val sql = "INSERT INTO members (email, name, password, role) VALUES (?, ?, ?, ?)"
        val rowsAffected = jdbcTemplate.update(sql, member.email, member.name, member.password, member.role)
        return rowsAffected > 0
    }

    fun existsByEmail(email: String): Boolean {
        val sql = "SELECT COUNT(*) FROM members where email = ?"
        val found = jdbcTemplate.queryForObject(sql, Int::class.java, email) ?: 0
        return found > 0
    }

    fun findByEmail(email: String): Member? {
        val sql = "SELECT * FROM members where email = ?"
        val member = jdbcTemplate.query(sql, memberRowMapper, email).firstOrNull()
        return member
    }
}
