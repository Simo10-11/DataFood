# DataFood Backend - Complete Structure Analysis

**Project Location:** `/Users/elyelisabetta/Desktop/DataFood/IngSW2026-BE`

**Base Package:** `it.unife.sample.backend`

---

## 1. USER ENTITY / MODEL STRUCTURE

### Utente Entity
**File:** [src/main/java/it/unife/sample/backend/model/Utente.java](src/main/java/it/unife/sample/backend/model/Utente.java)

**Fields:**
- `id` (Long) - Primary key, auto-generated (IDENTITY strategy)
- `nome` (String) - First name, required
- `cognome` (String) - Last name, required
- `email` (String) - Email address, required, unique (enforced by repository)
- `password` (String) - Password in plain text (for educational purposes only)
- `ruolo` (String) - Role, required (values: "cliente" or "admin")
- `numeroTelefono` (String) - Phone number, optional
- `citta` (String) - City, optional
- `cap` (String) - Postal code, optional
- `via` (String) - Street, optional
- `numeroCivico` (String) - Street number, optional

**Annotations:**
- `@Entity` - JPA entity
- `@Table(name = "UTENTE")` - Database table mapping
- `@Data` - Lombok: generates getters, setters, toString, equals, hashCode
- `@NoArgsConstructor` - Lombok: generates no-arg constructor
- `@AllArgsConstructor` - Lombok: generates constructor with all fields

**Key Points:**
- ❌ **NO points/loyalty field yet** - This will need to be added for the points system
- Plain text password storage (not production-safe)
- Session-based authentication
- No built-in relationships defined (relationships are managed via other entities)

---

## 2. ORDER & ORDER ITEM ENTITIES

### Ordine Entity
**File:** [src/main/java/it/unife/sample/backend/model/Ordine.java](src/main/java/it/unife/sample/backend/model/Ordine.java)

**Fields:**
- `id` (Long) - Primary key, auto-generated
- `data` (LocalDateTime) - Order creation timestamp, required
- `status` (String) - Order status, required (stored as string, uses OrderStatus enum for validation)
- `utente` (Utente) - Many-to-One relationship with Utente, eager fetch, required
- `items` (List<OrdineProdotto>) - One-to-Many relationship, cascade all, orphan removal enabled, eager fetch

**Key Points:**
- ❌ **NO total/totale field** - Total is calculated dynamically in OrderService.calculateTotal()
- Status is stored as string (not enum) in database
- Eager fetching for both utente and items
- Cascade delete behavior: deleting order deletes all items

### OrdineProdotto Entity
**File:** [src/main/java/it/unife/sample/backend/model/OrdineProdotto.java](src/main/java/it/unife/sample/backend/model/OrdineProdotto.java)

**Fields:**
- `id` (OrdineProdottoId) - Composite embedded key (product_id + order_id)
- `ordine` (Ordine) - Many-to-One relationship with Ordine, eager fetch
- `prodotto` (Prodotto) - Many-to-One relationship with Prodotto, eager fetch
- `quantita` (Integer) - Order quantity, required
- `prezzoUnitario` (BigDecimal) - Unit price at order time, required

**Composite Key (OrdineProdottoId):**
```java
@Embeddable
class OrdineProdottoId {
    @Column(name = "ID_PRODOTTO")
    private Integer idProdotto;
    
    @Column(name = "ID_ORDINE")
    private Long idOrdine;
}
```

**Key Points:**
- Composite primary key with product_id and order_id
- Price is stored to preserve historical pricing at time of order
- Quantity can be validated but no stock check occurs during order creation

### OrderStatus Enum
**File:** [src/main/java/it/unife/sample/backend/model/OrderStatus.java](src/main/java/it/unife/sample/backend/model/OrderStatus.java)

**Valid Statuses:**
- `IN_LAVORAZIONE` ("in_lavorazione") - In preparation/processing
- `COMPLETATO` ("completato") - Completed
- `ANNULLATO` ("annullato") - Cancelled

**Key Methods:**
- `getDbValue()` - Returns string value for database storage
- `fromDbValue(String)` - Converts string to enum
- `isValid(String)` - Validates if string is a valid status

---

## 3. SERVICE LAYER

### OrderService
**File:** [src/main/java/it/unife/sample/backend/service/OrderService.java](src/main/java/it/unife/sample/backend/service/OrderService.java)

**Key Methods:**

#### `checkout(HttpSession session): OrderDTO`
**Flow:**
1. Retrieves cart from session attribute "cart"
2. Throws if cart is empty
3. Gets logged user from session attribute "loggedUserId"
4. Creates new Ordine entity with status "in_lavorazione" and current LocalDateTime
5. Iterates through cart items:
   - Fetches product from database
   - Creates OrdineProdotto with current product price
   - Adds to order items
6. Validates total calculation before save
7. Saves to database
8. Clears cart from session
9. Returns OrderDTO

**Current Issues:**
- ❌ No transaction management (`@Transactional` not used)
- ❌ No stock validation or deduction
- ❌ No points calculation or update
- ❌ No concurrent access handling

#### `getMyOrders(HttpSession session): List<OrderDTO>`
- Fetches all orders for logged-in user by ID
- Returns mapped DTOs

#### `getAllOrders(...): Page<OrderDTO>`
- Admin-only method
- Supports pagination, sorting (by "data", "status", "totale"), filtering by status
- Supports customer name/order ID search

#### `updateOrderStatus(Long orderId, OrderStatusUpdateDTO request, HttpSession session): OrderDTO`
- Admin-only method
- Validates order exists and status is valid
- Updates and persists order status

**Helper Methods:**

#### `getLoggedUser(HttpSession session): Utente`
- Retrieves user ID from session attribute "loggedUserId"
- Handles both Long and Integer types
- Throws IllegalStateException if not authenticated

#### `calculateTotal(Ordine order): BigDecimal`
- Calculates total by summing: `unitPrice × quantity` for each item
- Returns BigDecimal.ZERO if order is null/empty

#### `requireAdmin(HttpSession session): void`
- Checks if logged user has role "admin"
- Throws IllegalStateException if not admin

### UtenteService
**File:** [src/main/java/it/unife/sample/backend/service/UtenteService.java](src/main/java/it/unife/sample/backend/service/UtenteService.java)

**Key Methods:**

#### `login(LoginRequestDTO request): UtenteDTO`
1. Finds user by email
2. Compares plain-text passwords
3. Returns UtenteDTO (password excluded)

#### `register(RegisterRequestDTO request): UtenteDTO`
1. Checks if email already exists
2. Creates new Utente with role "cliente"
3. Saves and returns DTO

#### `restoreSession(Long userId, HttpSession session): UtenteDTO`
- Restores session after page reload
- Sets session attributes: "loggedUserId", "loggedUserRole"
- Used by frontend to maintain session across browser reloads

#### `findAllUsers(HttpSession session): List<UtenteDTO>`
- Admin-only
- Returns all users as DTOs

#### `deleteUser(Long userId, HttpSession session): void`
- **Transactional** with `@Transactional`
- Admin-only, cannot self-delete
- Prevents deletion of admin accounts
- **Cleanup flow:**
  1. Deletes all orders for user (OrdineRepository.deleteByUtenteId)
  2. Deletes all wishlist entries (WishlistRepository.deleteByUtente_Id)
  3. Deletes user record
- Throws specific exceptions for error handling

### CartService
**File:** [src/main/java/it/unife/sample/backend/service/CartService.java](src/main/java/it/unife/sample/backend/service/CartService.java)

**Key Methods:**
- `getCart(HttpSession session): CartDTO` - Retrieves or creates cart
- `addProduct(Long productId, HttpSession session): CartDTO` - Adds item or increments quantity
- `removeProduct(Long productId, HttpSession session): CartDTO` - Removes item completely
- `updateQuantity(CartUpdateRequestDTO request, HttpSession session): CartDTO` - Sets specific quantity

**Cart Storage:**
- Stored in session as "cart" attribute
- Cart is not persisted to database until checkout
- Cart items are refreshed from database to get latest product data

---

## 4. CONTROLLER STRUCTURE

### OrderController
**File:** [src/main/java/it/unife/sample/backend/controller/OrderController.java](src/main/java/it/unife/sample/backend/controller/OrderController.java)

**Endpoints:**

| Method | URL | Authentication | Role | Purpose |
|--------|-----|-----------------|------|---------|
| POST | `/api/orders/checkout` | Session | Customer | Create order from cart |
| GET | `/api/orders/my` | Session | Customer | Get user's orders |
| GET | `/api/orders` | Session | Admin | Get all orders with filtering |
| PATCH | `/api/orders/{id}` | Session | Admin | Update order status |

**Error Handling:**
- Catches `IllegalStateException` → 401 Unauthorized (not authenticated)
- Catches `IllegalArgumentException` → 400 Bad Request or 404 Not Found
- Checks error message for "non trovato" to distinguish 404

### UtenteController
**File:** [src/main/java/it/unife/sample/backend/controller/UtenteController.java](src/main/java/it/unife/sample/backend/controller/UtenteController.java)

**Endpoints:**

| Method | URL | Purpose | Status Codes |
|--------|-----|---------|--------------|
| POST | `/auth/login` or `/api/auth/login` | Authenticate user | 200/401 |
| POST | `/auth/register` or `/api/auth/register` | Register new user | 201/400 |
| POST | `/auth/logout` or `/api/auth/logout` | Clear session | 204 |
| POST | `/auth/session` or `/api/auth/session` | Restore session | 200/404 |
| GET | `/api/users` | List all users (admin-only) | 200/403 |
| DELETE | `/api/users/{id}` | Delete user (admin-only) | 204/403/404/400 |

**Authentication Flow:**

1. **Login:** 
   - POST `/auth/login` with email/password
   - Session attributes set: `loggedUserId`, `loggedUserRole`
   - Returns UtenteDTO with user info

2. **Session Restoration:**
   - POST `/auth/session` with userId from browser storage
   - Recreates server-side session

3. **Logout:**
   - Removes session attributes

### CartController
**File:** [src/main/java/it/unife/sample/backend/controller/CartController.java](src/main/java/it/unife/sample/backend/controller/CartController.java)

**Endpoints:**

| Method | URL | Purpose |
|--------|-----|---------|
| GET | `/api/cart` | Get cart contents |
| POST | `/api/cart/add/{productId}` | Add product to cart |
| POST | `/api/cart/remove/{productId}` | Remove product from cart |
| POST | `/api/cart/update` | Update item quantity |

---

## 5. REPOSITORY INTERFACES

### OrdineRepository
**File:** [src/main/java/it/unife/sample/backend/repository/OrdineRepository.java](src/main/java/it/unife/sample/backend/repository/OrdineRepository.java)

**Methods:**
- `findByUtenteId(Long userId): List<Ordine>` - Get user's orders
- `deleteByUtenteId(Long userId): void` - Delete all orders for user (used during user deletion)

### UtenteRepository
**File:** [src/main/java/it/unife/sample/backend/repository/UtenteRepository.java](src/main/java/it/unife/sample/backend/repository/UtenteRepository.java)

**Methods:**
- `findByEmail(String email): Optional<Utente>` - Authentication lookup
- Standard JpaRepository methods (findById, save, delete, findAll)

### OrdineProdottoRepository
**File:** [src/main/java/it/unife/sample/backend/repository/OrdineProdottoRepository.java](src/main/java/it/unife/sample/backend/repository/OrdineProdottoRepository.java)

**Methods:**
- Only inherited JpaRepository methods (no custom queries)

### Other Repositories
- `ProdottoRepository` - Standard JpaRepository
- `CategoriaRepository` - Standard JpaRepository
- `WishlistRepository` - Has custom method: `deleteByUtente_Id(Long userId)`

---

## 6. DTOs (DATA TRANSFER OBJECTS)

### OrderDTO
**File:** [src/main/java/it/unife/sample/backend/dto/OrderDTO.java](src/main/java/it/unife/sample/backend/dto/OrderDTO.java)

**Fields:**
- `id` (Long) - Order ID
- `nomeCliente` (String) - Customer full name (concatenated from Utente.nome + cognome)
- `data` (String) - Order date/time in ISO format
- `status` (String) - Order status
- `totale` (double) - Order total (calculated dynamically)
- `items` (List<OrderItemDTO>) - Order line items

### OrderItemDTO
**File:** [src/main/java/it/unife/sample/backend/dto/OrderItemDTO.java](src/main/java/it/unife/sample/backend/dto/OrderItemDTO.java)

**Fields:**
- `productId` (Integer) - Product ID
- `nome` (String) - Product name
- `prezzo` (double) - Unit price at order time
- `quantita` (Integer) - Quantity ordered

### UtenteDTO
**File:** [src/main/java/it/unife/sample/backend/dto/UtenteDTO.java](src/main/java/it/unife/sample/backend/dto/UtenteDTO.java)

**Fields:**
- `id` (Long)
- `email` (String)
- `nome` (String)
- `cognome` (String)
- `ruolo` (String)

**Note:** Password excluded from DTO

### LoginRequestDTO
- `email` (String)
- `password` (String)

### RegisterRequestDTO
- `nome` (String)
- `cognome` (String)
- `email` (String)
- `password` (String)

### OrderStatusUpdateDTO
- `status` (String)

### CartDTO / CartUpdateRequestDTO
- Standard cart/item DTOs for session-based cart management

---

## 7. PAYMENT/CHECKOUT FLOW

### Current Checkout Process

```
1. User adds products to cart (stored in HttpSession)
   ↓
2. User calls POST /api/orders/checkout
   ↓
3. OrderService.checkout() processes:
   a. Get cart from session
   b. Get logged user from session
   c. Create new Ordine with status="in_lavorazione"
   d. For each cart item:
      - Fetch product from DB
      - Create OrdineProdotto with current price
      - Add to order
   e. Calculate total (sum of: price × quantity)
   f. Save Ordine to database
   g. Clear cart from session
   h. Return OrderDTO
   ↓
4. Order is now in database with status "in_lavorazione"
```

### Total Calculation
**Location:** `OrderService.calculateTotal()` and `OrderMapper.calculateTotal()`

```java
BigDecimal total = order.getItems().stream()
    .map(item -> item.getPrezzoUnitario().multiply(BigDecimal.valueOf(item.getQuantita())))
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

**Key Points:**
- ❌ NO payment processing (payment would need to be added)
- ❌ NO stock deduction
- ❌ NO points calculation
- ❌ NO discount application
- Total is calculated on-the-fly, not stored in database
- Uses product price at order time (stored in OrdineProdotto.prezzoUnitario)

---

## 8. TRANSACTION MANAGEMENT

### Current Transaction Usage

**Only in UtenteService:**
```java
@Transactional
public void deleteUser(Long userId, HttpSession session) {
    // Deletes orders, wishlist items, then user
}
```

**Not Used in:**
- ❌ OrderService.checkout() - No @Transactional on checkout method
- ❌ OrderService.updateOrderStatus()
- ❌ UtenteService.login(), register(), restoreSession()

### Issues & Recommendations

**Current Issues:**
- Checkout is NOT transactional - If error occurs midway through saving items, database could be in inconsistent state
- No rollback mechanism for failed checkouts
- Stock updates (if implemented) would not be atomic with order creation

**For Points System:**
- MUST add `@Transactional` to checkout method
- Should atomically: create order, update points, deduct stock (if applicable)
- Prevents race conditions if multiple users checkout simultaneously

---

## 9. AUTHENTICATION MECHANISM

### How userId is Obtained

**In Controllers:**
1. Method receives `HttpSession session` parameter
2. Service retrieves: `Object userId = session.getAttribute("loggedUserId")`
3. Handles both Long and Integer types
4. Throws `IllegalStateException` if null

### Session Attributes Set on Login
- `loggedUserId` (Long) - User's database ID
- `loggedUserRole` (String) - "admin" or "cliente"

### Session Management

**Setting Session:**
```java
// In UtenteController.login()
UtenteDTO loggedUser = utenteService.login(request);
session.setAttribute("loggedUserId", loggedUser.getId());
session.setAttribute("loggedUserRole", loggedUser.getRuolo());
```

**Getting Logged User:**
```java
// In services
Object userIdObj = session.getAttribute("loggedUserId");
Long userId = (Long) userIdObj;  // or from Integer
Utente utente = utenteRepository.findById(userId)
    .orElseThrow(() -> new IllegalStateException("Utente non autenticato"));
```

### Security Notes
- ❌ Plain-text password comparison (not production-safe)
- ❌ No CSRF protection visible
- ❌ No request authentication filter (relies on session)
- CORS configured for localhost:4200 with credentials
- Session-based auth (not JWT or token-based)

---

## 10. ADDITIONAL ENTITIES

### Prodotto (Product)
**Fields:**
- `id` (Integer) - Primary key
- `nome` (String) - Product name
- `descrizione` (String) - Description
- `prezzo` (BigDecimal) - Current price
- `quantitaDisponibile` (Integer) - Available stock
- `imageUrl` (String) - Image URL
- `categoria` (Categoria) - Many-to-One relationship

### Categoria (Category)
- Basic entity with id, nome, descrizione

### Cart / CartItem
- Not persisted to database
- Session-based temporary storage
- Used for shopping cart operations before checkout

### Wishlist
- Persisted to database
- Has custom delete method for user cleanup

---

## 11. MAPPERS

### OrderMapper
**File:** [src/main/java/it/unife/sample/backend/mapper/OrderMapper.java](src/main/java/it/unife/sample/backend/mapper/OrderMapper.java)

**Methods:**
- `toDTO(Ordine entity): OrderDTO` - Converts Ordine to OrderDTO
  - Builds customer name from utente.nome + cognome
  - Formats date as ISO string
  - Calculates total
- `toDTO(OrdineProdotto entity): OrderItemDTO` - Converts item
  - Maps prodotto.id to productId
  - Maps prodotto.nome to nome
  - Maps prezzoUnitario to prezzo

### UtenteMapper
**Methods:**
- `toDTO(Utente entity): UtenteDTO` - Excludes sensitive fields
- `toEntity(UtenteDTO dto): Utente` - Ignores sensitive fields during mapping

---

## 12. CONFIGURATION

### WebConfig
**File:** [src/main/java/it/unife/sample/backend/WebConfig.java](src/main/java/it/unife/sample/backend/WebConfig.java)

**CORS Configuration:**
- Allows requests from `http://localhost:4200`
- Allows methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
- Allows all headers
- Credentials: enabled

### Application Configuration
**File:** [src/main/resources/application.yaml](src/main/resources/application.yaml)

```yaml
spring:
  application:
    name: webapp
  datasource:
    url: jdbc:mysql://localhost:3306/e_commerceINGSW
    username: gavanelliuser
    password: password123
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

**Key Points:**
- MySQL database on localhost:3306
- Hibernate auto-update DDL (creates/updates schema)
- SQL queries logged to console

### Dependencies
**File:** build.gradle

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.mapstruct:mapstruct:1.5.5.Final'
implementation 'org.projectlombok:lombok:1.18.30'
runtimeOnly 'com.mysql:mysql-connector-j'
```

**Spring Boot Version:** 3.4.4
**Java Version:** 17
**No dedicated security framework** (Spring Security not used)

---

## CRITICAL GAPS FOR POINTS SYSTEM INTEGRATION

### Missing Components

1. **User Points Field**
   - ❌ Utente entity lacks `punti` or `points` field
   - Need to add column to database and entity

2. **Points DTO**
   - ❌ UtenteDTO doesn't include points
   - Need to add for frontend display

3. **Transaction Safety**
   - ❌ OrderService.checkout() not @Transactional
   - Risk of inconsistent state if errors occur
   - MUST ADD for atomic operations

4. **Points Calculation Logic**
   - ❌ No service/method to calculate points from order total
   - Need to determine rules: percentage, fixed amount, tiers?

5. **Points Update After Checkout**
   - ❌ Not implemented
   - Need to persist points increment after successful order

6. **Stock Management**
   - ❌ Not enforced during checkout
   - Product availability not validated
   - Need to implement if required

7. **Payment Processing**
   - ❌ No payment gateway integration
   - Order created immediately with "in_lavorazione" status
   - No distinction between paid/unpaid orders

8. **Discount Application**
   - ❌ No discount logic during checkout
   - Cannot redeem points for discounts
   - Need to add if points can be used

---

## SUMMARY TABLE

| Component | Status | Notes |
|-----------|--------|-------|
| User Entity | ✓ Complete | Missing: points field |
| Order Entity | ✓ Complete | Missing: total field (calculated dynamically) |
| OrderItem Entity | ✓ Complete | Uses composite key |
| OrderService | ✓ Complete | Missing: @Transactional, points logic, stock check |
| UtenteService | ✓ Complete | Has @Transactional for delete |
| Authentication | ✓ Complete | Session-based, plain-text passwords |
| Checkout Flow | ✓ Complete | Missing: transaction safety, points |
| Repositories | ✓ Complete | Basic, no custom query logic needed |
| Mappers | ✓ Complete | Using MapStruct framework |
| Controllers | ✓ Complete | Proper error handling |
| Configuration | ✓ Complete | MySQL, Hibernate, CORS configured |

---

## NEXT STEPS FOR POINTS SYSTEM

1. **Add `punti` field to Utente entity**
2. **Add `points` field to UtenteDTO**
3. **Create PointsService** for points calculations
4. **Add @Transactional to OrderService.checkout()**
5. **Implement points accumulation logic** in checkout flow
6. **Add points persistence** after successful order
7. **Create points history tracking** (optional)
8. **Add points redemption endpoints** (if needed)
