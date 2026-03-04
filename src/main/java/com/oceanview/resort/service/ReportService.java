package com.oceanview.resort.service;

import com.oceanview.resort.dao.*;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Layer for report generation.
 */
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final BillDAO billDAO;

    public ReportService(ReservationDAO reservationDAO, RoomDAO roomDAO, BillDAO billDAO) {
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
        this.billDAO = billDAO;
    }

    public Map<String, Object> generateMonthlyReport(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<Reservation> reservations = reservationDAO.findByCheckInDateBetween(startDate, endDate);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportType", "Monthly Report");
        report.put("year", year);
        report.put("month", month);
        report.put("period", yearMonth.toString());
        report.put("totalReservations", reservations.size());
        report.put("confirmedReservations", reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED).count());
        report.put("cancelledReservations", reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED).count());
        report.put("completedReservations", reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_OUT).count());
        report.put("generatedAt", LocalDate.now().toString());
        return report;
    }

    public Map<String, Object> generateWeeklyReport(int year, int week) {
        LocalDate startDate = LocalDate.ofYearDay(year, 1)
                .with(java.time.temporal.WeekFields.ISO.weekOfYear(), week)
                .with(java.time.DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);
        List<Reservation> reservations = reservationDAO.findByCheckInDateBetween(startDate, endDate);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportType", "Weekly Report");
        report.put("year", year);
        report.put("week", week);
        report.put("periodStart", startDate.toString());
        report.put("periodEnd", endDate.toString());
        report.put("totalReservations", reservations.size());
        report.put("generatedAt", LocalDate.now().toString());
        return report;
    }

    public Map<String, Object> generateOccupancyReport() {
        long totalRooms = roomDAO.count();
        long availableRooms = roomDAO.countAvailable();
        long occupiedRooms = totalRooms - availableRooms;
        double occupancyRate = totalRooms > 0 ? (double) occupiedRooms / totalRooms * 100.0 : 0.0;

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalRooms", totalRooms);
        report.put("availableRooms", availableRooms);
        report.put("occupiedRooms", occupiedRooms);
        report.put("occupancyRate", String.format("%.1f%%", occupancyRate));
        return report;
    }

    public Map<String, Object> generateRevenueReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportType", "Revenue Report");
        report.put("year", LocalDate.now().getYear());
        report.put("generatedAt", LocalDate.now().toString());
        return report;
    }

    public Map<String, Object> generateAdvancedMetrics() {
        List<Reservation> all = reservationDAO.findAll();
        LocalDate today = LocalDate.now();

        long checkInsToday = all.stream()
                .filter(r -> today.equals(r.getCheckInDate()))
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN || r.getStatus() == ReservationStatus.CONFIRMED)
                .count();
        long checkOutsToday = all.stream()
                .filter(r -> today.equals(r.getCheckOutDate()))
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN || r.getStatus() == ReservationStatus.CHECKED_OUT)
                .count();

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("checkInsToday", checkInsToday);
        metrics.put("checkOutsToday", checkOutsToday);
        metrics.put("totalReservations", all.size());
        metrics.put("pendingCount", all.stream().filter(r -> r.getStatus() == ReservationStatus.PENDING).count());
        return metrics;
    }
}

