package com.oceanview.resort.servlet.api;

import com.oceanview.resort.dao.BillDAO;
import com.oceanview.resort.dao.RoomDAO;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.service.ReservationService;
import com.oceanview.resort.service.RoomService;
import com.oceanview.resort.servlet.BaseApiServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST servlet for dashboard statistics.
 *
 * Endpoints:
 *   GET /api/dashboard/stats   – basic stats (backward-compat)
 *   GET /api/dashboard/metrics – full metrics snapshot
 *   GET /api/dashboard/revenue?months=N – chart data
 */
public class DashboardApiServlet extends BaseApiServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathParam = getPathParam(req);

        try {
            if (pathParam == null || pathParam.equals("stats")) {
                handleStats(req, resp);
            } else if (pathParam.equals("metrics")) {
                handleMetrics(req, resp);
            } else if (pathParam.equals("revenue")) {
                handleRevenueChart(req, resp);
            } else {
                sendError(resp, 404, "Unknown dashboard endpoint");
            }
        } catch (Exception e) {
            sendError(resp, 500, e.getMessage());
        }
    }

    // ── /api/dashboard/stats (backward-compat) ──────────────────────────
    private void handleStats(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReservationService resService = getService(req, "reservationService");
        RoomService roomService = getService(req, "roomService");

        List<Reservation> all = resService.findAll();
        LocalDate today = LocalDate.now();

        long totalReservations = all.size();
        long totalRooms        = roomService.count();
        long availableRooms    = roomService.countAvailable();

        long checkInsToday = all.stream()
                .filter(r -> today.equals(r.getCheckInDate()))
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN
                          || r.getStatus() == ReservationStatus.CONFIRMED)
                .count();
        long checkOutsToday = all.stream()
                .filter(r -> today.equals(r.getCheckOutDate()))
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN
                          || r.getStatus() == ReservationStatus.CHECKED_OUT)
                .count();
        long pendingCount = all.stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING).count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalReservations", totalReservations);
        stats.put("totalRooms",        totalRooms);
        stats.put("availableRooms",    availableRooms);
        stats.put("checkInsToday",     checkInsToday);
        stats.put("checkOutsToday",    checkOutsToday);
        stats.put("pendingCount",      pendingCount);
        sendJson(resp, stats);
    }

    // ── /api/dashboard/metrics ─────────────────────────────────────────
    private void handleMetrics(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReservationService resService = getService(req, "reservationService");
        RoomService        roomService = getService(req, "roomService");
        BillDAO            billDAO    = getService(req, "billDAO");

        Map<String, Object> metrics = new LinkedHashMap<>();

        // Room statistics
        long totalRooms     = roomService.count();
        long availableRooms = roomService.countAvailable();
        long occupiedRooms  = totalRooms - availableRooms;
        double occupancyRate = totalRooms > 0
                ? (double) occupiedRooms / totalRooms * 100.0 : 0.0;

        metrics.put("totalRooms",     totalRooms);
        metrics.put("availableRooms", availableRooms);
        metrics.put("occupiedRooms",  occupiedRooms);
        metrics.put("occupancyRate",  String.format("%.1f%%", occupancyRate));

        // Today's check-ins / check-outs
        LocalDate today = LocalDate.now();
        List<Reservation> all = resService.findAll();

        long todayCheckIns = all.stream()
                .filter(r -> today.equals(r.getCheckInDate()))
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED
                          || r.getStatus() == ReservationStatus.CHECKED_IN)
                .count();
        long todayCheckOuts = all.stream()
                .filter(r -> today.equals(r.getCheckOutDate()))
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN
                          || r.getStatus() == ReservationStatus.CHECKED_OUT)
                .count();

        metrics.put("todayCheckIns",  todayCheckIns);
        metrics.put("todayCheckOuts", todayCheckOuts);

        // Reservation status breakdown
        Map<String, Long> statusBreakdown = new LinkedHashMap<>();
        for (ReservationStatus s : ReservationStatus.values()) {
            final ReservationStatus fs = s;
            long count = all.stream().filter(r -> r.getStatus() == fs).count();
            statusBreakdown.put(s.name(), count);
        }
        metrics.put("reservationsByStatus", statusBreakdown);

        // Revenue summary
        List<Bill> allBills = billDAO.findAll();
        BigDecimal totalPaidRevenue = allBills.stream()
                .filter(b -> "PAID".equals(b.getPaymentStatus()))
                .map(b -> b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPendingRevenue = allBills.stream()
                .filter(b -> "UNPAID".equals(b.getPaymentStatus()))
                .map(b -> b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.put("totalPaidRevenue",    totalPaidRevenue);
        metrics.put("totalPendingRevenue", totalPendingRevenue);

        // Room type distribution
        List<Room> allRooms = roomService.findAll();
        Map<String, Long> roomTypeDist = allRooms.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRoomType().getDisplayName(),
                        Collectors.counting()));
        metrics.put("roomTypeDistribution", roomTypeDist);

        // Recent reservations (last 8 for dashboard table)
        List<Map<String, Object>> recentList = all.stream()
                .sorted(Comparator.comparing(r -> r.getCreatedAt() != null
                        ? r.getCreatedAt() : java.time.LocalDateTime.MIN,
                        Comparator.reverseOrder()))
                .limit(8)
                .map(r -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id",                r.getId());
                    m.put("reservationNumber", r.getReservationNumber());
                    m.put("customerName",      r.getCustomerName() != null ? r.getCustomerName() : r.getGuestName());
                    m.put("roomNumber",        r.getRoomNumber());
                    m.put("checkInDate",       r.getCheckInDate() != null ? r.getCheckInDate().toString() : null);
                    m.put("status",            r.getStatus().name());
                    return m;
                })
                .collect(Collectors.toList());
        metrics.put("recentReservations", recentList);

        metrics.put("generatedAt", java.time.LocalDateTime.now().toString());
        sendJson(resp, metrics);
    }

    // ── /api/dashboard/revenue ─────────────────────────────────────────
    private void handleRevenueChart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int months = 12;
        try { months = Integer.parseInt(req.getParameter("months")); } catch (Exception ignored) {}

        RoomService roomService = getService(req, "roomService");
        BillDAO     billDAO    = getService(req, "billDAO");

        List<Bill>        allBills = billDAO.findAll();
        ReservationService resService = getService(req, "reservationService");
        List<Reservation> allRes  = resService.findAll();
        long totalRooms = Math.max(1, roomService.count());

        List<String>     labels    = new ArrayList<>();
        List<BigDecimal> revenue   = new ArrayList<>();
        List<Double>     occupancy = new ArrayList<>();

        YearMonth now = YearMonth.now();
        for (int i = months - 1; i >= 0; i--) {
            YearMonth ym    = now.minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end   = ym.atEndOfMonth();

            // Label e.g. "Mar 26"
            labels.add(start.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    + " " + String.format("%02d", ym.getYear() % 100));

            // Revenue: sum of PAID bills whose generatedAt falls in this month
            BigDecimal monthRevenue = allBills.stream()
                    .filter(b -> "PAID".equals(b.getPaymentStatus())
                            && b.getGeneratedAt() != null
                            && !b.getGeneratedAt().toLocalDate().isBefore(start)
                            && !b.getGeneratedAt().toLocalDate().isAfter(end))
                    .map(b -> b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenue.add(monthRevenue);

            // Occupancy: reservations overlapping this month (not cancelled)
            long occupied = allRes.stream()
                    .filter(r -> r.getStatus() != ReservationStatus.CANCELLED
                            && r.getCheckInDate() != null && r.getCheckOutDate() != null
                            && !r.getCheckInDate().isAfter(end)
                            && !r.getCheckOutDate().isBefore(start))
                    .count();
            occupancy.add(Math.min(100.0, Math.round((double) occupied / totalRooms * 100.0)));
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("labels",    labels);
        data.put("revenue",   revenue);
        data.put("occupancy", occupancy);
        sendJson(resp, data);
    }
}

