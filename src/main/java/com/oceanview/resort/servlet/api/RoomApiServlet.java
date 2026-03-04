package com.oceanview.resort.servlet.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.RoomType;
import com.oceanview.resort.service.RoomService;
import com.oceanview.resort.servlet.BaseApiServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST servlet for room management. Replaces Spring's RoomController.
 */
public class RoomApiServlet extends BaseApiServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomService service = getService(req, "roomService");
        String pathParam = getPathParam(req);

        try {
            if (pathParam == null) {
                sendJson(resp, service.findAll());
            } else if (pathParam.equals("available")) {
                String checkIn = req.getParameter("checkIn");
                String checkOut = req.getParameter("checkOut");
                String type = req.getParameter("type");
                RoomType roomType = (type != null && !type.isEmpty()) ? RoomType.valueOf(type) : null;
                List<Room> rooms = service.findAvailableRooms(
                        LocalDate.parse(checkIn), LocalDate.parse(checkOut), roomType);
                sendJson(resp, rooms);
            } else if (pathParam.equals("search")) {
                String type = req.getParameter("type");
                String minPrice = req.getParameter("minPrice");
                String maxPrice = req.getParameter("maxPrice");
                String minCap = req.getParameter("minCapacity");
                String floor = req.getParameter("floor");
                boolean avail = !"false".equals(req.getParameter("availableOnly"));

                sendJson(resp, service.searchRooms(
                        type != null ? RoomType.valueOf(type) : null,
                        minPrice != null ? new BigDecimal(minPrice) : null,
                        maxPrice != null ? new BigDecimal(maxPrice) : null,
                        minCap != null ? Integer.parseInt(minCap) : null,
                        floor != null ? Integer.parseInt(floor) : null,
                        avail));
            } else {
                sendJson(resp, service.findById(pathParam));
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, 404, e.getMessage());
        } catch (Exception e) {
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomService service = getService(req, "roomService");
        try {
            JsonObject body = JsonParser.parseString(readBody(req)).getAsJsonObject();
            Room room = new Room();
            room.setRoomNumber(body.get("roomNumber").getAsString());
            room.setRoomType(RoomType.valueOf(body.get("roomType").getAsString()));
            room.setFloorNumber(body.get("floorNumber").getAsInt());
            room.setCapacity(body.get("capacity").getAsInt());
            room.setRatePerNight(body.get("ratePerNight").getAsBigDecimal());
            if (body.has("description")) room.setDescription(body.get("description").getAsString());
            if (body.has("amenities")) room.setAmenities(body.get("amenities").getAsString());
            room.setAvailable(body.has("available") ? body.get("available").getAsBoolean() : true);

            Room created = service.createRoom(room);
            sendJson(resp, HttpServletResponse.SC_CREATED, created);
        } catch (Exception e) {
            sendError(resp, 400, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomService service = getService(req, "roomService");
        String id = getPathParam(req);
        if (id == null) { sendError(resp, 400, "Missing room id"); return; }

        try {
            JsonObject body = JsonParser.parseString(readBody(req)).getAsJsonObject();
            Room room = new Room();
            if (body.has("roomNumber")) room.setRoomNumber(body.get("roomNumber").getAsString());
            if (body.has("roomType")) room.setRoomType(RoomType.valueOf(body.get("roomType").getAsString()));
            if (body.has("floorNumber")) room.setFloorNumber(body.get("floorNumber").getAsInt());
            if (body.has("capacity")) room.setCapacity(body.get("capacity").getAsInt());
            if (body.has("ratePerNight")) room.setRatePerNight(body.get("ratePerNight").getAsBigDecimal());
            if (body.has("description")) room.setDescription(body.get("description").getAsString());
            if (body.has("amenities")) room.setAmenities(body.get("amenities").getAsString());
            if (body.has("available")) room.setAvailable(body.get("available").getAsBoolean());

            sendJson(resp, service.updateRoom(id, room));
        } catch (Exception e) {
            sendError(resp, 400, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomService service = getService(req, "roomService");
        String id = getPathParam(req);
        if (id == null) { sendError(resp, 400, "Missing room id"); return; }
        try {
            service.deleteRoom(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            sendError(resp, 400, e.getMessage());
        }
    }
}

