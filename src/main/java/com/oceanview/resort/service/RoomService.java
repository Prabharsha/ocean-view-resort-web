package com.oceanview.resort.service;

import com.oceanview.resort.dao.RoomDAO;
import com.oceanview.resort.dao.ReservationDAO;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.RoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service Layer for room management.
 */
public class RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);
    private final RoomDAO roomDAO;
    private final ReservationDAO reservationDAO;

    public RoomService(RoomDAO roomDAO, ReservationDAO reservationDAO) {
        this.roomDAO = roomDAO;
        this.reservationDAO = reservationDAO;
    }

    public List<Room> findAll() { return roomDAO.findAll(); }

    public Room findById(String id) {
        return roomDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("Room not found: " + id));
    }

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        return roomDAO.findAvailableRooms(checkIn, checkOut, type);
    }

    public List<Room> searchRooms(RoomType type, BigDecimal minPrice, BigDecimal maxPrice,
                                  Integer minCapacity, Integer floor, boolean availableOnly) {
        return roomDAO.searchRooms(type, minPrice, maxPrice, minCapacity, floor, availableOnly);
    }

    public Room createRoom(Room room) {
        if (roomDAO.existsByRoomNumber(room.getRoomNumber())) {
            throw new IllegalStateException("Room " + room.getRoomNumber() + " already exists");
        }
        String id = roomDAO.save(room);
        room.setId(id);
        return room;
    }

    public Room updateRoom(String id, Room room) {
        Room existing = findById(id);
        existing.setRoomNumber(room.getRoomNumber());
        existing.setRoomType(room.getRoomType());
        existing.setFloorNumber(room.getFloorNumber());
        existing.setCapacity(room.getCapacity());
        existing.setRatePerNight(room.getRatePerNight());
        existing.setAvailable(room.isAvailable());
        existing.setDescription(room.getDescription());
        existing.setAmenities(room.getAmenities());
        roomDAO.update(existing);
        return existing;
    }

    public void deleteRoom(String id) {
        roomDAO.delete(id);
    }

    public long count() { return roomDAO.count(); }
    public long countAvailable() { return roomDAO.countAvailable(); }
}

