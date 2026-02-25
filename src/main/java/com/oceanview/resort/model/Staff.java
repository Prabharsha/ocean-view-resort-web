package com.oceanview.resort.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Staff entity extending {@link User}.
 *
 * <p>Represents hotel employees with department assignment and a unique
 * employee identifier. Maps to the {@code staff} table via JPA
 * {@link InheritanceType#JOINED} strategy.</p>
 *
 * <h3>Fields</h3>
 * <ul>
 *   <li>{@code department} – the department to which the staff member belongs (e.g. Front Desk, Housekeeping).</li>
 *   <li>{@code employeeId} – a unique employee reference code (e.g. EMP-001).</li>
 * </ul>
 *
 * @see User
 * @see Manager
 */
@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Staff extends User {

    /** Department the staff member is assigned to. */
    @Column(length = 100)
    @Size(max = 100, message = "Department name must not exceed 100 characters")
    private String department;

    /** Unique employee identification code. */
    @Column(name = "employee_id", unique = true, length = 20)
    @NotBlank(message = "Employee ID is required")
    @Size(max = 20, message = "Employee ID must not exceed 20 characters")
    private String employeeId;
}
