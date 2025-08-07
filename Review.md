## Step 1-2 - first Refactoring: apply review

### 1. Remove unnecessary commented code

**File:** `Product.kt`
**Lines:**

```kotlin
//    init {
//        require(options.isNotEmpty()) { "Product name must not be blank" }
//    }
```
- [ ] If these lines are not necessary, consider removing them to keep the code clean.

---

### 2. Avoid using `FetchType.EAGER` unless necessary

**File:** `Option.kt`
**Lines:**

```kotlin
@ManyToOne(fetch = FetchType.EAGER)
var product: Product
```

- [ ] Consider using `LAZY` unless eager fetching is required.

---

### 3. Avoid business logic inside the entity constructor

**File:** `Option.kt`
**Lines:**

```kotlin
if (product.options.isNotEmpty()) {
    require(product.options.all { it.name != this.name }) { "duplicate name ${product.name} found" }
}
```

- [ ] This looks like business logic. Consider moving this validation to the service layer rather than placing it inside the entity.

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

- [ ] Consider moving this logic into the `CartItem` class to encapsulate behavior and reduce direct access.

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

- [ ] You can make this more idiomatic Kotlin by using the Elvis operator.

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

- [ ] Since this method is only reading data, consider marking it as `readOnly = true` for clarity and potential optimization.

---

### 8. Delegate member access to the object

**File:** `StatService.kt`
**Lines:**

```kotlin
cartItems.map { it.cart.member }
```

- [ ] Instead of accessing nested properties directly, consider letting the object expose what it needs through a method like `getMember()`.

---

### 9. Write unit tests for model-level methods

**File:** `Member.kt`
**Lines:**

```kotlin
fun validatePassword(password: String)
```

- [ ] This method looks like it can be unit tested. Consider writing unit tests for this logic.

