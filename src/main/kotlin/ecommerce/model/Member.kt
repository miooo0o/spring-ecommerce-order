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

@Entity
@Table(name = "members")
class Member(
    @Column(name = "email", nullable = false)
    val email: String,
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Column(name = "password", nullable = false)
    var password: String,
    @Column(name = "role", nullable = false)
    val role: String = Role.USER.name,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    fun validatePassword(password: String) {
        if (this.password != password) {
            throw UnauthorizedException("Incorrect password")
        }
    }

    fun toRegisteredMember(): RegisteredMember {
        val role = Role.valueOf(this.role)
        return RegisteredMember(this.id, email, role)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as Member

        return id != 0L && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
