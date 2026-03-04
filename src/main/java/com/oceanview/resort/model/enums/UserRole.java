package com.oceanview.resort.model.enums;

/**
 * Enumeration of user roles in the Ocean View Resort system.
 * Defines role-based access control levels.
 */
public enum UserRole {
    CUSTOMER("Customer"),
    STAFF("Staff"),
    MANAGER("Manager"),
    MAINTENANCE("Maintenance");

    private final String displayName;

    UserRole(String displayName) { this.displayName = displayName; }

    public String getDisplayName() { return displayName; }
}

