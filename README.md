# spring-ecommerce-order

## Step 1-1 - Entity Mapping

Transform Repository and entities using Spring Data JPA.

### Feature List

- [ ] Product -> `Product @Entity`, `ProductModel` ...
- [ ] ProductRepository
    - [ ] `ProductRepository(private val jdbcTemplate)` -> `interface ProductRepository : JpaRepository<Product, Long>`
    - [ ] rename `insert` to `insertWithKeyholder`
    - [ ] remove `update` ->
    - [ ] refactor `delete` with JPA method
    - [ ] refactor `existsByName` with JPA method
    - [ ] refactor `existsById` with JPA method
    - [ ] refactor `findById` with JPA method


## Considerations

- [ ] remove Boolean return type from all delete methods
- [ ] change Double to BigDecimal inside the Entity (Product/price)
- [ ] decide on where and how to use Models