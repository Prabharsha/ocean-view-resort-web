package com.oceanview.resort.servlet.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.service.CustomerService;
import com.oceanview.resort.servlet.BaseApiServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST servlet for customer management.
 */
public class CustomerApiServlet extends BaseApiServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CustomerService service = getService(req, "customerService");
        String pathParam = getPathParam(req);

        try {
            if (pathParam == null) {
                String q = req.getParameter("q");
                List<Customer> customers = (q != null && !q.isBlank()) ?
                        service.search(q) : service.findAll();
                // Convert to maps (hide password)
                sendJson(resp, customers.stream().map(this::toMap).collect(Collectors.toList()));
            } else {
                sendJson(resp, toMap(service.findById(pathParam)));
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, 404, e.getMessage());
        } catch (Exception e) {
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CustomerService service = getService(req, "customerService");
        try {
            JsonObject body = JsonParser.parseString(readBody(req)).getAsJsonObject();
            Customer customer = service.createCustomer(
                    body.get("firstName").getAsString(),
                    body.get("lastName").getAsString(),
                    body.get("email").getAsString(),
                    body.has("phone") ? body.get("phone").getAsString() : null,
                    body.has("address") ? body.get("address").getAsString() : null);
            sendJson(resp, HttpServletResponse.SC_CREATED, toMap(customer));
        } catch (IllegalStateException e) {
            sendError(resp, 409, e.getMessage());
        } catch (Exception e) {
            sendError(resp, 400, e.getMessage());
        }
    }

    private Map<String, Object> toMap(Customer c) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", c.getId());
        m.put("username", c.getUsername());
        m.put("firstName", c.getFirstName());
        m.put("lastName", c.getLastName());
        m.put("email", c.getEmail());
        m.put("phone", c.getPhone());
        m.put("address", c.getAddress());
        m.put("loyaltyPoints", c.getLoyaltyPoints());
        m.put("role", "CUSTOMER");
        return m;
    }
}

