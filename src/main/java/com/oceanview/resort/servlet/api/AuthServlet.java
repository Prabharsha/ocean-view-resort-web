package com.oceanview.resort.servlet.api;

import com.google.gson.JsonObject;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.model.User;
import com.oceanview.resort.service.UserService;
import com.oceanview.resort.servlet.BaseApiServlet;
import com.oceanview.resort.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST servlet for authentication operations.
 * Handles: POST /api/auth/login, POST /api/auth/register, POST /api/auth/logout,
 *          GET /api/auth/profile, PUT /api/auth/change-password
 */
public class AuthServlet extends BaseApiServlet {

    private static final Logger log = LoggerFactory.getLogger(AuthServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        try {
            if (pathInfo.endsWith("/login")) {
                handleLogin(req, resp);
            } else if (pathInfo.endsWith("/register")) {
                handleRegister(req, resp);
            } else if (pathInfo.endsWith("/logout")) {
                sendJson(resp, Map.of("message", "Logout successful"));
            } else if (pathInfo.endsWith("/refresh")) {
                // Simplified: just re-validate and re-issue
                sendError(resp, 400, "Refresh not implemented in basic mode");
            } else {
                sendError(resp, 404, "Unknown auth endpoint");
            }
        } catch (Exception e) {
            log.error("Auth error", e);
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.endsWith("/profile")) {
            handleGetProfile(req, resp);
        } else {
            sendError(resp, 404, "Not found");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.endsWith("/change-password")) {
            handleChangePassword(req, resp);
        } else {
            sendError(resp, 404, "Not found");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject body = com.google.gson.JsonParser.parseString(readBody(req)).getAsJsonObject();
        String username = body.get("username").getAsString();
        String password = body.get("password").getAsString();

        UserService userService = getService(req, "userService");
        User user = userService.authenticate(username, password);

        if (user == null) {
            sendError(resp, 401, "Invalid username or password");
            return;
        }

        String jwtSecret = (String) req.getServletContext().getAttribute("jwtSecret");
        long jwtExpiration = (Long) req.getServletContext().getAttribute("jwtExpiration");

        String accessToken = JwtUtil.generateToken(username, user.getId(), user.getRole().name(),
                jwtExpiration, jwtSecret);
        String refreshToken = JwtUtil.generateToken(username, user.getId(), user.getRole().name(),
                jwtExpiration * 7, jwtSecret);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("tokenType", "Bearer");

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("email", user.getEmail());
        userMap.put("phone", user.getPhone());
        userMap.put("role", user.getRole().name());
        response.put("user", userMap);

        log.info("User logged in: {}", username);
        sendJson(resp, response);
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject body = com.google.gson.JsonParser.parseString(readBody(req)).getAsJsonObject();

        UserService userService = getService(req, "userService");
        try {
            Customer customer = userService.registerCustomer(
                    body.get("username").getAsString(),
                    body.get("password").getAsString(),
                    body.get("firstName").getAsString(),
                    body.get("lastName").getAsString(),
                    body.get("email").getAsString(),
                    body.has("phone") ? body.get("phone").getAsString() : null);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", customer.getId());
            userMap.put("username", customer.getUsername());
            userMap.put("firstName", customer.getFirstName());
            userMap.put("lastName", customer.getLastName());
            userMap.put("email", customer.getEmail());
            userMap.put("role", "CUSTOMER");

            sendJson(resp, HttpServletResponse.SC_CREATED, userMap);
        } catch (IllegalStateException e) {
            sendError(resp, 409, e.getMessage());
        }
    }

    private void handleGetProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = (String) req.getAttribute("username");
        if (username == null) { sendError(resp, 401, "Not authenticated"); return; }

        UserService userService = getService(req, "userService");
        User user = userService.findByUsername(username);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("email", user.getEmail());
        userMap.put("phone", user.getPhone());
        userMap.put("role", user.getRole().name());
        sendJson(resp, userMap);
    }

    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userId = (String) req.getAttribute("userId");
        if (userId == null) { sendError(resp, 401, "Not authenticated"); return; }

        JsonObject body = com.google.gson.JsonParser.parseString(readBody(req)).getAsJsonObject();
        UserService userService = getService(req, "userService");

        try {
            userService.changePassword(userId,
                    body.get("oldPassword").getAsString(),
                    body.get("newPassword").getAsString());
            sendJson(resp, Map.of("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            sendError(resp, 400, e.getMessage());
        }
    }
}

