package com.oceanview.resort.controller;

import com.oceanview.resort.model.LoyaltyTransaction;
import com.oceanview.resort.model.enums.LoyaltyTier;
import com.oceanview.resort.service.interfaces.LoyaltyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for the Ocean View Resort loyalty points system.
 *
 * <p>Provides endpoints for checking loyalty balance, tier status,
 * points history, and point redemption.</p>
 */
@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Loyalty", description = "Loyalty points management endpoints")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    /**
     * Gets the loyalty balance and tier for a customer.
     */
    @GetMapping("/balance/{customerId}")
    @Operation(summary = "Get Loyalty Balance",
            description = "Returns the customer's loyalty points balance and current tier")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable String customerId) {
        log.info("Getting loyalty balance for customer: {}", customerId);

        int balance = loyaltyService.getBalance(customerId);
        LoyaltyTier tier = loyaltyService.getTier(customerId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("customerId", customerId);
        response.put("balance", balance);
        response.put("tier", tier.name());
        response.put("tierDisplayName", tier.getDisplayName());
        response.put("benefits", tier.getBenefits());

        // Calculate points to next tier
        LoyaltyTier[] tiers = LoyaltyTier.values();
        int currentIdx = tier.ordinal();
        if (currentIdx < tiers.length - 1) {
            LoyaltyTier next = tiers[currentIdx + 1];
            response.put("nextTier", next.getDisplayName());
            response.put("pointsToNextTier", next.getMinimumPoints() - balance);
        } else {
            response.put("nextTier", "Maximum tier reached");
            response.put("pointsToNextTier", 0);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Gets the loyalty transaction history for a customer.
     */
    @GetMapping("/history/{customerId}")
    @Operation(summary = "Loyalty History",
            description = "Returns the customer's loyalty points transaction history")
    public ResponseEntity<List<LoyaltyTransaction>> getHistory(@PathVariable String customerId) {
        log.info("Getting loyalty history for customer: {}", customerId);
        List<LoyaltyTransaction> history = loyaltyService.getHistory(customerId);
        return ResponseEntity.ok(history);
    }

    /**
     * Redeems loyalty points for a customer.
     */
    @PostMapping("/redeem")
    @Operation(summary = "Redeem Points",
            description = "Redeems loyalty points from a customer's balance")
    public ResponseEntity<Map<String, Object>> redeemPoints(
            @RequestParam String customerId,
            @RequestParam int points,
            @RequestParam(required = false) String description) {
        log.info("Redeeming {} points for customer: {}", points, customerId);

        String desc = description != null ? description : "Manual points redemption";
        loyaltyService.redeemPoints(customerId, points, desc, null);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Points redeemed successfully");
        response.put("pointsRedeemed", points);
        response.put("newBalance", loyaltyService.getBalance(customerId));
        response.put("currentTier", loyaltyService.getTier(customerId).getDisplayName());

        return ResponseEntity.ok(response);
    }
}
