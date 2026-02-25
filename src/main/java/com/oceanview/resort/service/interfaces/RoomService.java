package com.oceanview.resort.service.interfaces;

import com.oceanview.resort.dto.RoomSearchCriteria;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.RoomType;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for room management operations.
 * Provides methods for querying, creating, updating, and managing rooms.
 */
public interface RoomService {

    /**
     * Returns all rooms.
     */
    List<Room> findAllRooms();

    /**
     * Finds a room by its UUID.
     */
    Room findById(String id);

    /**
     * Finds a room by its room number.
     */
    Room findByRoomNumber(String roomNumber);

    /**
     * Finds rooms available for the given date range and optional type.
     */
    List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type);

    /**
     * Creates a new room.
     */
    Room createRoom(Room room);

    /**
     * Updates an existing room.
     */
    Room updateRoom(String id, Room room);

    /**
     * Deletes a room by its UUID.
     */
    void deleteRoom(String id);

    /**
     * Searches rooms with advanced multi-criteria filters.
     *
     * @param criteria the search criteria DTO
     * @return list of matching rooms
     */
    List<Room> searchRooms(RoomSearchCriteria criteria);
}
