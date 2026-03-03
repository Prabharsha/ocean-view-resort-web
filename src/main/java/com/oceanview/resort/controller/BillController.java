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
 */
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bills", description = "Bill management endpoints")
public class BillController {

    private final BillService billService;

    /** Returns all bills. */
    @GetMapping
    @Operation(summary = "All Bills", description = "Get all bills")
    public ResponseEntity<List<BillDTO>> getAllBills() {
        return ResponseEntity.ok(billService.getAllBills());
    }

    /**
     * Generates a bill for a reservation, or returns the existing one if already generated.
     */
    @PostMapping("/generate/{reservationId}")
    @Operation(summary = "Generate Bill", description = "Generate or retrieve existing bill for a reservation")
    public ResponseEntity<BillDTO> generateBill(@PathVariable String reservationId) {
        log.info("Generating bill for reservation: {}", reservationId);
        BillDTO bill = billService.generateOrGetBill(reservationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bill);
    }

    /**
     * Returns a bill by its bill ID (UUID or seeded ID like b-001).
     * Falls back to reservation-ID lookup if not found by bill ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Bill", description = "Get a bill by bill ID or reservation ID")
    public ResponseEntity<BillDTO> getBill(@PathVariable String id) {
        // Try direct bill lookup first
        try {
            BillDTO bill = billService.findBillById(id);
            return ResponseEntity.ok(bill);
        } catch (Exception e) {
            // Not found by bill id — try as reservation id
        }
        try {
            BillDTO bill = billService.findBillByReservationId(id);
            return ResponseEntity.ok(bill);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Returns a bill by its associated reservation ID. */
    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Get Bill by Reservation", description = "Get a bill by reservation ID")
    public ResponseEntity<BillDTO> getBillByReservation(@PathVariable String reservationId) {
        return ResponseEntity.ok(billService.findBillByReservationId(reservationId));
    }

    /** Applies a percentage discount to a bill. */
    @PostMapping("/{id}/discount")
    @Operation(summary = "Apply Discount", description = "Apply a percentage discount to a bill")
    public ResponseEntity<BillDTO> applyDiscount(
            @PathVariable String id,
            @Valid @RequestBody DiscountRequest request) {
        log.info("Applying {}% discount to bill: {}", request.getDiscountPercent(), id);
        return ResponseEntity.ok(billService.applyDiscount(id, request.getDiscountPercent()));
    }

    /** Downloads a bill as PDF. */
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
     * Records a payment against a bill and updates its payment status.
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

    /** Returns all unpaid bills. */
    @GetMapping("/unpaid")
    @Operation(summary = "Unpaid Bills", description = "Get all unpaid bills")
    public ResponseEntity<List<BillDTO>> getUnpaidBills() {
        return ResponseEntity.ok(billService.getUnpaidBills());
    }
}
