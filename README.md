# spring-ecommerce-order


## Feature List
### Step 1-2 - first Refactoring

#### Todo
- [x] DatabaseFixture
  - [x] move DatabaseFixture to test
  - [x] rename to TestFixture

#### Apply review
- [ ] Remove unnecessary commented code
  - If these lines are not necessary, consider removing them to keep the code clean.
- [ ] Avoid using `FetchType.EAGER` unless necessary
  - Consider using `LAZY` unless eager fetching is required.
- [ ] Avoid business logic inside the entity constructor
  - Consider moving this validation to the service layer rather than placing it inside the entity.
- [ ] Simplify manual pagination logic
  - This pagination logic works, but it might be cleaner to let the repository handle pagination if possible.
- [ ] Delegate quantity adjustment to `CartItem` instead of changing it externally
  - Consider moving this logic into the `CartItem` class to encapsulate behavior and reduce direct access.
- [x] Use the Elvis operator for null checks
  - more idiomatic Kotlin by using the Elvis operator.
- [ ] Annotate read-only transactional methods
  - Since this method is only reading data, consider marking it as `readOnly = true` for clarity and potential optimization.
- [ ] Delegate member access to the object
  - Instead of accessing nested properties directly
  - consider letting the object expose what it needs through a method like `getMember()`.
- [ ] Write unit tests for model-level methods
  - This method looks like it can be unit tested. Consider writing unit tests for this logic.

---

###  Step 1-1 - Entity Mapping

Goal: Transform Repository and entities using Spring Data JPA.

- [x] **Transform Models into Entities**
    - [x] Product -> `Product @Entity`
    - [x] Member -> `Member @Entity`
    - [x] CartItem -> `CartItem @Entity`
    - [x] Cart -> `Cart @Entity`
- [x] **Decide on the relationships between databases**
    - [x] Product
    - [x] Member
    - [x] CartItem
        - [x] A CartItem should reference a Product via @ManyToOne, not @OneToOne. Multiple CartItems across different
          carts may reference the same Product.
    - [x] Cart
- [x] **Transform Repositories to JpaRepositories**
  - **Flow**
  - Rename repo to JdbcProductRepository to `JdbcProductRepository(private val jdbcTemplate)`
  - Create `interface ProductRepository`
  - Add `override` keyword before functions in JdbcProductRepository
  - Add functions to interface
  - Run tests
  - Change return type of functions to Jpa style
  - Create `interface JpaProductRepository : ProductRepository, JpaProductRepository<Product, Long>`
  - Remove `@Repository` keyword -> comment out!!
  - Run tests
    - [x] Product
    - [x] Member
    - [x] Cart
    - [x] CartItem

- [x] **Refactor Services**
    - [x] extract business logic to Entities -> most validation SHOULD happen inside the Entities!!
        - [x] e.g. addOption()

#### Tests

- [x] Refactor all tests to be less heavy
    - [x] Create companion objects with constant values
- [x] Use @Transactional @SpringBootTest for testing is still small -> use @ExtendWith(MockitoExtension::class)
    - [x] @Mock repos (lateinit)
    - [x] private service (lateinit)
    - [x] @BeforeEach setup -> init

## Step 1-2 - Pagination

Goal: Implement pagination for both the product list and the wishlist view.
Most web applications do not display all data at once. Instead, content is split into multiple pages. 
Pagination allows users to define how data should be sorted, how many items are shown per page, and which page number to retrieve.

- [x] Sorting can also be used to prioritize which data appears first.
- [x] Spring Data provides a convenient object called `Pageable`.
  - `Page<T>` – full pagination with total count, total pages, current page, etc.

## Step 1-3 - Product Option

Goal: Add options to product information.
Design and implement the feature considering the relationship between the Product and Option models.

### Feature List
- [x] has:
  - name 
  - id
  - quantity
  - products
- [x] Option names can include up to 50 characters, including spaces.
- [x] Allowed special characters in option names:
  - [x] (, ), [, ], +, -, &, /, _
  - [x] All other special characters are not allowed.
- [x] Option quantity must be at least 1 and less than 100,000,000.
- [x] Implement a method to decrease the quantity of a product option by a specified amount:
- [x] No need to create a separate HTTP API.
- [x] This logic should be implemented in the Service class or Entity class for future reuse.
- [x] check inside DTO or addOption()
  - [x] A product must always have at least one option.
  - [x] Duplicate option names are not allowed within the same product to prevent confusion during purchase.

#### Test
- [x] test Option