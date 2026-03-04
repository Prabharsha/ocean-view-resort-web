package com.oceanview.resort.servlet;

import com.oceanview.resort.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * Base class for all API servlets. Provides helper methods for JSON I/O.
 */
public abstract class BaseApiServlet extends HttpServlet {

    /** Sends a JSON response with 200 OK. */
    protected void sendJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(JsonUtil.toJson(data));
    }

    /** Sends a JSON response with a specific status code. */
    protected void sendJson(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        sendJson(resp, data);
    }

    /** Sends a JSON error response. */
    protected void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.getWriter().write(JsonUtil.toJson(Map.of(
                "status", status,
                "error", getStatusText(status),
                "message", message)));
    }

    /** Reads the request body as a string. */
    protected String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    /** Parses JSON request body to an object. */
    protected <T> T readJsonBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        return JsonUtil.fromJson(readBody(req), clazz);
    }

    /** Extracts the path segment after /api/xxx/  (e.g. the ID or sub-path). */
    protected String getPathParam(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) return null;
        // Remove leading slash
        return pathInfo.substring(1);
    }

    /** Gets a service from ServletContext. */
    @SuppressWarnings("unchecked")
    protected <T> T getService(HttpServletRequest req, String name) {
        return (T) req.getServletContext().getAttribute(name);
    }

    private String getStatusText(int status) {
        switch (status) {
            case 400: return "Bad Request";
            case 401: return "Unauthorized";
            case 403: return "Forbidden";
            case 404: return "Not Found";
            case 409: return "Conflict";
            default:  return "Internal Server Error";
        }
    }
}

