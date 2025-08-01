package ecommerce.repository

import ecommerce.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import java.sql.ResultSet

interface ProductRepositoryJPA : JpaRepository<Product, Long> {
    fun existsByName(name: String): Boolean
}

interface ProductRepository {
    fun findAll(): List<Product>

    fun save(entity: Product): Product

    fun deleteById(id: Long): Boolean

    fun existsByName(name: String): Boolean

    fun existsById(id: Long): Boolean

    fun findById(id: Long): Product?
}

@Repository
class ProductRepositoryJDBC(private val jdbcTemplate: JdbcTemplate) : ProductRepository {
    private val productRowMapper =
        RowMapper<Product> { rs: ResultSet, _ ->
            Product(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("image_url"),
            )
        }

    override fun findAll(): List<Product> {
        val sql = "select id, name, price, image_url from products"
        val products: List<Product> = jdbcTemplate.query(sql, productRowMapper)
        return products
    }

    override fun save(product: Product): Product {
        val productId = updateDataAndReturnId(product)
        return findById(productId)
            ?: throw RuntimeException("Product with id $productId not found")
    }

    private fun updateDataAndReturnId(product: Product): Long {
        if (product.id == null) {
            val sql = "insert into products (name, price, image_url) values (?, ?, ?)"
            return getIdFromDatabase(product, sql)
        } else {
            // update
            val sql = "UPDATE products SET name = ?, price = ?, image_url = ? WHERE id = ?"
            jdbcTemplate.update(sql, product.name, product.price, product.imageUrl, product.id!!)
            return product.id!!
        }
    }

    private fun getIdFromDatabase(
        product: Product,
        sql: String,
    ): Long {
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

//    override fun update(
//        product: Product,
//        productId: Long,
//    ): Boolean {
//        val sql = "UPDATE products SET name = ?, price = ?, image_url = ? WHERE id = ?"
//        val rowsAffected = jdbcTemplate.update(sql, product.name, product.price, product.imageUrl, productId)
//        return rowsAffected > 0
//    }

    override fun deleteById(id: Long): Boolean {
        val rowsAffected = jdbcTemplate.update("delete from products where id = ?", id)
        return rowsAffected > 0
    }

    override fun existsByName(name: String): Boolean {
        val sql = "select count(*) from products where name = ?"
        val count = jdbcTemplate.queryForObject(sql, Int::class.java, name) ?: 0
        return count > 0
    }

    override fun existsById(id: Long): Boolean {
        val sql = "select count(*) from products where id = ?"
        val count = jdbcTemplate.queryForObject(sql, Int::class.java, id) ?: 0
        return count > 0
    }

    override fun findById(id: Long): Product? {
        val sql = "select * from products where id = ?"
        return try {
            jdbcTemplate.queryForObject(sql, productRowMapper, id)
        } catch (e: Exception) {
            null
        }
    }
}
