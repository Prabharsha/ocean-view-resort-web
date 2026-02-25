package com.oceanview.resort.service.impl;

import com.oceanview.resort.model.Customer;
import com.oceanview.resort.model.LoyaltyTransaction;
import com.oceanview.resort.model.enums.LoyaltyTier;
import com.oceanview.resort.repository.CustomerRepository;
import com.oceanview.resort.repository.LoyaltyTransactionRepository;
import com.oceanview.resort.service.interfaces.LoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link LoyaltyService}.
 *
 * <p>Manages the Ocean View Resort loyalty programme. Customers earn
 * 1 point per £1 spent. Points can be redeemed for discounts on future stays
 * at a rate of 1 point = £0.10.</p>
 *
 * <p>Loyalty tiers are calculated dynamically from the customer's current
 * points balance using {@link LoyaltyTier#fromPoints(int)}.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LoyaltyServiceImpl implements LoyaltyService {

    private final CustomerRepository customerRepository;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void earnPoints(String customerId, int points, String description, String referenceId) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to earn must be positive");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        customer.addLoyaltyPoints(points);
        customerRepository.save(customer);

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .customerId(customerId)
                .points(points)
                .transactionType("EARN")
                .description(description)
                .referenceId(referenceId)
                .build();
        loyaltyTransactionRepository.save(transaction);

        log.info("Customer {} earned {} points. New balance: {}. Tier: {}",
                customerId, points, customer.getLoyaltyPoints(),
                LoyaltyTier.fromPoints(customer.getLoyaltyPoints()));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void redeemPoints(String customerId, int points, String description, String referenceId) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to redeem must be positive");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        customer.redeemLoyaltyPoints(points);
        customerRepository.save(customer);

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .customerId(customerId)
                .points(-points)
                .transactionType("REDEEM")
                .description(description)
                .referenceId(referenceId)
                .build();
        loyaltyTransactionRepository.save(transaction);

        log.info("Customer {} redeemed {} points. New balance: {}",
                customerId, points, customer.getLoyaltyPoints());
    }

    /** {@inheritDoc} */
    @Override
    public int getBalance(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        return customer.getLoyaltyPoints();
    }

    /** {@inheritDoc} */
    @Override
    public LoyaltyTier getTier(String customerId) {
        int balance = getBalance(customerId);
        return LoyaltyTier.fromPoints(balance);
    }

    /** {@inheritDoc} */
    @Override
    public List<LoyaltyTransaction> getHistory(String customerId) {
        return loyaltyTransactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }
}
