package com.oceanview.resort.config;

import com.oceanview.resort.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for the Ocean View Resort application.
 *
 * <p>Configures stateless JWT-based authentication, role-based access control,
 * and registers the {@link JwtAuthenticationFilter} in the filter chain.</p>
 *
 * <h3>Role hierarchy</h3>
 * <ul>
 *   <li><b>CUSTOMER</b> – create / view own reservations, pay bills</li>
 *   <li><b>STAFF</b> – all customer permissions + manage any reservation</li>
 *   <li><b>MANAGER</b> – all staff permissions + reports, rooms, users</li>
 *   <li><b>MAINTENANCE</b> – room maintenance endpoints</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the security filter chain with role-based access rules
     * and JWT authentication.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ─── Public endpoints ───
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/rooms", "/api/rooms/available",
                        "/api/rooms/search").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/", "/login", "/register", "/error",
                        "/dashboard", "/reservations/**", "/rooms/**",
                        "/bills/**", "/reports", "/reports/**",
                        "/profile", "/help").permitAll()

                // ─── Reports: MANAGER only ───
                .requestMatchers("/api/reports/**").hasRole("MANAGER")

                // ─── Room management: MANAGER or MAINTENANCE (write ops) ───
                .requestMatchers(HttpMethod.POST, "/api/rooms").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/rooms/**")
                    .hasAnyRole("MANAGER", "MAINTENANCE")
                .requestMatchers(HttpMethod.DELETE, "/api/rooms/**").hasRole("MANAGER")

                // ─── User management: MANAGER only ───
                .requestMatchers("/api/users/**").hasRole("MANAGER")

                // ─── Bill discount: MANAGER and STAFF ───
                .requestMatchers(HttpMethod.POST, "/api/bills/*/discount")
                    .hasAnyRole("MANAGER", "STAFF")

                // ─── Reservation list (all): STAFF and MANAGER ───
                .requestMatchers(HttpMethod.GET, "/api/reservations")
                    .hasAnyRole("STAFF", "MANAGER")

                // ─── Check-in / Check-out: STAFF and MANAGER ───
                .requestMatchers(HttpMethod.PUT, "/api/reservations/*/checkin")
                    .hasAnyRole("STAFF", "MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/reservations/*/checkout")
                    .hasAnyRole("STAFF", "MANAGER")

                // ─── All remaining endpoints require authentication ───
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Exposes the AuthenticationManager bean for use in controllers.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Provides BCrypt password encoder with strength 12.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
