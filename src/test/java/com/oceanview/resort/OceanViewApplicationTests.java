package com.oceanview.resort;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test to verify the Spring application context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("test")
class OceanViewApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context starts without errors.
    }
}
