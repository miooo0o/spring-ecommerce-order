package ecommerce

import ecommerce.dto.PendingOption
import ecommerce.dto.PendingProduct

object ProductFixture {
    val PENDING_PRODUCT_PAINTING = PendingProduct(
        name = "Painting",
        price = 400000L,
        imageUrl = "https://dummy_painting.com/500x500.png"
    )

    val PENDING_PRODUCT_BRUSH = PendingProduct(
        name = "Brush",
        price = 1000L,
        imageUrl = "https://dummy_brush.com/500x500.png"
    )

    val PENDING_PRODUCT_PAINT = PendingProduct(
        name = "Paint",
        price = 4000L,
        imageUrl = "https://dummy_paint.com/500x500.png"
    )
}

object OptionFixture {
    val PENDING_OPTION_HAPPY_DOG = listOf(
        PendingOption(
            name = "Happy dog loves walking near water",
            quantity = 10,
            optionPrice = 10000L
        )
    )

    val PENDING_OPTION_DUMMY = listOf(
        PendingOption(
            name = "Test Dummy: I hate test",
            quantity = 5,
            optionPrice = 100L
        )
    )

    val PENDING_SIZE_OPTIONS = setOf(
        PendingOption(name = "S", quantity = 10),
        PendingOption(name = "M", quantity = 10),
        PendingOption(name = "L", quantity = 10),
    ).toList()

    val PENDING_COLOR_OPTIONS = setOf(
        PendingOption(name = "Red", quantity = 10),
        PendingOption(name = "Black", quantity = 10),
        PendingOption(name = "Zink White", quantity = 5),
    ).toList()


}