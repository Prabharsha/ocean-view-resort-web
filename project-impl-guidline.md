You are an expert Java full-stack developer. Build a complete, production-quality 
Ocean View Resort Room Reservation System for a Cardiff Metropolitan University 
Advanced Programming (CIS6003) assignment. The system must achieve the highest 
academic marks (70–100%) by meeting ALL criteria below.

═══════════════════════════════════════════════════════
SYSTEM OVERVIEW
═══════════════════════════════════════════════════════

Project Name: Ocean View Resort Room Reservation System
Language: Java (Spring Boot backend)
Frontend: HTML5, CSS3, JavaScript (Thymeleaf + bootstrap)
Database: MySQL 8.x
Architecture: 3-Tier (Presentation → Business Logic → Data Access)
Design Patterns: Repository, Service, DAO, Singleton, Factory, Builder, MVC
Testing: JUnit 5 + Mockito (TDD approach)
Build Tool: Maven
Version Control: Git/GitHub

═══════════════════════════════════════════════════════
TASK 1: PROJECT STRUCTURE (Maven Multi-Module)
═══════════════════════════════════════════════════════

Create the following package structure:

com.oceanview.resort/
├── OceanViewApplication.java                  (Main entry point)
├── config/
│   ├── SecurityConfig.java                    (Spring Security config)
│   ├── DatabaseConfig.java                    (DataSource config)
│   └── WebConfig.java                         (CORS, MVC config)
├── controller/
│   ├── AuthController.java
│   ├── ReservationController.java
│   ├── RoomController.java
│   ├── BillController.java
│   ├── PaymentController.java
│   ├── ReportController.java
│   └── MaintenanceController.java
├── service/
│   ├── interfaces/
│   │   ├── UserService.java
│   │   ├── ReservationService.java
│   │   ├── RoomService.java
│   │   ├── BillService.java
│   │   ├── PaymentService.java
│   │   └── ReportService.java
│   └── impl/
│       ├── UserServiceImpl.java
│       ├── ReservationServiceImpl.java
│       ├── RoomServiceImpl.java
│       ├── BillServiceImpl.java
│       ├── PaymentServiceImpl.java
│       └── ReportServiceImpl.java
├── repository/
│   ├── UserRepository.java
│   ├── ReservationRepository.java
│   ├── RoomRepository.java
│   ├── BillRepository.java
│   └── PaymentRepository.java
├── dao/
│   ├── interfaces/
│   │   ├── UserDAO.java
│   │   ├── ReservationDAO.java
│   │   └── RoomDAO.java
│   └── impl/
│       ├── UserDAOImpl.java
│       ├── ReservationDAOImpl.java
│       └── RoomDAOImpl.java
├── model/
│   ├── User.java
│   ├── Customer.java
│   ├── Staff.java
│   ├── Manager.java
│   ├── Reservation.java
│   ├── Room.java
│   ├── Bill.java
│   ├── Payment.java
│   └── enums/
│       ├── RoomType.java
│       ├── PaymentMethod.java
│       ├── ReservationStatus.java
│       └── UserRole.java
├── dto/
│   ├── UserDTO.java
│   ├── ReservationDTO.java
│   ├── BillDTO.java
│   └── PaymentDTO.java
├── mapper/
│   ├── UserMapper.java
│   ├── ReservationMapper.java
│   └── BillMapper.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ReservationNotFoundException.java
│   ├── RoomNotAvailableException.java
│   └── InvalidPaymentException.java
└── util/
    ├── ReservationNumberGenerator.java
    ├── BillCalculator.java
    └── ValidationUtil.java

src/main/resources/
├── application.properties
├── application-dev.properties
├── application-prod.properties
├── db/
│   ├── schema.sql
│   └── data.sql
└── templates/
    ├── login.html
    ├── dashboard.html
    ├── reservation/
    │   ├── create.html
    │   ├── view.html
    │   ├── list.html
    │   └── update.html
    ├── bill/
    │   ├── view.html
    │   └── print.html
    ├── report/
    │   ├── monthly.html
    │   └── weekly.html
    └── help.html

src/test/java/com/oceanview/resort/
├── service/
│   ├── UserServiceTest.java
│   ├── ReservationServiceTest.java
│   └── BillServiceTest.java
├── controller/
│   ├── AuthControllerTest.java
│   └── ReservationControllerTest.java
└── repository/
    └── ReservationRepositoryTest.java

═══════════════════════════════════════════════════════
TASK 2: DATABASE SCHEMA (MySQL)
═══════════════════════════════════════════════════════

Generate the complete schema.sql file with the following tables:

-- 1. USERS TABLE
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,       -- BCrypt hashed
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20),
    role ENUM('CUSTOMER', 'STAFF', 'MANAGER', 'MAINTENANCE') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. CUSTOMERS TABLE (extends users)
CREATE TABLE customers (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) UNIQUE NOT NULL,
    address TEXT,
    loyalty_points INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. ROOMS TABLE
CREATE TABLE rooms (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type ENUM('STANDARD','DELUXE','SUITE','PENTHOUSE') NOT NULL,
    floor_number INT NOT NULL,
    capacity INT NOT NULL,
    rate_per_night DECIMAL(10,2) NOT NULL,
    description TEXT,
    is_available BOOLEAN DEFAULT TRUE,
    amenities JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. RESERVATIONS TABLE
CREATE TABLE reservations (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    reservation_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    room_id VARCHAR(36) NOT NULL,
    staff_id VARCHAR(36),
    guest_name VARCHAR(200) NOT NULL,
    guest_address TEXT,
    guest_contact VARCHAR(20) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    num_guests INT NOT NULL DEFAULT 1,
    special_requests TEXT,
    status ENUM('PENDING','CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') 
           DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (staff_id) REFERENCES users(id)
);

-- 5. BILLS TABLE
CREATE TABLE bills (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    reservation_id VARCHAR(36) UNIQUE NOT NULL,
    num_nights INT NOT NULL,
    room_rate DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_rate DECIMAL(5,2) DEFAULT 10.00,
    tax_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('UNPAID','PARTIAL','PAID') DEFAULT 'UNPAID',
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

-- 6. PAYMENTS TABLE
CREATE TABLE payments (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    bill_id VARCHAR(36) NOT NULL,
    reservation_id VARCHAR(36) NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH','CARD','POINTS','ONLINE') NOT NULL,
    transaction_reference VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_by VARCHAR(36),
    notes TEXT,
    FOREIGN KEY (bill_id) REFERENCES bills(id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    FOREIGN KEY (processed_by) REFERENCES users(id)
);

-- 7. AUDIT LOG TABLE
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(36),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id VARCHAR(36),
    old_value JSON,
    new_value JSON,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- INDEXES for performance
CREATE INDEX idx_reservations_customer ON reservations(customer_id);
CREATE INDEX idx_reservations_room ON reservations(room_id);
CREATE INDEX idx_reservations_dates ON reservations(check_in_date, check_out_date);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_payments_bill ON payments(bill_id);
CREATE INDEX idx_audit_user ON audit_log(user_id);
CREATE INDEX idx_audit_timestamp ON audit_log(timestamp);

-- STORED PROCEDURE: Calculate Bill
DELIMITER //
CREATE PROCEDURE CalculateBill(IN res_id VARCHAR(36))
BEGIN
    DECLARE v_nights INT;
    DECLARE v_rate DECIMAL(10,2);
    DECLARE v_subtotal DECIMAL(10,2);
    DECLARE v_tax DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);
    
    SELECT DATEDIFF(r.check_out_date, r.check_in_date),
           rm.rate_per_night
    INTO v_nights, v_rate
    FROM reservations r
    JOIN rooms rm ON r.room_id = rm.id
    WHERE r.id = res_id;
    
    SET v_subtotal = v_nights * v_rate;
    SET v_tax = v_subtotal * 0.10;
    SET v_total = v_subtotal + v_tax;
    
    INSERT INTO bills (reservation_id, num_nights, room_rate, subtotal, 
                       tax_amount, total_amount)
    VALUES (res_id, v_nights, v_rate, v_subtotal, v_tax, v_total)
    ON DUPLICATE KEY UPDATE
        num_nights = v_nights, room_rate = v_rate,
        subtotal = v_subtotal, tax_amount = v_tax, total_amount = v_total;
END //
DELIMITER ;

-- TRIGGER: Update room availability on reservation
DELIMITER //
CREATE TRIGGER after_reservation_checkin
AFTER UPDATE ON reservations
FOR EACH ROW
BEGIN
    IF NEW.status = 'CHECKED_IN' THEN
        UPDATE rooms SET is_available = FALSE WHERE id = NEW.room_id;
    ELSEIF NEW.status = 'CHECKED_OUT' OR NEW.status = 'CANCELLED' THEN
        UPDATE rooms SET is_available = TRUE WHERE id = NEW.room_id;
    END IF;
END //
DELIMITER ;

═══════════════════════════════════════════════════════
TASK 3: MAVEN pom.xml
═══════════════════════════════════════════════════════

Generate the complete pom.xml with these dependencies:
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-thymeleaf
- thymeleaf-extras-springsecurity6
- spring-boot-starter-validation
- spring-boot-starter-mail
- mysql-connector-j
- lombok
- mapstruct
- jjwt-api, jjwt-impl, jjwt-jackson (JWT authentication)
- spring-boot-starter-test
- junit-jupiter
- mockito-core
- h2 (for testing)
- jasperreports (for PDF bill generation)
- itext7-core (PDF generation)
- springdoc-openapi-starter-webmvc-ui (Swagger API docs)
- spring-boot-actuator

═══════════════════════════════════════════════════════
TASK 4: MODEL CLASSES (Full Implementation)
═══════════════════════════════════════════════════════

Generate ALL model classes with:
- Full JPA annotations (@Entity, @Table, @Column, etc.)
- Lombok annotations (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)
- Validation annotations (@NotNull, @Email, @Size, @Pattern, etc.)
- Proper relationships (@OneToOne, @OneToMany, @ManyToOne, @ManyToMany)
- equals(), hashCode(), toString() methods

Example for User.java:
@Entity @Table(name = "users")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank @Size(min = 3, max = 50)
    private String username;
    
    @Column(nullable = false)
    @NotBlank
    private String password;
    
    @Column(name = "first_name", nullable = false)
    @NotBlank @Size(max = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    @NotBlank
    private String lastName;
    
    @Column(unique = true, nullable = false)
    @Email @NotBlank
    private String email;
    
    @Column(length = 20)
    @Pattern(regexp = "^[+]?[0-9]{10,15}$")
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

-- Generate similar complete implementations for:
-- Customer.java (extends User, adds address, loyaltyPoints)
-- Staff.java (extends User, adds department, employeeId)
-- Manager.java (extends Staff)
-- Room.java (full entity with amenities as JSON)
-- Reservation.java (full entity with all relationships)
-- Bill.java (full entity with calculation fields)
-- Payment.java (full entity)

═══════════════════════════════════════════════════════
TASK 5: SERVICE LAYER (Full Business Logic)
═══════════════════════════════════════════════════════

Implement ReservationServiceImpl.java with these methods:

public interface ReservationService {
    ReservationDTO createReservation(ReservationDTO dto);
    ReservationDTO findByReservationNumber(String reservationNumber);
    List<ReservationDTO> findAllReservations();
    List<ReservationDTO> findByCustomerId(String customerId);
    List<ReservationDTO> findByDateRange(LocalDate start, LocalDate end);
    ReservationDTO updateReservationStatus(String id, ReservationStatus status);
    void cancelReservation(String id);
    boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut);
    List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, 
                                   RoomType type);
    ReservationDTO checkIn(String reservationNumber);
    ReservationDTO checkOut(String reservationNumber);
    byte[] generateReservationConfirmationPDF(String reservationId);
}

-- In the implementation:
-- Use @Transactional annotations appropriately
-- Validate all inputs using custom validators
-- Generate unique reservation number: "OVR-" + year + "-" + 6-digit-sequence
-- Check room availability before creating reservation
-- Send confirmation email upon successful reservation (async)
-- Throw appropriate custom exceptions
-- Log all operations to audit_log table

Implement BillServiceImpl.java:

public interface BillService {
    BillDTO generateBill(String reservationId);
    BillDTO findBillByReservationId(String reservationId);
    BillDTO applyDiscount(String billId, BigDecimal discountPercent);
    byte[] generateBillPDF(String billId);
    void markAsPaid(String billId, PaymentDTO paymentDTO);
    BigDecimal calculateTotal(int numNights, BigDecimal ratePerNight, 
                               BigDecimal taxRate, BigDecimal discount);
    List<BillDTO> getUnpaidBills();
}

-- calculateTotal() formula:
-- subtotal = numNights * ratePerNight
-- taxAmount = subtotal * (taxRate / 100)
-- total = subtotal + taxAmount - discount
-- Use BigDecimal for all monetary calculations (never double/float)

═══════════════════════════════════════════════════════
TASK 6: REST API CONTROLLERS (Full Implementation)
═══════════════════════════════════════════════════════

Implement ALL controllers with:
- Full @RestController or @Controller annotations
- Proper HTTP method mappings (@GetMapping, @PostMapping, etc.)
- Input validation (@Valid, @RequestBody, @PathVariable)
- Consistent response format using ResponseEntity<>
- Swagger/OpenAPI annotations for documentation
- Proper HTTP status codes (200, 201, 400, 401, 403, 404, 409, 500)

AuthController endpoints:
POST   /api/auth/login         - Login and receive JWT
POST   /api/auth/logout        - Invalidate token  
POST   /api/auth/register      - Register new customer
POST   /api/auth/refresh       - Refresh JWT token
GET    /api/auth/profile       - Get current user profile
PUT    /api/auth/change-password - Change password

ReservationController endpoints:
POST   /api/reservations               - Create reservation
GET    /api/reservations               - List all (STAFF/MANAGER)
GET    /api/reservations/{id}          - Get by ID
GET    /api/reservations/number/{num}  - Get by reservation number
GET    /api/reservations/customer/{id} - Get customer reservations
PUT    /api/reservations/{id}          - Update reservation
DELETE /api/reservations/{id}          - Cancel reservation
PUT    /api/reservations/{id}/checkin  - Check in
PUT    /api/reservations/{id}/checkout - Check out
GET    /api/reservations/{id}/pdf      - Download confirmation PDF

BillController endpoints:
POST   /api/bills/generate/{reservationId} - Generate bill
GET    /api/bills/{id}                     - Get bill
GET    /api/bills/reservation/{resId}      - Get bill by reservation
POST   /api/bills/{id}/discount            - Apply discount
GET    /api/bills/{id}/pdf                 - Download bill PDF
PUT    /api/bills/{id}/pay                 - Mark as paid

RoomController endpoints:
GET    /api/rooms                          - List all rooms
GET    /api/rooms/available                - Find available rooms
GET    /api/rooms/{id}                     - Get room details
POST   /api/rooms                          - Add room (MANAGER)
PUT    /api/rooms/{id}                     - Update room (MANAGER/MAINTENANCE)
DELETE /api/rooms/{id}                     - Delete room (MANAGER)

ReportController endpoints:
GET    /api/reports/monthly/{year}/{month} - Monthly report
GET    /api/reports/weekly/{year}/{week}   - Weekly report
GET    /api/reports/occupancy              - Occupancy report
GET    /api/reports/revenue                - Revenue report
GET    /api/reports/monthly/pdf            - Download monthly PDF report

═══════════════════════════════════════════════════════
TASK 7: SECURITY CONFIGURATION
═══════════════════════════════════════════════════════

Implement full Spring Security with JWT:

1. SecurityConfig.java:
   - Configure HTTP security with role-based access
   - CUSTOMER: can create/view own reservations, pay bills
   - STAFF: all customer permissions + manage any reservation
   - MANAGER: all staff permissions + view reports, manage rooms, manage users
   - MAINTENANCE: room maintenance and system maintenance
   - Public endpoints: /api/auth/**, /api/rooms (GET), /static/**

2. JwtTokenProvider.java:
   - Generate JWT with userId, username, role, expiry (24 hours)
   - Validate JWT tokens
   - Extract user details from token
   - Refresh token support (7-day refresh token)

3. JwtAuthenticationFilter.java:
   - Extract JWT from Authorization header
   - Validate and set SecurityContext

4. UserDetailsServiceImpl.java:
   - Load user by username from database
   - Map roles to Spring Security GrantedAuthority

5. Password encoding: BCryptPasswordEncoder with strength 12

═══════════════════════════════════════════════════════
TASK 8: FRONTEND (Thymeleaf + Bootstrap 5)
═══════════════════════════════════════════════════════

Generate complete, professional HTML templates:

1. login.html:
   - Professional hotel-themed login form
   - Username and password fields with validation
   - Error message display
   - "Forgot Password" link
   - Background: Ocean/beach themed CSS

2. dashboard.html:
   - Role-based dashboard (different views per role)
   - Statistics cards: Total Reservations, Available Rooms, 
     Today's Check-ins, Today's Check-outs, Monthly Revenue
   - Recent reservations table
   - Quick action buttons
   - Navigation sidebar with all menu items
   - Use Bootstrap 5 + Chart.js for statistics graphs

3. reservation/create.html:
   - Multi-step form (Guest Info → Room Selection → Confirmation)
   - Date picker for check-in/check-out
   - Real-time room availability check
   - Room type selector with pricing display
   - Client-side validation with error messages
   - Form submission with AJAX

4. reservation/view.html:
   - Full reservation details display
   - Status badge with color coding
   - Action buttons (Check In, Check Out, Cancel, Print)
   - Bill summary section
   - Payment history

5. reservation/list.html:
   - Searchable, sortable, paginated table
   - Filter by status, date range, room type
   - Export to PDF/Excel buttons
   - Bulk actions for managers

6. bill/view.html:
   - Professional bill layout with hotel branding
   - Itemized charges breakdown
   - Tax calculation display
   - Payment options (Cash, Card, Points)
   - Print bill button

7. report/monthly.html:
   - Bar chart for monthly revenue (Chart.js)
   - Line chart for occupancy rate
   - Data table with all reservations
   - Download PDF/Excel option

8. help.html:
   - Accordion-style FAQ
   - Step-by-step guides with screenshots
   - Role-based help content
   - Contact support section

CSS Requirements:
- Custom CSS with Ocean View Resort branding (ocean blue, white, sand/gold)
- Fully responsive (mobile, tablet, desktop)
- Professional hotel aesthetic
- Smooth transitions and hover effects
- Loading spinners for async operations

═══════════════════════════════════════════════════════
TASK 9: DESIGN PATTERNS IMPLEMENTATION
═══════════════════════════════════════════════════════

Implement and DOCUMENT these design patterns (critical for marks):

1. REPOSITORY PATTERN:
   - All database access through Repository interfaces
   - JPA repositories extend JpaRepository<Entity, ID>
   - Custom query methods with @Query annotations
   - Example: ReservationRepository.findByRoomIdAndCheckInDateBetween()

2. SERVICE LAYER PATTERN:
   - Interface + Implementation separation
   - @Service annotation, @Transactional methods
   - Business logic isolated from controllers and repositories

3. DAO PATTERN:
   - Custom DAO layer for complex native SQL queries
   - UserDAOImpl, ReservationDAOImpl with JDBC template for reports

4. BUILDER PATTERN:
   - Use Lombok @Builder on all model classes
   - BillCalculator.builder() for constructing bills
   - ReservationDTO.builder() for creating DTOs

5. SINGLETON PATTERN:
   - Spring @Service and @Repository are singletons by default
   - ReservationNumberGenerator as @Component singleton
   - Document this in code with comments

6. FACTORY PATTERN:
   - PaymentStrategyFactory: creates PaymentStrategy based on method
   - Strategies: CashPaymentStrategy, CardPaymentStrategy, 
     PointsPaymentStrategy
   - Interface: PaymentStrategy with execute() method

7. STRATEGY PATTERN (for payments):
   public interface PaymentStrategy {
       PaymentResult execute(PaymentRequest request);
       PaymentMethod getMethod();
   }
   // Implementations for Cash, Card, Points

8. OBSERVER PATTERN (for notifications):
   - ReservationEventPublisher (Spring ApplicationEventPublisher)
   - Events: ReservationCreatedEvent, CheckInEvent, CheckOutEvent
   - Listeners: EmailNotificationListener, AuditLogListener

9. MVC PATTERN:
   - Controllers (presentation), Services (business), 
     Repositories (data access)
   - Clearly documented in architecture section

10. DECORATOR PATTERN (optional bonus):
    - LoggingReservationService wrapping ReservationServiceImpl
    - Adds logging to all service method calls

═══════════════════════════════════════════════════════
TASK 10: TEST-DRIVEN DEVELOPMENT (TDD)
═══════════════════════════════════════════════════════

Follow RED → GREEN → REFACTOR cycle. Generate ALL test files:

1. ReservationServiceTest.java (JUnit 5 + Mockito):

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private CustomerRepository customerRepository;
    @InjectMocks private ReservationServiceImpl reservationService;

    // Test 1: Create reservation successfully
    @Test
    void createReservation_ValidData_ReturnsReservationDTO() { ... }

    // Test 2: Room not available throws exception
    @Test
    void createReservation_RoomNotAvailable_ThrowsException() { ... }

    // Test 3: Invalid dates (checkout before checkin)
    @Test
    void createReservation_InvalidDates_ThrowsValidationException() { ... }

    // Test 4: Find by reservation number
    @Test
    void findByReservationNumber_Exists_ReturnsDTO() { ... }

    // Test 5: Find by reservation number not found
    @Test
    void findByReservationNumber_NotFound_ThrowsNotFoundException() { ... }

    // Test 6: Cancel reservation
    @Test
    void cancelReservation_ValidId_UpdatesStatus() { ... }

    // Test 7: Check in changes status and room availability
    @Test
    void checkIn_ValidReservation_UpdatesStatusAndRoom() { ... }

    // Test 8: Generate unique reservation numbers
    @Test
    void generateReservationNumber_ReturnsUniqueNumbers() { ... }
}

2. BillServiceTest.java:
    // Test calculateTotal with various inputs
    // Test correct tax calculation
    // Test discount application
    // Test generate bill creates correct bill record
    // Parameterized tests with @ParameterizedTest + @CsvSource

3. UserServiceTest.java:
    // Test user registration with valid data
    // Test duplicate username throws exception
    // Test password encoding
    // Test role assignment

4. AuthControllerTest.java (MockMvc integration test):
    // Test login with valid credentials returns JWT
    // Test login with invalid credentials returns 401
    // Test protected endpoint without token returns 403

5. ReservationControllerTest.java (MockMvc):
    // Test POST /api/reservations - 201 Created
    // Test GET /api/reservations/{id} - 200 OK
    // Test GET /api/reservations/{id} - 404 Not Found
    // Test unauthorized access - 403

6. BillCalculatorTest.java:
    @ParameterizedTest
    @CsvSource({
        "3, 150.00, 10.00, 0.00, 495.00",
        "5, 200.00, 10.00, 50.00, 1050.00",
        "1, 350.00, 15.00, 0.00, 402.50",
        "7, 100.00, 10.00, 100.00, 670.00"
    })
    void calculateTotal_VariousInputs_ReturnsCorrectTotal(
        int nights, BigDecimal rate, BigDecimal taxRate, 
        BigDecimal discount, BigDecimal expected) { ... }

7. RoomAvailabilityTest.java:
    // Test overlapping date detection
    // Test boundary date conditions
    // Test room becomes unavailable after check-in trigger

Generate a test-plan-document section in code comments with:
- Test ID
- Test description
- Preconditions
- Test steps
- Expected result
- Actual result
- Pass/Fail status

═══════════════════════════════════════════════════════
TASK 11: ADDITIONAL FEATURES (for excellent/70+ marks)
═══════════════════════════════════════════════════════

1. EMAIL NOTIFICATIONS (Spring Mail):
   - Reservation confirmation email with PDF attachment
   - Check-in reminder (sent day before)
   - Check-out reminder
   - Bill receipt email
   - HTML email templates using Thymeleaf

2. PDF GENERATION (iText7 / JasperReports):
   - Professional bill PDF with hotel letterhead
   - Reservation confirmation PDF
   - Monthly report PDF with charts

3. SESSION MANAGEMENT:
   - JWT stored in HttpOnly cookie (secure)
   - Session timeout handling
   - Remember me functionality
   - Concurrent session control (one session per user)

4. ADVANCED REPORTING:
   - Monthly occupancy rate (rooms occupied / total rooms * 100)
   - Monthly revenue breakdown by room type
   - Weekly check-in/check-out statistics
   - Top room types by booking frequency
   - Average length of stay
   - Use Chart.js for visual graphs

5. ROOM SEARCH WITH FILTERS:
   - Filter by room type, capacity, price range, amenities
   - Date availability check in real-time
   - Sort by price, rating, room number

6. LOYALTY POINTS SYSTEM:
   - Earn 1 point per dollar spent
   - Redeem 100 points = $10 discount
   - Points history tracking
   - Tier system: Bronze, Silver, Gold, Platinum

7. ADMIN DASHBOARD METRICS:
   - Real-time WebSocket updates for new reservations
   - Occupancy dashboard with live updates

8. API DOCUMENTATION:
   - Full Swagger/OpenAPI 3.0 documentation
   - Available at /swagger-ui.html
   - All endpoints documented with request/response examples

═══════════════════════════════════════════════════════
TASK 12: application.properties CONFIGURATION
═══════════════════════════════════════════════════════

# Server
server.port=8080
server.servlet.context-path=/oceanview

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/oceanview_resort
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# JWT
app.jwt.secret=your-256-bit-secret-key-here
app.jwt.expiration=86400000
app.jwt.refresh-expiration=604800000

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=oceanview@gmail.com
spring.mail.password=app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Logging
logging.level.com.oceanview=DEBUG
logging.level.org.springframework.security=INFO
logging.file.name=logs/oceanview.log

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always

═══════════════════════════════════════════════════════
TASK 13: GLOBAL EXCEPTION HANDLING
═══════════════════════════════════════════════════════

Create GlobalExceptionHandler.java with @RestControllerAdvice:

- Handle ReservationNotFoundException → 404 with message
- Handle RoomNotAvailableException → 409 Conflict
- Handle MethodArgumentNotValidException → 400 with field errors
- Handle AccessDeniedException → 403 Forbidden
- Handle AuthenticationException → 401 Unauthorized
- Handle DataIntegrityViolationException → 409 (duplicate key)
- Handle generic Exception → 500 with sanitized message

Standard error response format:
{
  "timestamp": "2025-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Reservation OVR-2025-000123 not found",
  "path": "/api/reservations/number/OVR-2025-000123"
}

═══════════════════════════════════════════════════════
TASK 14: GITHUB WORKFLOW (CI/CD) - .github/workflows/
═══════════════════════════════════════════════════════

Generate ci.yml for GitHub Actions:

name: Ocean View Resort CI/CD
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: test_password
          MYSQL_DATABASE: oceanview_test
    steps:
      - uses: actions/checkout@v3
      - Set up JDK 17
      - Cache Maven packages
      - Run tests: mvn test
      - Upload test results
      - Generate coverage report (JaCoCo)
      - Upload coverage to Codecov

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - Build JAR: mvn package -DskipTests
      - Build Docker image
      - Push to Docker Hub

  deploy: (optional stage for documentation)
    needs: build
    steps:
      - Deploy to staging environment

═══════════════════════════════════════════════════════
TASK 15: SAMPLE DATA (data.sql)
═══════════════════════════════════════════════════════

Insert sample data for testing:
- 3 users: admin/manager (password: Admin@123), 
  staff1 (password: Staff@123), customer1 (password: Customer@123)
- 10 rooms: 3 Standard, 3 Deluxe, 2 Suite, 2 Penthouse with rates
- 5 sample reservations in various states
- Sample bills and payments

All passwords must be BCrypt hashed in the SQL file.
Include a comment showing the plaintext for testing purposes.

═══════════════════════════════════════════════════════
FINAL REQUIREMENTS & QUALITY STANDARDS
═══════════════════════════════════════════════════════

CODE QUALITY:
- All public methods must have Javadoc comments
- Follow Google Java Style Guide
- Maximum method length: 30 lines
- No magic numbers (use constants or enums)
- Proper exception handling (no empty catch blocks)
- SRP: each class has single responsibility

VALIDATION:
- Server-side validation on ALL inputs
- Client-side validation for UX
- SQL injection prevention (parameterized queries via JPA)
- XSS prevention (Thymeleaf auto-escaping)
- CSRF protection (Spring Security CSRF tokens)

DOCUMENTATION in code:
- README.md with: setup instructions, API documentation, 
  design patterns used, database schema diagram, 
  class hierarchy explanation, test results summary
- Architecture Decision Records (ADR) for major decisions
- UML diagram source files (PlantUML .puml files)

DELIVERABLE FILES:
Generate these PlantUML files for UML diagrams:
1. use-case-diagram.puml
2. class-diagram.puml
3. sequence-diagram-login.puml
4. sequence-diagram-create-reservation.puml
5. sequence-diagram-checkout-payment.puml

═══════════════════════════════════════════════════════
OUTPUT FORMAT
═══════════════════════════════════════════════════════

Provide ALL files with their FULL content. For each file:
1. State the file path
2. Provide the COMPLETE code (no truncation, no "..." placeholders)
3. Include all imports
4. All methods fully implemented

Start with: pom.xml → schema.sql → data.sql → 
model classes → repository → service interfaces → 
service implementations → controllers → security → 
frontend templates → tests → CI/CD → README.md

Ensure the application compiles and runs without errors.
The complete system should demonstrate a DISTINCTION-level 
submission for CIS6003 Advanced Programming.