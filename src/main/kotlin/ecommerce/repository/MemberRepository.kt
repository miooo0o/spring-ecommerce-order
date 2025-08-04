package ecommerce.repository

import ecommerce.model.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
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
