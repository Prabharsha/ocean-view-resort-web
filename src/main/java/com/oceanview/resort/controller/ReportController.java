package com.oceanview.resort.controller;

import com.oceanview.resort.service.interfaces.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for report generation.
 *
 * <p>Provides endpoints for monthly, weekly, occupancy, and revenue reports.
 * Reports return JSON data; PDF download is available for the monthly report.</p>
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports", description = "Report generation endpoints")
public class ReportController {

    private final ReportService reportService;

    /**
     * Generates a monthly report for the given year and month.
     */
    @GetMapping("/monthly/{year}/{month}")
    @Operation(summary = "Monthly Report",
            description = "Generate a monthly report for the given year and month")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month) {
        log.info("Generating monthly report: {}/{}", year, month);
        Map<String, Object> report = reportService.generateMonthlyReport(year, month);
        return ResponseEntity.ok(report);
    }

    /**
     * Generates a weekly report for the given year and ISO week number.
     */
    @GetMapping("/weekly/{year}/{week}")
    @Operation(summary = "Weekly Report",
            description = "Generate a weekly report for the given year and week number")
    public ResponseEntity<Map<String, Object>> getWeeklyReport(
            @PathVariable int year,
            @PathVariable int week) {
        log.info("Generating weekly report: {} W{}", year, week);
        Map<String, Object> report = reportService.generateWeeklyReport(year, week);
        return ResponseEntity.ok(report);
    }

    /**
     * Generates a current occupancy report.
     */
    @GetMapping("/occupancy")
    @Operation(summary = "Occupancy Report",
            description = "Generate a current room occupancy report")
    public ResponseEntity<Map<String, Object>> getOccupancyReport() {
        log.info("Generating occupancy report");
        Map<String, Object> report = reportService.generateOccupancyReport();
        return ResponseEntity.ok(report);
    }

    /**
     * Generates a revenue report for the current year.
     */
    @GetMapping("/revenue")
    @Operation(summary = "Revenue Report",
            description = "Generate an annual revenue report")
    public ResponseEntity<Map<String, Object>> getRevenueReport() {
        log.info("Generating revenue report");
        Map<String, Object> report = reportService.generateRevenueReport();
        return ResponseEntity.ok(report);
    }

    /**
     * Downloads the monthly report as a PDF.
     */
    @GetMapping("/monthly/pdf")
    @Operation(summary = "Monthly Report PDF",
            description = "Download monthly report as a PDF file")
    public ResponseEntity<byte[]> downloadMonthlyReportPDF(
            @RequestParam int year,
            @RequestParam int month) {
        log.info("Generating monthly report PDF: {}/{}", year, month);
        byte[] pdf = reportService.generateMonthlyReportPDF(year, month);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                String.format("monthly-report-%d-%02d.pdf", year, month));
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    /**
     * Returns advanced metrics including revenue by room type,
     * top room types, average stay length, and cancellation rate.
     */
    @GetMapping("/advanced-metrics")
    @Operation(summary = "Advanced Metrics",
            description = "Generate advanced analytics: revenue by room type, top room types, avg stay")
    public ResponseEntity<Map<String, Object>> getAdvancedMetrics() {
        log.info("Generating advanced metrics report");
        Map<String, Object> metrics = reportService.generateAdvancedMetrics();
        return ResponseEntity.ok(metrics);
    }
}
