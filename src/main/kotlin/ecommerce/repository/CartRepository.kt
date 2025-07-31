package ecommerce.repository

import ecommerce.dto.CartItemResponse
import ecommerce.exception.NotFoundException
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class CartRepository(private val jdbcClient: JdbcClient) {
    fun addItemToCart(
        productId: Long,
        productQuantity: Long,
        cartId: Long,
    ): CartItemResponse {
        val updateSql =
            """
            UPDATE cart_items
            SET quantity = quantity + ?, created_at = CURRENT_TIMESTAMP
            WHERE product_id = ? AND cart_id = ?
            """.trimIndent()

        val insertSql =
            """
            INSERT INTO cart_items (cart_id, product_id, quantity)
            VALUES (?, ?, ?)
            """.trimIndent()

        val rowsUpdated =
            jdbcClient
                .sql(updateSql)
                .params(productQuantity, productId, cartId)
                .update()

        if (rowsUpdated == 0) {
            // No existing row, insert new one
            val rowsInserted =
                jdbcClient
                    .sql(insertSql)
                    .params(cartId, productId, productQuantity)
                    .update()

            if (rowsInserted == 0) {
                throw NotFoundException("Failed to add: Product $productId not found")
            }
        }

        return showItem(cartId, productId)
            ?: throw NotFoundException("Failed to add: Product $productId not found in cart")
    }

    fun removeItemFromCart(
        productId: Long,
        quantity: Long,
        cartId: Long,
    ): CartItemResponse? {
        val currentQuantity = quantityInCart(productId, cartId)
        var updateQuantity: Long = quantity
        if (currentQuantity - updateQuantity < 0) {
            updateQuantity = -1 * currentQuantity
        }

        if (currentQuantity > updateQuantity) {
            decreaseItemQuantity(productId, updateQuantity, cartId)
            return showItem(cartId, productId)
                ?: throw NotFoundException("Failed to delete: Product $productId not found in cart $cartId")
        } else {
            decreaseItemQuantity(productId, updateQuantity, cartId)
            return null
        }
    }

    private fun decreaseItemQuantity(
        productId: Long,
        quantity: Long,
        cartId: Long,
    ) {
        val sql =
            """
            UPDATE cart_items
            SET quantity = quantity - ?
            WHERE product_id = ? AND cart_id = ?
            """.trimIndent()

        val rowsAffected =
            jdbcClient
                .sql(sql)
                .params(quantity, productId, cartId)
                .update()

        if (rowsAffected == 0) {
            throw NotFoundException("Failed to delete: product $productId not found in cart $cartId")
        }
    }

    private fun deleteItemRow(
        productId: Long,
        cartId: Long,
    ) {
        val sql =
            """
            DELETE FROM cart_items
            WHERE product_id = ? AND cart_id = ?
            """.trimIndent()

        val rowsAffected =
            jdbcClient
                .sql(sql)
                .param(1, productId)
                .param(2, cartId)
                .update()

        if (rowsAffected == 0) {
            throw NotFoundException("Failed to delete: product $productId not found in cart $cartId")
        }
    }

    fun quantityInCart(
        productId: Long,
        cartId: Long,
    ): Long {
        val sql = "SELECT quantity FROM cart_items WHERE product_id = ? AND cart_id = ?"
        val quantity =
            jdbcClient
                .sql(sql)
                .params(productId, cartId)
                .query(Long::class.java)
                .optional()
                .orElse(0L)
        return quantity
    }

    fun findOrCreateCartId(userId: Long): Long {
        val sql = "SELECT cart_id FROM carts WHERE user_id = ?"
        val foundId: Long? =
            jdbcClient
                .sql(sql)
                .param(1, userId)
                .query(Long::class.java)
                .optional()
                .orElse(null)

        if (foundId != null) return foundId

        val insertSql = "INSERT INTO carts (user_id) VALUES (?)"
        jdbcClient
            .sql(insertSql)
            .param(1, userId)
            .update()

        val cartId =
            jdbcClient
                .sql("SELECT cart_id FROM carts WHERE user_id = ?")
                .param(1, userId)
                .query(Long::class.java)
                .single()

        return cartId
    }

    fun showAllItemsInCart(cartId: Long): List<CartItemResponse> {
        val sql =
            """
            SELECT
                ci.quantity,
                p.id AS product_id,
                p.name AS product_name,
                p.price AS product_price,
                p.image_url AS product_image_url
            FROM cart_items ci
            INNER JOIN products p ON ci.product_id = p.id
            WHERE ci.cart_id = ?
            """.trimIndent()
        val cartItems =
            jdbcClient
                .sql(sql)
                .param(1, cartId)
                .query(CartItemResponse::class.java)
                .list()
        return cartItems
    }

    fun showItem(
        cartId: Long,
        productId: Long,
    ): CartItemResponse? {
        val joinedSql =
            """
            SELECT 
                ci.quantity,
                ci.product_id,
                p.name AS product_name,
                p.price AS product_price,
                p.image_url AS product_image_url
            FROM cart_items ci
            JOIN products p ON ci.product_id = p.id
            WHERE ci.cart_id = ? AND ci.product_id = ?
            """.trimIndent()

        return jdbcClient
            .sql(joinedSql)
            .params(cartId, productId)
            .query(CartItemResponse::class.java)
            .optional()
            .orElse(null)
    }
}
