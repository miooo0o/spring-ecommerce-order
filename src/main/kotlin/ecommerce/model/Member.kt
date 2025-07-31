package ecommerce.model

import ecommerce.dto.RegisteredMember
import ecommerce.dto.Role
import ecommerce.exception.UnauthorizedException
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

// TODO: email: String to email: Email

@Entity
@Table(name = "members")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "email", nullable = false)
    val email: String, // TODO: Email

    @Column(name = "name", nullable = false)
    val name: String = "",

    @Column(name = "password", nullable = false)
    val password: String,

    @Column(name = "role", nullable = false)
    val role: String = Role.USER.name,
) {
    fun validatePassword(password: String) {
        if (this.password != password) {
            throw UnauthorizedException("Incorrect password")
        }
    }

    // TODO: toDto should be here? if we have time
    fun toRegisteredMember(): RegisteredMember {
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
