package com.oceanview.resort.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Manager entity extending {@link Staff}.
 *
 * <p>Represents senior hotel staff with additional administrative privileges
 * such as viewing reports, managing rooms, and managing users. Maps to the
 * {@code managers} table via JPA {@link InheritanceType#JOINED} strategy.</p>
 *
 * <p>A Manager inherits all {@link User} and {@link Staff} fields. Additional
 * manager-specific attributes (e.g. access level, approval authority) can be
 * added as the system evolves.</p>
 *
 * @see User
 * @see Staff
 */
@Entity
@Table(name = "managers")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Manager extends Staff {

    // Manager inherits all Staff and User fields.
    // Additional manager-specific fields can be added here.
}
