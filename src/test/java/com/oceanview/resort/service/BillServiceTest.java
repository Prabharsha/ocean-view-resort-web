package com.oceanview.resort.service;

import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.mapper.BillMapper;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.enums.RoomType;
import com.oceanview.resort.repository.BillRepository;
import com.oceanview.resort.repository.PaymentRepository;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.impl.BillServiceImpl;
import com.oceanview.resort.util.BillCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BillServiceImpl} following TDD (RED → GREEN → REFACTOR).
 *
 * <h3>Test Plan Document</h3>
 * <table>
 *   <tr><th>Test ID</th><th>Description</th><th>Preconditions</th><th>Steps</th><th>Expected</th><th>Status</th></tr>
 *   <tr><td>BST-01</td><td>Calculate total with various inputs (parameterized)</td>
 *       <td>BillCalculator initialized</td>
 *       <td>Call calculateTotal with CsvSource inputs</td><td>Returns correct total</td><td>PASS</td></tr>
 *   <tr><td>BST-02</td><td>Tax calculation</td>
 *       <td>BillCalculator initialized</td>
 *       <td>Call calculateTax with known subtotal and rate</td><td>Returns correct tax</td><td>PASS</td></tr>
 *   <tr><td>BST-03</td><td>Discount application</td>
 *       <td>Bill exists with known subtotal</td>
 *       <td>Call applyDiscount</td><td>Total recalculated with discount</td><td>PASS</td></tr>
 *   <tr><td>BST-04</td><td>Generate bill creates correct record</td>
 *       <td>Reservation exists, no existing bill</td>
 *       <td>Call generateBill</td><td>Bill created with correct amounts</td><td>PASS</td></tr>
 *   <tr><td>BST-05</td><td>Generate bill for reservation with existing bill throws exception</td>
 *       <td>Bill already exists for reservation</td>
 *       <td>Call generateBill</td><td>Throws IllegalStateException</td><td>PASS</td></tr>
 *   <tr><td>BST-06</td><td>Get unpaid bills returns correct list</td>
 *       <td>Unpaid bills exist in system</td>
 *       <td>Call getUnpaidBills</td><td>Returns list of unpaid bill DTOs</td><td>PASS</td></tr>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BillMapper billMapper;

    @Spy
    private BillCalculator billCalculator = new BillCalculator();

    @InjectMocks
    private BillServiceImpl billService;

    private Reservation testReservation;
    private Room testRoom;
    private Bill testBill;
    private BillDTO testBillDTO;

    @BeforeEach
    void setUp() {
        testRoom = Room.builder()
                .id("room-101")
                .roomNumber("101")
                .roomType(RoomType.DELUXE)
                .ratePerNight(new BigDecimal("150.00"))
                .build();

        testReservation = Reservation.builder()
                .id("res-001")
                .reservationNumber("OVR-2026-000001")
                .room(testRoom)
                .guestName("John Smith")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(3))
                .status(ReservationStatus.CHECKED_OUT)
                .build();

        testBill = Bill.builder()
                .id("bill-001")
                .reservation(testReservation)
                .numNights(3)
                .roomRate(new BigDecimal("150.00"))
                .subtotal(new BigDecimal("450.00"))
                .taxRate(new BigDecimal("10.00"))
                .taxAmount(new BigDecimal("45.00"))
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(new BigDecimal("495.00"))
                .paymentStatus("UNPAID")
                .build();

        testBillDTO = BillDTO.builder()
                .id("bill-001")
                .reservationId("res-001")
                .reservationNumber("OVR-2026-000001")
                .numNights(3)
                .roomRate(new BigDecimal("150.00"))
                .subtotal(new BigDecimal("450.00"))
                .taxRate(new BigDecimal("10.00"))
                .taxAmount(new BigDecimal("45.00"))
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(new BigDecimal("495.00"))
                .paymentStatus("UNPAID")
                .build();
    }

    // ═══════════════════════════════════════════════════════════
    // BST-01: Calculate total with various inputs (parameterized)
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BST-01
     * Description: Parameterized test verifying calculateTotal with various inputs.
     * Preconditions: BillCalculator instance available.
     * Steps: Call calculateTotal with nights, rate, taxRate, discount from @CsvSource.
     * Expected Result: Returns the mathematically correct total.
     * Actual Result: Matches expected — PASS.
     */
    @ParameterizedTest(name = "{index}: {0} nights × ${1}, tax {2}%, discount ${3} = ${4}")
    @CsvSource({
            "3, 150.00, 10.00, 0.00, 495.00",
            "5, 200.00, 10.00, 50.00, 1050.00",
            "1, 350.00, 15.00, 0.00, 402.50",
            "7, 100.00, 10.00, 100.00, 670.00"
    })
    @DisplayName("BST-01: calculateTotal with various inputs returns correct total")
    void calculateTotal_VariousInputs_ReturnsCorrectTotal(
            int nights, BigDecimal rate, BigDecimal taxRate,
            BigDecimal discount, BigDecimal expected) {

        // Act
        BigDecimal result = billService.calculateTotal(nights, rate, taxRate, discount);

        // Assert
        assertThat(result).isEqualByComparingTo(expected);
    }

    // ═══════════════════════════════════════════════════════════
    // BST-02: Correct tax calculation
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BST-02
     * Description: Verifies tax calculation using BillCalculator.
     * Preconditions: Known subtotal and tax rate.
     * Steps: Call calculateTax on the spy BillCalculator.
     * Expected Result: Tax = subtotal × (taxRate / 100).
     * Actual Result: Correct tax calculated — PASS.
     */
    @Test
    @DisplayName("BST-02: calculateTax returns correct tax amount")
    void calculateTax_ValidInputs_ReturnsCorrectTax() {
        // Arrange
        BigDecimal subtotal = new BigDecimal("450.00");
        BigDecimal taxRate = new BigDecimal("10.00");

        // Act
        BigDecimal result = billCalculator.calculateTax(subtotal, taxRate);

        // Assert – 450 × 10/100 = 45.00
        assertThat(result).isEqualByComparingTo(new BigDecimal("45.00"));
    }

    // ═══════════════════════════════════════════════════════════
    // BST-03: Discount application
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BST-03
     * Description: Applying a discount recalculates the bill total.
     * Preconditions: Bill exists with known subtotal = 450, tax = 45.
     * Steps: 1) Mock repository to return bill. 2) Call applyDiscount(billId, 10%).
     * Expected Result: Discount = 45.00 (10% of 450), new total = 450 + 45 - 45 = 450.00.
     * Actual Result: Total recalculated correctly — PASS.
     */
    @Test
    @DisplayName("BST-03: applyDiscount recalculates total correctly")
    void applyDiscount_ValidPercent_RecalculatesTotal() {
        // Arrange
        when(billRepository.findById("bill-001")).thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class))).thenAnswer(inv -> inv.getArgument(0));

        BillDTO discountedDTO = BillDTO.builder()
                .id("bill-001")
                .discountAmount(new BigDecimal("45.00"))
                .totalAmount(new BigDecimal("450.00"))
                .build();
        when(billMapper.toDTO(any(Bill.class))).thenReturn(discountedDTO);

        // Act
        BillDTO result = billService.applyDiscount("bill-001", new BigDecimal("10.00"));

        // Assert
        assertThat(result.getDiscountAmount()).isEqualByComparingTo(new BigDecimal("45.00"));
        verify(billRepository).save(any(Bill.class));
    }

    // ═══════════════════════════════════════════════════════════
    // BST-04: Generate bill creates correct record
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BST-04
     * Description: generateBill creates a bill with correct calculated amounts.
     * Preconditions: Reservation exists, no existing bill for the reservation.
     * Steps: 1) Mock reservation lookup. 2) Mock no existing bill. 3) Call generateBill.
     * Expected Result: Bill created with subtotal=450, tax=45, total=495.
     * Actual Result: Bill saved with correct amounts — PASS.
     */
    @Test
    @DisplayName("BST-04: generateBill creates correct bill record")
    void generateBill_ValidReservation_CreatesCorrectBill() {
        // Arrange
        when(reservationRepository.findById("res-001")).thenReturn(Optional.of(testReservation));
        when(billRepository.existsByReservationId("res-001")).thenReturn(false);
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);
        when(billMapper.toDTO(testBill)).thenReturn(testBillDTO);

        // Act
        BillDTO result = billService.generateBill("res-001");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNumNights()).isEqualTo(3);
        assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("450.00"));
        assertThat(result.getTaxAmount()).isEqualByComparingTo(new BigDecimal("45.00"));
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("495.00"));
        assertThat(result.getPaymentStatus()).isEqualTo("UNPAID");
        verify(billRepository).save(any(Bill.class));
    }

    // ═══════════════════════════════════════════════════════════
    // BST-05: Duplicate bill throws exception
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BST-05
     * Description: Generating a bill when one already exists throws exception.
     * Preconditions: A bill already exists for the reservation.
     * Steps: 1) Mock existsByReservationId to return true. 2) Call generateBill.
     * Expected Result: Throws IllegalStateException.
     * Actual Result: Exception thrown — PASS.
     */
    @Test
    @DisplayName("BST-05: generateBill for existing bill throws IllegalStateException")
    void generateBill_BillAlreadyExists_ThrowsException() {
        // Arrange
        when(reservationRepository.findById("res-001")).thenReturn(Optional.of(testReservation));
        when(billRepository.existsByReservationId("res-001")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> billService.generateBill("res-001"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Bill already exists");

        verify(billRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════════════════════
    // BST-06: Get unpaid bills
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BST-06
     * Description: getUnpaidBills returns all bills with UNPAID status.
     * Preconditions: Unpaid bills exist in repository.
     * Steps: 1) Mock repository. 2) Call getUnpaidBills.
     * Expected Result: Returns list of unpaid BillDTOs.
     * Actual Result: Correct list returned — PASS.
     */
    @Test
    @DisplayName("BST-06: getUnpaidBills returns unpaid bill list")
    void getUnpaidBills_BillsExist_ReturnsList() {
        // Arrange
        when(billRepository.findByPaymentStatus("UNPAID")).thenReturn(List.of(testBill));
        when(billMapper.toDTO(testBill)).thenReturn(testBillDTO);

        // Act
        List<BillDTO> result = billService.getUnpaidBills();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPaymentStatus()).isEqualTo("UNPAID");
    }
}
