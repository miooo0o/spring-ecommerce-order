package ecommerce.model

import ecommerce.dto.RegisteredMember
import ecommerce.dto.Role
import ecommerce.exception.UnauthorizedException

class Member(
    val id: Long? = null,
    val email: String,
    val name: String = "",
    val password: String,
    val role: String = Role.USER.toString(),
) {
    fun validatePassword(password: String) {
        if (this.password != password) {
            throw UnauthorizedException("Incorrect password")
        }
    }

    fun toMemberDto(): RegisteredMember {
        val role = Role.valueOf(this.role)
        return RegisteredMember(this.id!!, email, role)
    }

    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as Member

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: System.identityHashCode(this)
    }
}
