package ecommerce.repository

import ecommerce.model.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder

interface MemberRepositoryJPA : JpaRepository<Member, Long> {
    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): Member?
}

interface MemberRepository {
    fun save(member: Member): Member

    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): Member?

    fun findById(id: Long): Member?
}

// @Repository
class MemberRepositoryJDBC(private val jdbcTemplate: JdbcTemplate) : MemberRepository {
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

    override fun save(entity: Member): Member {
        val memberId = updateDataAndReturnId(entity)
        return findById(memberId)
            ?: throw RuntimeException("Member with id ${entity.id} not found")
    }

    override fun existsByEmail(email: String): Boolean {
        val sql = "SELECT EXISTS(SELECT 1 FROM members WHERE email = ?)"
        val found = jdbcTemplate.queryForObject(sql, Int::class.java, email) ?: 0
        return found > 0
    }

    override fun findByEmail(email: String): Member? {
        val sql = "SELECT * FROM members where email = ?"
        val member = jdbcTemplate.query(sql, memberRowMapper, email).firstOrNull()
        return member
    }

    private fun updateDataAndReturnId(member: Member): Long {
        if (member.id == null) {
            val sql = "INSERT INTO members (email, name, password, role) VALUES (?, ?, ?, ?)"
            return getIdFromDatabase(member, sql)
        } else {
            val sql = "UPDATE members SET email = ?, name = ?, password = ?, role = ? WHERE id = ?"
            jdbcTemplate.update(sql, member.email, member.name, member.password, member.role)
            return member.id!!
        }
    }

    private fun getIdFromDatabase(
        member: Member,
        sql: String,
    ): Long {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({
            it.prepareStatement(sql, arrayOf("id")).apply {
                setString(1, member.email)
                setString(2, member.name)
                setString(3, member.password)
                setString(4, member.role)
            }
        }, keyHolder)
        return keyHolder.key!!.toLong()
    }

    override fun findById(id: Long): Member? {
        val sql = "select * from members where id = ?"
        return try {
            jdbcTemplate.queryForObject(sql, memberRowMapper, id)
        } catch (e: Exception) {
            null
        }
    }
}
