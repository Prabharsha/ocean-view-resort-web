package com.oceanview.resort.controller;

import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.dto.DiscountRequest;
import com.oceanview.resort.dto.PaymentDTO;
import com.oceanview.resort.service.interfaces.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for bill management operations.
 *
 * <p>Handles bill generation, viewing, discount application, payment
 * recording, and PDF generation.</p>
 */
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bills", description = "Bill management endpoints")
public class BillController {

    private final BillService billService;

    /**
     * Generates a bill for a reservation.
     */
    @PostMapping("/generate/{reservationId}")
    @Operation(summary = "Generate Bill", description = "Generate a bill for a reservation")
    public ResponseEntity<BillDTO> generateBill(@PathVariable String reservationId) {
        log.info("Generating bill for reservation: {}", reservationId);
        BillDTO bill = billService.generateBill(reservationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bill);
    }

    /**
     * Returns a bill by its UUID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Bill", description = "Get a bill by its ID")
    public ResponseEntity<BillDTO> getBill(@PathVariable String id) {
        // Retrieve by reservation ID as the primary lookup
        BillDTO bill = billService.findBillByReservationId(id);
        return ResponseEntity.ok(bill);
    }

    /**
     * Returns a bill by reservation ID.
     */
    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Get Bill by Reservation",
            description = "Get a bill by its reservation ID")
    public ResponseEntity<BillDTO> getBillByReservation(@PathVariable String reservationId) {
        BillDTO bill = billService.findBillByReservationId(reservationId);
        return ResponseEntity.ok(bill);
    }

    /**
     * Applies a discount to a bill.
     */
    @PostMapping("/{id}/discount")
    @Operation(summary = "Apply Discount", description = "Apply a percentage discount to a bill")
    public ResponseEntity<BillDTO> applyDiscount(
            @PathVariable String id,
            @Valid @RequestBody DiscountRequest request) {
        log.info("Applying {}% discount to bill: {}", request.getDiscountPercent(), id);
        BillDTO bill = billService.applyDiscount(id, request.getDiscountPercent());
        return ResponseEntity.ok(bill);
    }

    /**
     * Downloads a bill as PDF.
     */
    @GetMapping("/{id}/pdf")
    @Operation(summary = "Download PDF", description = "Download bill as PDF")
    public ResponseEntity<byte[]> downloadPDF(@PathVariable String id) {
        byte[] pdf = billService.generateBillPDF(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "bill-" + id + ".pdf");
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    /**
     * Records a payment and marks the bill accordingly.
     */
    @PutMapping("/{id}/pay")
    @Operation(summary = "Pay Bill", description = "Record a payment against a bill")
    public ResponseEntity<Void> payBill(
            @PathVariable String id,
            @Valid @RequestBody PaymentDTO paymentDTO) {
        log.info("Processing payment for bill: {}", id);
        billService.markAsPaid(id, paymentDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * Returns all unpaid bills.
     */
    @GetMapping("/unpaid")
    @Operation(summary = "Unpaid Bills", description = "Get all unpaid bills")
    public ResponseEntity<List<BillDTO>> getUnpaidBills() {
        List<BillDTO> bills = billService.getUnpaidBills();
        return ResponseEntity.ok(bills);
    }
}
