package com.oceanview.resort.servlet.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.service.ReservationService;
import com.oceanview.resort.servlet.BaseApiServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * REST servlet for reservation management. Replaces Spring's ReservationController.
 * Handles: /api/reservations/*
 */
public class ReservationApiServlet extends BaseApiServlet {

    private static final Logger log = LoggerFactory.getLogger(ReservationApiServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReservationService service = getService(req, "reservationService");
        String pathParam = getPathParam(req);

        try {
            if (pathParam == null) {
                // GET /api/reservations — list all
                List<Reservation> list = service.findAll();
                sendJson(resp, list);
            } else if (pathParam.startsWith("customer/")) {
                // GET /api/reservations/customer/{customerId}
                String customerId = pathParam.substring("customer/".length());
                sendJson(resp, service.findByCustomerId(customerId));
            } else if (pathParam.startsWith("number/")) {
                // GET /api/reservations/number/{resNumber}
                String resNum = pathParam.substring("number/".length());
                sendJson(resp, service.findByReservationNumber(resNum));
            } else {
                // GET /api/reservations/{id} — by UUID or reservation number
                if (pathParam.startsWith("OVR-") || pathParam.startsWith("RES-")) {
                    sendJson(resp, service.findByReservationNumber(pathParam));
                } else {
                    sendJson(resp, service.findById(pathParam));
                }
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, 404, e.getMessage());
        } catch (Exception e) {
            log.error("Error in reservation GET", e);
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReservationService service = getService(req, "reservationService");

        try {
            JsonObject body = JsonParser.parseString(readBody(req)).getAsJsonObject();

            Reservation reservation = new Reservation();
            reservation.setCustomerId(body.get("customerId").getAsString());
            reservation.setRoomId(body.get("roomId").getAsString());
            if (body.has("checkInDate"))
                reservation.setCheckInDate(LocalDate.parse(body.get("checkInDate").getAsString()));
            if (body.has("checkOutDate"))
                reservation.setCheckOutDate(LocalDate.parse(body.get("checkOutDate").getAsString()));
            if (body.has("numGuests"))
                reservation.setNumGuests(body.get("numGuests").getAsInt());
            if (body.has("specialRequests") && !body.get("specialRequests").isJsonNull())
                reservation.setSpecialRequests(body.get("specialRequests").getAsString());
            if (body.has("guestName") && !body.get("guestName").isJsonNull())
                reservation.setGuestName(body.get("guestName").getAsString());
            if (body.has("guestContact") && !body.get("guestContact").isJsonNull())
                reservation.setGuestContact(body.get("guestContact").getAsString());
            if (body.has("staffId") && !body.get("staffId").isJsonNull())
                reservation.setStaffId(body.get("staffId").getAsString());

            // Auto-assign staff from JWT token if present
            String authUserId = (String) req.getAttribute("userId");
            String authRole = (String) req.getAttribute("userRole");
            if (reservation.getStaffId() == null &&
                authUserId != null &&
                ("STAFF".equals(authRole) || "MANAGER".equals(authRole))) {
                reservation.setStaffId(authUserId);
            }

            Reservation created = service.createReservation(reservation);
            sendJson(resp, HttpServletResponse.SC_CREATED, created);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            log.error("Error creating reservation", e);
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReservationService service = getService(req, "reservationService");
        String pathParam = getPathParam(req);
        if (pathParam == null) { sendError(resp, 400, "Missing reservation identifier"); return; }

        try {
            if (pathParam.endsWith("/confirm")) {
                String resNum = pathParam.replace("/confirm", "");
                sendJson(resp, service.confirmReservation(resNum));
            } else if (pathParam.endsWith("/checkin")) {
                String resNum = pathParam.replace("/checkin", "");
                sendJson(resp, service.checkIn(resNum));
            } else if (pathParam.endsWith("/checkout")) {
                String resNum = pathParam.replace("/checkout", "");
                sendJson(resp, service.checkOut(resNum));
            } else if (pathParam.endsWith("/cancel")) {
                String id = pathParam.replace("/cancel", "");
                service.cancelReservation(id);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                // PUT /api/reservations/{id}?status=...
                String status = req.getParameter("status");
                if (status != null) {
                    sendJson(resp, service.updateStatus(pathParam, ReservationStatus.valueOf(status)));
                } else {
                    sendError(resp, 400, "Missing status parameter");
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            log.error("Error in reservation PUT", e);
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReservationService service = getService(req, "reservationService");
        String id = getPathParam(req);
        if (id == null) { sendError(resp, 400, "Missing reservation id"); return; }

        try {
            service.cancelReservation(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            sendError(resp, 400, e.getMessage());
        }
    }
}

