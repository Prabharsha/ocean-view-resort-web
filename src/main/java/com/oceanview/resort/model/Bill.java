package com.oceanview.resort.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Bill POJO representing a financial bill for a reservation.
 * All monetary fields use BigDecimal for precision.
 */
public class Bill {

    private String id;
    private String reservationId;
    private int numNights;
    private BigDecimal roomRate;
    private BigDecimal subtotal;
    private BigDecimal taxRate = new BigDecimal("10.00");
    private BigDecimal taxAmount;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal totalAmount;
    private String paymentStatus = "UNPAID";
    private LocalDateTime generatedAt;

    // Transient display fields
    private String reservationNumber;
    private String guestName;
    private String guestEmail;
    private String roomNumber;
    private String roomType;

    public Bill() {}

    // ── Getters & Setters ──
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public int getNumNights() { return numNights; }
    public void setNumNights(int numNights) { this.numNights = numNights; }

    public BigDecimal getRoomRate() { return roomRate; }
    public void setRoomRate(BigDecimal roomRate) { this.roomRate = roomRate; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
}

