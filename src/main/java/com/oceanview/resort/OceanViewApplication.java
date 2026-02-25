package com.oceanview.resort;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main entry point for the Ocean View Resort Room Reservation System.
 * <p>
 * This Spring Boot application provides a comprehensive hotel room reservation
 * management system with role-based access control, billing, and reporting.
 * </p>
 *
 * @author Ocean View Resort Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableAsync
public class OceanViewApplication {

    public static void main(String[] args) {
        SpringApplication.run(OceanViewApplication.class, args);
    }
}
