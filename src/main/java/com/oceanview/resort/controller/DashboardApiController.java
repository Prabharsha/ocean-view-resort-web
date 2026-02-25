package com.oceanview.resort.controller;

import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.repository.BillRepository;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.RoomRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller and WebSocket broadcaster for admin dashboard metrics.
 *
 * <p>Provides real-time operational metrics for the hotel management dashboard.
 * Metrics are available via REST API and also broadcast via WebSocket
 * every 30 seconds for live-updating dashboards.</p>
 *
 * <p><b>WebSocket topic:</b> {@code /topic/dashboard-metrics}</p>
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard", description = "Admin dashboard metrics endpoints")
public class DashboardApiController {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final BillRepository billRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Returns a comprehensive snapshot of dashboard metrics.
     */
    @GetMapping("/metrics")
    @Operation(summary = "Dashboard Metrics",
            description = "Returns a comprehensive dashboard metrics snapshot")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        return ResponseEntity.ok(buildMetrics());
    }

    /**
     * Broadcasts dashboard metrics to all connected WebSocket clients
     * every 30 seconds.
     */
    @Scheduled(fixedRate = 30000)
    public void broadcastMetrics() {
        try {
            Map<String, Object> metrics = buildMetrics();
            messagingTemplate.convertAndSend("/topic/dashboard-metrics", metrics);
            log.debug("Dashboard metrics broadcast via WebSocket");
        } catch (Exception e) {
            log.warn("Failed to broadcast dashboard metrics: {}", e.getMessage());
        }
    }

    /**
     * Builds the complete metrics map.
     */
    private Map<String, Object> buildMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();

        // Room statistics
        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.countByIsAvailableTrue();
        long occupiedRooms = totalRooms - availableRooms;
        double occupancyRate = totalRooms > 0
                ? (double) occupiedRooms / totalRooms * 100.0 : 0.0;

        metrics.put("totalRooms", totalRooms);
        metrics.put("availableRooms", availableRooms);
        metrics.put("occupiedRooms", occupiedRooms);
        metrics.put("occupancyRate", String.format("%.1f%%", occupancyRate));

        // Today's check-ins and check-outs
        LocalDate today = LocalDate.now();
        long todayCheckIns = reservationRepository.findByCheckInDateBetween(today, today)
                .stream().filter(r -> r.getStatus() == ReservationStatus.CONFIRMED
                        || r.getStatus() == ReservationStatus.CHECKED_IN)
                .count();
        long todayCheckOuts = reservationRepository.findAll().stream()
                .filter(r -> r.getCheckOutDate() != null && r.getCheckOutDate().equals(today))
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN
                        || r.getStatus() == ReservationStatus.CHECKED_OUT)
                .count();

        metrics.put("todayCheckIns", todayCheckIns);
        metrics.put("todayCheckOuts", todayCheckOuts);

        // Reservation status breakdown
        Map<String, Long> statusBreakdown = new LinkedHashMap<>();
        for (ReservationStatus status : ReservationStatus.values()) {
            long count = reservationRepository.findByStatus(status).size();
            statusBreakdown.put(status.name(), count);
        }
        metrics.put("reservationsByStatus", statusBreakdown);

        // Revenue summary
        BigDecimal totalPaidRevenue = billRepository.findByPaymentStatus("PAID").stream()
                .map(b -> b.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPendingRevenue = billRepository.findByPaymentStatus("UNPAID").stream()
                .map(b -> b.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.put("totalPaidRevenue", totalPaidRevenue);
        metrics.put("totalPendingRevenue", totalPendingRevenue);

        // Room type distribution
        Map<String, Long> roomTypeDistribution = roomRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRoomType().getDisplayName(),
                        Collectors.counting()));
        metrics.put("roomTypeDistribution", roomTypeDistribution);

        metrics.put("generatedAt", java.time.LocalDateTime.now().toString());

        return metrics;
    }
}
