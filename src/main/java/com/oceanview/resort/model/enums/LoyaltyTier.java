package com.oceanview.resort.model.enums;

/**
 * Enumeration of loyalty program tiers at Ocean View Resort.
 *
 * <p>Each tier is determined by the customer's accumulated loyalty points.
 * Higher tiers unlock better rewards and benefits.</p>
 *
 * <ul>
 *   <li>{@link #BRONZE}   – 0 to 499 points (entry level).</li>
 *   <li>{@link #SILVER}   – 500 to 1,999 points.</li>
 *   <li>{@link #GOLD}     – 2,000 to 4,999 points.</li>
 *   <li>{@link #PLATINUM} – 5,000+ points (highest tier).</li>
 * </ul>
 */
public enum LoyaltyTier {

    BRONZE("Bronze", 0, "5% room discount"),
    SILVER("Silver", 500, "10% room discount + free breakfast"),
    GOLD("Gold", 2000, "15% room discount + free breakfast + late checkout"),
    PLATINUM("Platinum", 5000, "20% room discount + all Gold benefits + room upgrade");

    private final String displayName;
    private final int minimumPoints;
    private final String benefits;

    LoyaltyTier(String displayName, int minimumPoints, String benefits) {
        this.displayName = displayName;
        this.minimumPoints = minimumPoints;
        this.benefits = benefits;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinimumPoints() {
        return minimumPoints;
    }

    public String getBenefits() {
        return benefits;
    }

    /**
     * Determines the loyalty tier based on the customer's total points.
     *
     * @param points the customer's total loyalty points
     * @return the corresponding {@link LoyaltyTier}
     */
    public static LoyaltyTier fromPoints(int points) {
        if (points >= PLATINUM.minimumPoints) return PLATINUM;
        if (points >= GOLD.minimumPoints) return GOLD;
        if (points >= SILVER.minimumPoints) return SILVER;
        return BRONZE;
    }
}
