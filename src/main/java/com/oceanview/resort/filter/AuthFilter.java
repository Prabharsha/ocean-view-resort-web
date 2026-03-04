package com.oceanview.resort.filter;

import com.oceanview.resort.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Authentication filter for API endpoints.
 * Checks for JWT Bearer token in Authorization header.
 * Public endpoints (login, register, rooms GET) skip authentication.
 */
public class AuthFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        // Handle CORS preflight
        if ("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
            httpResp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = httpReq.getPathInfo() != null ? httpReq.getPathInfo() : "";
        String servletPath = httpReq.getServletPath();
        String fullPath = servletPath + path;

        // Public endpoints – skip auth
        if (fullPath.startsWith("/api/auth/login") ||
            fullPath.startsWith("/api/auth/register") ||
            (fullPath.startsWith("/api/rooms") && "GET".equalsIgnoreCase(httpReq.getMethod()))) {
            chain.doFilter(request, response);
            return;
        }

        // Extract JWT from Authorization header
        String authHeader = httpReq.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResp.setContentType("application/json");
            httpResp.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
            return;
        }

        String token = authHeader.substring(7);
        String jwtSecret = (String) httpReq.getServletContext().getAttribute("jwtSecret");

        try {
            Map<String, Object> claims = JwtUtil.validateToken(token, jwtSecret);
            // Store user info in request attributes for use by servlets
            httpReq.setAttribute("userId", claims.get("userId"));
            httpReq.setAttribute("username", claims.get("sub"));
            httpReq.setAttribute("userRole", claims.get("role"));
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            httpResp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResp.setContentType("application/json");
            httpResp.getWriter().write("{\"error\":\"Invalid or expired token\"}");
        }
    }
}

