package com.oceanview.resort.repository;

import com.oceanview.resort.model.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for {@link LoyaltyTransaction} entity.
 * Provides queries for loyalty points history and balance calculations.
 */
@Repository
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, String> {

    /**
     * Finds all loyalty transactions for a customer, ordered by newest first.
     */
    List<LoyaltyTransaction> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    /**
     * Counts the total earned points for a customer.
     */
    @Query("SELECT COALESCE(SUM(t.points), 0) FROM LoyaltyTransaction t " +
           "WHERE t.customerId = :customerId AND t.transactionType = 'EARN'")
    int sumEarnedPoints(@Param("customerId") String customerId);

    /**
     * Counts the total redeemed points for a customer.
     */
    @Query("SELECT COALESCE(SUM(t.points), 0) FROM LoyaltyTransaction t " +
           "WHERE t.customerId = :customerId AND t.transactionType = 'REDEEM'")
    int sumRedeemedPoints(@Param("customerId") String customerId);
}
