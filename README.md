# spring-ecommerce-order


## Feature List
### Step 1-2 - first Refactoring

#### Todo
- [x] DatabaseFixture
  - [x] move DatabaseFixture to test
  - [x] rename to TestFixture
- [x] Use fetch join to `Eager`ly load options and prevent N+1 query problem
  - [x] `ProductService`, `ProductRepository`
- [x] cleanup codebase
  - [x] return UpsertStatus and ProductResponse in upsert API
  - [x] var -> val
  - [x] removed bidirectional relation setup from Option constructor

#### Apply review
### 1. Remove unnecessary commented code

**File:** `Product.kt`
**Lines:**

```kotlin
//    init {
//        require(options.isNotEmpty()) { "Product name must not be blank" }
//    }
```
- [x] removing them to keep the code clean.

---

### 2. Avoid using `FetchType.EAGER` unless necessary

**File:** `Option.kt`
**Lines:**

```kotlin
@ManyToOne(fetch = FetchType.EAGER)
var product: Product
```

- [x] Consider using `LAZY` unless eager fetching is required.
- reference: [tecotalk](https://www.youtube.com/watch?v=ni92wUkAmQI)


---

### 3. Avoid business logic inside the entity constructor

**File:** `Option.kt`
**Lines:**

```kotlin
if (product.options.isNotEmpty()) {
    require(product.options.all { it.name != this.name }) { "duplicate name ${product.name} found" }
}
```
This looks like business logic.
- Consider moving this validation to the service layer rather than placing it inside the entity.
- point: Where does the problem start? *Bidirectional relation*
  - [x] Removed bidirectional relation setup from Option constructor
  - [x] Introduced ProductFactory to create Product with pre-built Option entities
  - [x] Moved responsibility of binding Option to Product into Product.addOption(s) to centralize relation management and maintain domain invariants
  - [x] Updated ProductService to use ProductFactory instead of direct entity creation
  - [x] Added duplicate option name validation in Product entity -> temporary!
- [ ] **moving this validation to the service layer**

---

### 4. Simplify manual pagination logic

**File:** `CartService.kt`
**Lines:**

```kotlin
val cart = findCart(memberId)
val itemResponses = cart.items.map { CartItemMapper.toResponse(it) }
val pageRequest = PageRequest.of(page, size, Sort.by("productName"))
val start = pageRequest.offset.toInt()
val end = min(start + pageRequest.pageSize, itemResponses.size)
val pageContent = itemResponses.subList(start, end)
return PageImpl<CartItemResponse>(pageContent, pageRequest, itemResponses.size.toLong())
```

- [ ] This pagination logic works, but it might be cleaner to let the repository handle pagination if possible.

---

### 5. Delegate quantity adjustment to `CartItem` instead of changing it externally

**File:** `Cart.kt`
**Lines:**

```kotlin
var quantityToRemove = quantity
if (existingItem.quantity < quantity) quantityToRemove = existingItem.quantity
existingItem.quantity -= quantityToRemove
```

- [x] moving this logic into the `CartItem` class to encapsulate behavior and reduce direct access.

---

### 6. Use the Elvis operator for null checks

**File:** `AuthService.kt`
**Lines:**

```kotlin
val member = memberRepository.findByEmail(payload)
if (member == null) {
    throw NotFoundException("Member not found")
}
```

**Suggested:**

```kotlin
val member = memberRepository.findByEmail(payload)
    ?: throw NotFoundException("Member not found")
```

- [x] Using the Elvis operator.

---

### 7. Annotate read-only transactional methods

**File:** `ProductService.kt`
**Lines:**

```kotlin
fun read(): List<Product>
```

**Suggested:**

```kotlin
@Transactional(readOnly = true)
fun read(): List<Product>
```
Since this method is only reading data...
- [x] Marking it as `readOnly = true` for clarity and potential optimization.

---

### 8. Delegate member access to the object

**File:** `StatService.kt`
**Lines:**

```kotlin
cartItems.map { it.cart.member }
```

- [x] Instead of accessing nested properties directly, letting the object expose what it needs through a method like `getMember()`.

---

### 9. Write unit tests for model-level methods

**File:** `Member.kt`
**Lines:**

```kotlin
fun validatePassword(password: String)
```

- [x] Consider writing unit tests for this logic.
  - I don't need it, I guess!
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