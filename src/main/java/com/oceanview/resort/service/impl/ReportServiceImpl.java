package com.oceanview.resort.service.impl;

import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.repository.BillRepository;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.service.interfaces.ReportService;
import com.oceanview.resort.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ReportService}.
 *
 * <p>Generates monthly, weekly, occupancy, and revenue reports by aggregating
 * data from reservations, rooms, and bills. PDF generation delegates to
 * JasperReports (placeholder for Task 12).</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final BillRepository billRepository;
    private final PdfGenerator pdfGenerator;

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> generateMonthlyReport(int year, int month) {
        log.info("Generating monthly report for {}/{}", year, month);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Reservation> reservations = reservationRepository
                .findByCheckInDateBetween(startDate, endDate);

        long totalReservations = reservations.size();
        long confirmedCount = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();
        long cancelledCount = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .count();
        long completedCount = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_OUT)
                .count();

        BigDecimal totalRevenue = reservations.stream()
                .filter(r -> r.getBill() != null)
                .map(r -> r.getBill().getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalNights = reservations.stream()
                .mapToLong(Reservation::getNumberOfNights)
                .sum();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportType", "Monthly Report");
        report.put("year", year);
        report.put("month", month);
        report.put("period", yearMonth.toString());
        report.put("totalReservations", totalReservations);
        report.put("confirmedReservations", confirmedCount);
        report.put("cancelledReservations", cancelledCount);
        report.put("completedReservations", completedCount);
        report.put("totalRevenue", totalRevenue);
        report.put("totalNights", totalNights);
        report.put("averageRevenuePerReservation",
                totalReservations > 0
                        ? totalRevenue.divide(BigDecimal.valueOf(totalReservations), 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);
        report.put("generatedAt", LocalDate.now().toString());

        log.info("Monthly report generated: {} reservations, revenue: {}",
                totalReservations, totalRevenue);
        return report;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> generateWeeklyReport(int year, int week) {
        log.info("Generating weekly report for year {} week {}", year, week);

        // Calculate start and end dates for the ISO week
        LocalDate startDate = LocalDate.ofYearDay(year, 1)
                .with(java.time.temporal.WeekFields.ISO.weekOfYear(), week)
                .with(java.time.DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);

        List<Reservation> reservations = reservationRepository
                .findByCheckInDateBetween(startDate, endDate);

        long totalReservations = reservations.size();
        BigDecimal totalRevenue = reservations.stream()
                .filter(r -> r.getBill() != null)
                .map(r -> r.getBill().getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportType", "Weekly Report");
        report.put("year", year);
        report.put("week", week);
        report.put("periodStart", startDate.toString());
        report.put("periodEnd", endDate.toString());
        report.put("totalReservations", totalReservations);
        report.put("totalRevenue", totalRevenue);
        report.put("averageRevenuePerDay",
                totalRevenue.divide(BigDecimal.valueOf(7), 2, RoundingMode.HALF_UP));
        report.put("generatedAt", LocalDate.now().toString());

        log.info("Weekly report generated: {} reservations", totalReservations);
        return report;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> generateOccupancyReport() {
        log.info("Generating occupancy report");

        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.countByIsAvailableTrue();
        long occupiedRooms = totalRooms - availableRooms;
        double occupancyRate = totalRooms > 0
                ? (double) occupiedRooms / totalRooms * 100.0
                : 0.0;

        // Room type breakdown
        Map<String, Long> roomTypeBreakdown = roomRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRoomType().name(),
                        Collectors.counting()));

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportType", "Occupancy Report");
        report.put("totalRooms", totalRooms);
        report.put("availableRooms", availableRooms);
        report.put("occupiedRooms", occupiedRooms);
        report.put("occupancyRate", String.format("%.1f%%", occupancyRate));
        report.put("roomTypeBreakdown", roomTypeBreakdown);
        report.put("generatedAt", LocalDate.now().toString());

        log.info("Occupancy report: {}/{} rooms occupied ({}%)",
                occupiedRooms, totalRooms, String.format("%.1f", occupancyRate));
        return report;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> generateRevenueReport() {
        log.info("Generating revenue report");

        int currentYear = LocalDate.now().getYear();

        // Monthly revenue for current year
        Map<String, BigDecimal> monthlyRevenue = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
            YearMonth ym = YearMonth.of(currentYear, month);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            BigDecimal revenue = reservationRepository
                    .findByCheckInDateBetween(start, end).stream()
                    .filter(r -> r.getBill() != null)
                    .map(r -> r.getBill().getTotalAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlyRevenue.put(ym.toString(), revenue);
        }

        BigDecimal totalAnnualRevenue = monthlyRevenue.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Paid vs unpaid summary
        BigDecimal paidRevenue = billRepository.findByPaymentStatus("PAID").stream()
                .map(b -> b.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal unpaidRevenue = billRepository.findByPaymentStatus("UNPAID").stream()
                .map(b -> b.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportType", "Revenue Report");
        report.put("year", currentYear);
        report.put("totalAnnualRevenue", totalAnnualRevenue);
        report.put("monthlyRevenue", monthlyRevenue);
        report.put("paidRevenue", paidRevenue);
        report.put("unpaidRevenue", unpaidRevenue);
        report.put("generatedAt", LocalDate.now().toString());

        log.info("Revenue report: total annual = {}", totalAnnualRevenue);
        return report;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] generateMonthlyReportPDF(int year, int month) {
        log.info("Generating monthly report PDF for {}/{}", year, month);

        Map<String, Object> reportData = generateMonthlyReport(year, month);
        return pdfGenerator.generateReportPdf(reportData);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> generateAdvancedMetrics() {
        log.info("Generating advanced metrics report");

        List<Reservation> allReservations = reservationRepository.findAll();

        // Revenue by room type
        Map<String, BigDecimal> revenueByRoomType = allReservations.stream()
                .filter(r -> r.getBill() != null && r.getRoom() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getRoom().getRoomType().getDisplayName(),
                        Collectors.mapping(
                                r -> r.getBill().getTotalAmount(),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        // Top room types by booking frequency
        Map<String, Long> topRoomTypes = allReservations.stream()
                .filter(r -> r.getRoom() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getRoom().getRoomType().getDisplayName(),
                        Collectors.counting()
                ));

        // Average length of stay
        double avgStayLength = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_OUT)
                .mapToLong(Reservation::getNumberOfNights)
                .average()
                .orElse(0.0);

        // Check-in / check-out statistics for today
        LocalDate today = LocalDate.now();
        long checkInsToday = allReservations.stream()
                .filter(r -> r.getCheckInDate() != null && r.getCheckInDate().equals(today))
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN
                        || r.getStatus() == ReservationStatus.CONFIRMED)
                .count();
        long checkOutsToday = allReservations.stream()
                .filter(r -> r.getCheckOutDate() != null && r.getCheckOutDate().equals(today))
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN
                        || r.getStatus() == ReservationStatus.CHECKED_OUT)
                .count();

        // Cancellation rate
        long totalReservations = allReservations.size();
        long cancelledCount = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .count();
        double cancellationRate = totalReservations > 0
                ? (double) cancelledCount / totalReservations * 100.0 : 0.0;

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("reportType", "Advanced Metrics Report");
        metrics.put("revenueByRoomType", revenueByRoomType);
        metrics.put("topRoomTypesByBookings", topRoomTypes);
        metrics.put("averageStayLengthNights", String.format("%.1f", avgStayLength));
        metrics.put("checkInsToday", checkInsToday);
        metrics.put("checkOutsToday", checkOutsToday);
        metrics.put("cancellationRate", String.format("%.1f%%", cancellationRate));
        metrics.put("totalReservations", totalReservations);
        metrics.put("generatedAt", LocalDate.now().toString());

        log.info("Advanced metrics generated: {} room types, avg stay: {} nights",
                revenueByRoomType.size(), String.format("%.1f", avgStayLength));
        return metrics;
    }
}
