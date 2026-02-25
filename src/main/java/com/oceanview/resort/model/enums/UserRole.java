package com.oceanview.resort.model.enums;

/**
 * Enumeration of user roles in the Ocean View Resort system.
 * Defines role-based access control levels used by Spring Security.
 *
 * <ul>
 *   <li>{@link #CUSTOMER}    – Hotel guests who can make and manage their own reservations.</li>
 *   <li>{@link #STAFF}       – Hotel employees who manage all reservations and check-ins.</li>
 *   <li>{@link #MANAGER}     – Senior staff with full administrative privileges including reports.</li>
 *   <li>{@link #MAINTENANCE} – Technical personnel responsible for room and system maintenance.</li>
 * </ul>
 */
public enum UserRole {

    /** Hotel guest – can create/view own reservations and pay bills. */
    CUSTOMER("Customer"),

    /** Hotel staff – can manage any reservation, perform check-ins/check-outs. */
    STAFF("Staff"),

    /** Hotel manager – full administrative access including reports and user management. */
    MANAGER("Manager"),

    /** Maintenance personnel – room and system maintenance responsibilities. */
    MAINTENANCE("Maintenance");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this role.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
