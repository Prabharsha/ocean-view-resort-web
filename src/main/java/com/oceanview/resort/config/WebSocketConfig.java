package com.oceanview.resort.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time dashboard updates.
 *
 * <p>Enables STOMP-based WebSocket messaging so that the admin dashboard
 * can receive live updates when reservations are created, guests check in
 * or check out, or occupancy changes.</p>
 *
 * <h3>Architecture:</h3>
 * <ul>
 *   <li><b>Endpoint:</b> {@code /ws} — clients connect here using SockJS.</li>
 *   <li><b>Topic prefix:</b> {@code /topic} — server pushes messages to
 *       these destinations (e.g., {@code /topic/dashboard}).</li>
 *   <li><b>App prefix:</b> {@code /app} — clients send messages through
 *       this prefix (not used in the current dashboard scenario).</li>
 * </ul>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker with topic and application destination prefixes.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the STOMP endpoint at {@code /ws} with SockJS fallback.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
