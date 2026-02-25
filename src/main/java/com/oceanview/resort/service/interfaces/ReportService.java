package com.oceanview.resort.service.interfaces;

import java.util.Map;

/**
 * Service interface for report generation.
 * Provides methods for generating monthly, weekly, occupancy, and revenue reports.
 */
public interface ReportService {

    /**
     * Generates a monthly report for the given year and month.
     */
    Map<String, Object> generateMonthlyReport(int year, int month);

    /**
     * Generates a weekly report for the given year and ISO week number.
     */
    Map<String, Object> generateWeeklyReport(int year, int week);

    /**
     * Generates an occupancy report.
     */
    Map<String, Object> generateOccupancyReport();

    /**
     * Generates a revenue report.
     */
    Map<String, Object> generateRevenueReport();

    /**
     * Generates a monthly report as a PDF.
     */
    byte[] generateMonthlyReportPDF(int year, int month);

    /**
     * Generates advanced metrics report including revenue by room type,
     * top room types by booking frequency, and average stay length.
     *
     * @return map of advanced metric data
     */
    Map<String, Object> generateAdvancedMetrics();
}
