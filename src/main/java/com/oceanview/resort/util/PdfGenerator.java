package com.oceanview.resort.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Utility class for generating PDF documents using iText 8.
 *
 * <p>Produces professional-looking PDFs for:
 * <ul>
 *   <li>Reservation confirmations</li>
 *   <li>Bill receipts / invoices</li>
 *   <li>Monthly reports</li>
 * </ul>
 *
 * <p>All methods return a {@code byte[]} that can be served directly
 * as an HTTP response body or attached to an email.</p>
 */
@Component
@Slf4j
public class PdfGenerator {

    /** Ocean blue brand colour. */
    private static final DeviceRgb BRAND_COLOUR = new DeviceRgb(0, 119, 182);
    private static final DeviceRgb HEADER_BG = new DeviceRgb(2, 62, 138);
    private static final DeviceRgb LIGHT_BG = new DeviceRgb(202, 240, 248);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Generates a reservation confirmation PDF.
     *
     * @param reservationNumber the unique reservation reference
     * @param guestName         the guest's full name
     * @param roomNumber        the assigned room number
     * @param roomType          the room type name
     * @param checkIn           check-in date
     * @param checkOut          check-out date
     * @param numGuests         number of guests
     * @return the PDF as a byte array
     */
    public byte[] generateReservationConfirmation(String reservationNumber, String guestName,
                                                   String roomNumber, String roomType,
                                                   LocalDate checkIn, LocalDate checkOut,
                                                   int numGuests) {
        log.info("Generating reservation confirmation PDF for: {}", reservationNumber);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            // Header
            addHeader(doc, "RESERVATION CONFIRMATION");

            // Reservation details table
            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 60}));
            table.setWidth(UnitValue.createPercentValue(100));

            addTableRow(table, "Reservation #", reservationNumber);
            addTableRow(table, "Guest Name", guestName);
            addTableRow(table, "Room Number", roomNumber);
            addTableRow(table, "Room Type", roomType);
            addTableRow(table, "Check-in Date", checkIn.format(DATE_FMT));
            addTableRow(table, "Check-out Date", checkOut.format(DATE_FMT));
            addTableRow(table, "Number of Guests", String.valueOf(numGuests));

            doc.add(table);

            // Footer
            addFooter(doc);
            doc.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate reservation confirmation PDF: {}", e.getMessage(), e);
            return createFallbackPdf("Reservation Confirmation - " + reservationNumber);
        }
    }

    /**
     * Generates a bill / invoice PDF.
     *
     * @param reservationNumber the reservation reference
     * @param guestName         the guest's name
     * @param numNights         number of nights
     * @param roomRate          nightly room rate
     * @param subtotal          subtotal amount
     * @param taxRate           tax percentage
     * @param taxAmount         calculated tax amount
     * @param discountAmount    discount applied
     * @param totalAmount       final total
     * @param paymentStatus     payment status string
     * @return the PDF as a byte array
     */
    public byte[] generateBillPdf(String reservationNumber, String guestName,
                                   int numNights, BigDecimal roomRate,
                                   BigDecimal subtotal, BigDecimal taxRate,
                                   BigDecimal taxAmount, BigDecimal discountAmount,
                                   BigDecimal totalAmount, String paymentStatus) {
        log.info("Generating bill PDF for reservation: {}", reservationNumber);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            // Header
            addHeader(doc, "BILL / INVOICE");

            // Bill details
            Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
            table.setWidth(UnitValue.createPercentValue(100));

            addTableRow(table, "Reservation #", reservationNumber);
            addTableRow(table, "Guest Name", guestName);
            addTableRow(table, "Number of Nights", String.valueOf(numNights));
            addTableRow(table, "Room Rate (per night)", "£" + roomRate.toPlainString());
            addTableRow(table, "Subtotal", "£" + subtotal.toPlainString());
            addTableRow(table, "Tax (" + taxRate.toPlainString() + "%)", "£" + taxAmount.toPlainString());
            addTableRow(table, "Discount", "£" + discountAmount.toPlainString());

            // Total row with emphasis
            Cell labelCell = new Cell().add(new Paragraph("TOTAL DUE")
                    .setBold().setFontSize(14));
            Cell valueCell = new Cell().add(new Paragraph("£" + totalAmount.toPlainString())
                    .setBold().setFontSize(14).setFontColor(BRAND_COLOUR));
            table.addCell(labelCell);
            table.addCell(valueCell);

            addTableRow(table, "Payment Status", paymentStatus);

            doc.add(table);

            addFooter(doc);
            doc.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate bill PDF: {}", e.getMessage(), e);
            return createFallbackPdf("Bill - " + reservationNumber);
        }
    }

    /**
     * Generates a monthly report PDF from report data.
     *
     * @param reportData the report data map
     * @return the PDF as a byte array
     */
    public byte[] generateReportPdf(Map<String, Object> reportData) {
        String reportType = String.valueOf(reportData.getOrDefault("reportType", "Report"));
        log.info("Generating {} PDF", reportType);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            // Header
            addHeader(doc, reportType.toUpperCase());

            // Report data table
            Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
            table.setWidth(UnitValue.createPercentValue(100));

            reportData.forEach((key, value) -> {
                if (!(value instanceof Map)) {
                    addTableRow(table, formatKey(key), String.valueOf(value));
                }
            });

            doc.add(table);

            // Sub-tables for nested maps
            reportData.forEach((key, value) -> {
                if (value instanceof Map) {
                    doc.add(new Paragraph("\n" + formatKey(key))
                            .setBold().setFontSize(12).setFontColor(BRAND_COLOUR));
                    Table subTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
                    subTable.setWidth(UnitValue.createPercentValue(100));
                    ((Map<?, ?>) value).forEach((k, v) ->
                            addTableRow(subTable, String.valueOf(k), String.valueOf(v)));
                    doc.add(subTable);
                }
            });

            addFooter(doc);
            doc.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate report PDF: {}", e.getMessage(), e);
            return createFallbackPdf(reportType);
        }
    }

    // ──────────────── Private helpers ────────────────

    private void addHeader(Document doc, String title) {
        Paragraph header = new Paragraph("OCEAN VIEW RESORT")
                .setFontSize(22)
                .setBold()
                .setFontColor(BRAND_COLOUR)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(header);

        Paragraph subtitle = new Paragraph(title)
                .setFontSize(16)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(subtitle);

        Paragraph dateLine = new Paragraph("Generated: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")))
                .setFontSize(9)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(dateLine);

        doc.add(new Paragraph("\n"));
    }

    private void addTableRow(Table table, String label, String value) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setBold().setFontSize(10))
                .setBackgroundColor(LIGHT_BG);
        Cell valueCell = new Cell()
                .add(new Paragraph(value != null ? value : "N/A").setFontSize(10));
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addFooter(Document doc) {
        doc.add(new Paragraph("\n"));
        Paragraph footer = new Paragraph(
                "Ocean View Resort | 123 Coastal Road, Cardiff CF10 1AB | " +
                "+44 (0)29 2034 5678 | info@oceanviewresort.com")
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(footer);

        Paragraph disclaimer = new Paragraph(
                "This is a computer-generated document and does not require a signature.")
                .setFontSize(7)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(disclaimer);
    }

    private String formatKey(String key) {
        // Convert camelCase to Title Case with spaces
        StringBuilder sb = new StringBuilder();
        for (char c : key.toCharArray()) {
            if (Character.isUpperCase(c) && sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(sb.length() == 0 ? Character.toUpperCase(c) : c);
        }
        return sb.toString();
    }

    /**
     * Creates a simple fallback text-based PDF when iText fails.
     */
    private byte[] createFallbackPdf(String title) {
        String content = "OCEAN VIEW RESORT\n" +
                "==================\n" +
                title + "\n" +
                "Generated: " + LocalDateTime.now() + "\n" +
                "(PDF generation encountered an issue. Contact support.)\n";
        return content.getBytes();
    }
}
