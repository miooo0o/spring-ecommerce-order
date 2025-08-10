package ecommerce

import ecommerce.dto.Role
import ecommerce.model.Member
import ecommerce.model.Option
import ecommerce.model.Product

object TestFixture {
    val MINA =
        Member(
            email = "mina@mail.com",
            name = "Mina Kim",
            password = "ILoveMyDog!",
            role = Role.USER.name,
        )

    fun createMina(): Member =
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

    fun createJin(): Member =
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

    fun createPetra(): Member =
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

    fun createAdmin(): Member =
        Member(
            email = "admin@mail.com",
            name = "Boss",
            password = "IAmAdmin!",
            role = Role.ADMIN.name,
        )

    val BRUSH =
        Product(
            name = "Brush",
            price = 5.99,
            imageUrl = "https://example.com/images/brush.jpg",
        )

    fun createBrush(): Product =
        Product(
            name = "Brush",
            price = 5.99,
            imageUrl = "https://example.com/images/brush.jpg",
        )

    fun createBrushWithOptions(): Product {
        val product =
            Product(
                name = "Brush",
                price = 5.99,
                imageUrl = "https://example.com/images/brush.jpg",
            )
        product.addOptions(
            listOf(
                Option("small", 3),
                Option("medium", 1),
                Option("large", 2),
            ),
        )
        return product
    }

    fun createCanvas(): Product =
        Product(
            name = "Canvas",
            price = 8.50,
            imageUrl = "https://example.com/images/canvas.jpg",
        )

    fun createPalette(): Product =
        Product(
            name = "Palette",
            price = 6.25,
            imageUrl = "https://example.com/images/palette.jpg",
        )

    fun createAcrylics(): Product =
        Product(
            name = "Acrylics",
            price = 15.00,
            imageUrl = "https://example.com/images/acrylics.jpg",
        )

    val PAINTING_SAD_HUMAN =
        Product(
            name = "Sad human and Cute dog",
            price = 3000.00,
            imageUrl = "https://picsum.d-o-go",
        )

    fun createPaintingSadHuman(): Product =
        Product(
            name = "Sad human and Cute dog",
            price = 3000.00,
            imageUrl = "https://picsum.d-o-go",
        )

    fun createPaintingHappyHuman(): Product =
        Product(
            name = "Happy human and Cute dog",
            price = 19000.00,
            imageUrl = "https://picsum.d-o-go",
        )

    fun createPen(): Product =
        Product(
            name = "Pen",
            price = 1.50,
            imageUrl = "https://example.com/images/pen.jpg",
        )

    fun createPencil(): Product =
        Product(
            name = "Pencil",
            price = 1.00,
            imageUrl = "https://example.com/images/pencil.jpg",
        )
}
