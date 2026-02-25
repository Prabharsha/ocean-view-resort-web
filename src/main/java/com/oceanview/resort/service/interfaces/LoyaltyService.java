package com.oceanview.resort.service.interfaces;

import com.oceanview.resort.model.LoyaltyTransaction;
import com.oceanview.resort.model.enums.LoyaltyTier;

import java.util.List;

/**
 * Service interface for the Ocean View Resort loyalty points system.
 *
 * <p>Manages loyalty points earning, redemption, balance inquiries,
 * tier determination, and transaction history.</p>
 */
public interface LoyaltyService {

    /**
     * Awards loyalty points to a customer.
     *
     * @param customerId  the customer's UUID
     * @param points      number of points to award (must be positive)
     * @param description reason for the award (e.g. "Stayed 3 nights")
     * @param referenceId optional reference ID (e.g. reservation or bill ID)
     */
    void earnPoints(String customerId, int points, String description, String referenceId);

    /**
     * Redeems loyalty points from a customer's balance.
     *
     * @param customerId  the customer's UUID
     * @param points      number of points to redeem (must be positive)
     * @param description reason for redemption
     * @param referenceId optional reference ID
     * @throws IllegalArgumentException if insufficient balance
     */
    void redeemPoints(String customerId, int points, String description, String referenceId);

    /**
     * Gets the current loyalty points balance for a customer.
     *
     * @param customerId the customer's UUID
     * @return current points balance
     */
    int getBalance(String customerId);

    /**
     * Determines the loyalty tier for a customer based on their points.
     *
     * @param customerId the customer's UUID
     * @return the customer's current {@link LoyaltyTier}
     */
    LoyaltyTier getTier(String customerId);

    /**
     * Gets the loyalty transaction history for a customer.
     *
     * @param customerId the customer's UUID
     * @return list of transactions ordered by newest first
     */
    List<LoyaltyTransaction> getHistory(String customerId);
}
