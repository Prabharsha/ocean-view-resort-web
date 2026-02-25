package com.oceanview.resort.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class for enabling async method execution and scheduled tasks.
 *
 * <p>Enables {@code @Async} annotations on methods (used by event listeners
 * such as {@code EmailNotificationListener} to send emails without blocking
 * the main transaction thread) and {@code @Scheduled} annotations for
 * periodic tasks (such as check-in reminder emails).</p>
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    // Spring Boot auto-configures a default task executor.
    // Custom thread pool can be defined here if needed.
}
