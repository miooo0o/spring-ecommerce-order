package ecommerce

import ecommerce.dto.Role
import ecommerce.model.Member

object MemberTestFixture {
    fun createMina(): Member = MINA
    fun createPetra(): Member = PETRA
    fun createJin(): Member = JIN
    fun createAdmin(): Member = ADMIN
    val MINA =
        Member(
            email = "mina@mail.com",
            name = "Mina Kim",
            password = "ILoveMyDog!",
            role = Role.USER.name,
        )

    val JIN =
        Member(
            email = "jin@mail.com",
            name = "Jinnie",
            password = "Hello1234!",
            role = Role.USER.name,
        )

    val PETRA =
        Member(
            email = "petra@mail.com",
            name = "Petra Bencze",
            password = "MyPasswordIsLong123",
            role = Role.USER.name,
        )


    val ADMIN: Member =
        Member(
            email = "admin@mail.com",
            name = "Boss",
            password = "IAmAdmin!",
            role = Role.ADMIN.name,
        )
}
