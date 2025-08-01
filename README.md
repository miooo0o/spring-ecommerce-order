# spring-ecommerce-order

## Step 1-1 - Entity Mapping
Goal: Transform Repository and entities using Spring Data JPA.

### Feature List

- [x] **Transform Models into Entities**
  - [x] Product -> `Product @Entity`
  - [x] Member -> `Member @Entity`
  - [x] CartItem -> `CartItem @Entity`
  - [x] Cart -> `Cart @Entity`
- [ ] **Decide on the relationships between databases**
  - [x] Product
  - [x] Member
  - [x] CartItem
    - [x] A CartItem should reference a Product via @ManyToOne, not @OneToOne. Multiple CartItems across different carts may reference the same Product.
  - [x] Cart
- [ ] **Transform Repositories to JpaRepositories**
  - [ ] Product
    - [ ] Rename repo to JdbcProductRepository to `JdbcProductRepository(private val jdbcTemplate)`
    - [ ] Create `interface ProductRepository`
    - [ ] Add `override` keyword before functions in JdbcProductRepository
    - [ ] Add functions to interface
    - [ ] Run tests
    - [ ] Change return type of functions to Jpa style
    - [ ] Create `interface JpaProductRepository : ProductRepository, JpaProductRepository<Product, Long>`
    - [ ] Remove `@Repository` keyword -> comment out!!
    - [ ] Run tests
  - [ ] Member
  - [ ] Cart

- [ ] **Refactor Services**
  - [ ] extract business logic to Entities -> most validation SHOULD happen inside the Entities!!
    - [ ] e.g. addOption()

#### Tests
- [ ] Refactor all tests to be less heavy
  - [ ] Create companion objects with constant values
- [ ] Use @Transactional @SpringBootTest  for testing  is still small -> use @ExtendWith(MockitoExtension::class)
  - [ ] @Mock repos (lateinit)
  - [ ] private service (lateinit)
  - [ ] @BeforeEach setup -> init
  - [ ] Use given() from Mockito
- [ ] Optional, but effective: Create InMemory fake repos -> service uses the fake repos
- [ ] 


## Step 1-2 - Pagination
Goal: Implement pagination for both the product list and the wishlist view.

Most web applications do not display all data at once. Instead, content is split into multiple pages.
Pagination allows users to define how data should be sorted, how many items are shown per page, and which page number to retrieve.
Sorting can also be used to prioritize which data appears first.
Spring Data provides a convenient object called `Pageable`.

It also supports several return types:

- `List<T>` – regular list, no pagination metadata
- `Slice<T>` – supports page requests, but not total count
- `Page<T>` – full pagination with total count, total pages, current page, etc.

## Step 1-3 - Product Option
Goal: Add options to product information.
Design and implement the feature considering the relationship between the Product and Option models.

## Considerations

- [ ] remove Boolean return type from all delete methods
- [ ] change Double to BigDecimal inside the Entity (Product/price)
- [x] decide on where and how to use Models
  - -> Entity == Model
