package ecommerce.repository

import ecommerce.model.Product
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ProductRepository(private val jdbcTemplate: JdbcTemplate) {
    private val productRowMapper =
        RowMapper<Product> { rs: ResultSet, _ ->
            Product(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("image_url"),
            )
        }

    fun findAllProducts(): List<Product> {
        val sql = "select id, name, price, image_url from products"
        val products: List<Product> = jdbcTemplate.query(sql, productRowMapper)
        return products
    }

    fun insert(product: Product): Boolean {
        val sql = "insert into products (name, price, image_url) values (?, ?, ?)"
        val rowsAffected = jdbcTemplate.update(sql, product.name, product.price, product.imageUrl)
        return rowsAffected > 0
    }

    fun insertWithKeyHolder(product: Product): Long {
        val sql = "insert into products (name, price, image_url) values (?, ?, ?)"

        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({
            it.prepareStatement(sql, arrayOf("id")).apply {
                setString(1, product.name)
                setDouble(2, product.price)
                setString(3, product.imageUrl)
            }
        }, keyHolder)

        return keyHolder.key!!.toLong()
    }

    fun update(
        product: Product,
        productId: Long,
    ): Boolean {
        val sql = "UPDATE products SET name = ?, price = ?, image_url = ? WHERE id = ?"
        val rowsAffected = jdbcTemplate.update(sql, product.name, product.price, product.imageUrl, productId)
        return rowsAffected > 0
    }

    fun delete(id: Long): Boolean {
        val rowsAffected = jdbcTemplate.update("delete from products where id = ?", id)
        return rowsAffected > 0
    }

    fun existsByName(name: String): Boolean {
        val sql = "select count(*) from products where name = ?"
        val count = jdbcTemplate.queryForObject(sql, Int::class.java, name) ?: 0
        return count > 0
    }

    fun existsById(id: Long): Boolean {
        val sql = "select count(*) from products where id = ?"
        val count = jdbcTemplate.queryForObject(sql, Int::class.java, id) ?: 0
        return count > 0
    }

    fun findById(id: Long): Product? {
        val sql = "select * from products where id = ?"
        return try {
            jdbcTemplate.queryForObject(sql, productRowMapper, id)
        } catch (e: Exception) {
            null
        }
    }
}
