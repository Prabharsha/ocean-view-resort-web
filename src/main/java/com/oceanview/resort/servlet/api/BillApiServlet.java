package com.oceanview.resort.servlet.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.model.enums.PaymentMethod;
import com.oceanview.resort.service.BillService;
import com.oceanview.resort.servlet.BaseApiServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * REST servlet for bill management. Replaces Spring's BillController.
 */
public class BillApiServlet extends BaseApiServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BillService service = getService(req, "billService");
        String pathParam = getPathParam(req);

        try {
            if (pathParam == null) {
                sendJson(resp, service.findAll());
            } else if (pathParam.equals("unpaid")) {
                sendJson(resp, service.findUnpaid());
            } else if (pathParam.startsWith("reservation/")) {
                String resId = pathParam.substring("reservation/".length());
                sendJson(resp, service.findByReservationId(resId));
            } else {
                Bill bill = service.findById(pathParam);
                sendJson(resp, bill);
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, 404, e.getMessage());
        } catch (Exception e) {
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BillService service = getService(req, "billService");
        String pathParam = getPathParam(req);

        try {
            if (pathParam != null && pathParam.startsWith("generate/")) {
                // POST /api/bills/generate/{reservationId}
                String resId = pathParam.substring("generate/".length());
                Bill bill = service.generateOrGetBill(resId);
                sendJson(resp, HttpServletResponse.SC_CREATED, bill);
            } else if (pathParam != null && pathParam.endsWith("/discount")) {
                // POST /api/bills/{id}/discount
                String billId = pathParam.replace("/discount", "");
                JsonObject body = JsonParser.parseString(readBody(req)).getAsJsonObject();
                BigDecimal discountPercent = body.get("discountPercent").getAsBigDecimal();
                sendJson(resp, service.applyDiscount(billId, discountPercent));
            } else {
                sendError(resp, 400, "Unknown bill operation");
            }
        } catch (Exception e) {
            sendError(resp, 400, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BillService service = getService(req, "billService");
        String pathParam = getPathParam(req);

        try {
            if (pathParam != null && pathParam.endsWith("/pay")) {
                // PUT /api/bills/{id}/pay
                String billId = pathParam.replace("/pay", "");
                JsonObject body = JsonParser.parseString(readBody(req)).getAsJsonObject();

                BigDecimal amountPaid = body.get("amountPaid").getAsBigDecimal();
                PaymentMethod method = PaymentMethod.valueOf(body.get("paymentMethod").getAsString());
                String txRef = body.has("transactionReference") ? body.get("transactionReference").getAsString() : null;
                String notes = body.has("notes") ? body.get("notes").getAsString() : null;
                String processedBy = (String) req.getAttribute("userId");

                service.markAsPaid(billId, amountPaid, method, txRef, notes, processedBy);
                sendJson(resp, java.util.Map.of("message", "Payment recorded successfully"));
            } else {
                sendError(resp, 400, "Unknown bill operation");
            }
        } catch (Exception e) {
            sendError(resp, 400, e.getMessage());
        }
    }
}

