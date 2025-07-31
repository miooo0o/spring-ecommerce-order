package ecommerce.dto

class RegisteredMember(
    val id: Long,
    val email: String,
    val role: Role,
)

enum class Role {
    ADMIN,
    USER,
}
