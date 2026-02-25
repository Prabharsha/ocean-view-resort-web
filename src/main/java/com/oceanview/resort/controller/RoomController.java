package com.oceanview.resort.controller;

import com.oceanview.resort.dto.RoomSearchCriteria;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.RoomType;
import com.oceanview.resort.service.interfaces.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for room management operations.
 *
 * <p>Handles listing, searching, creating, updating, and deleting rooms.
 * Public GET endpoints allow guests to browse rooms; write operations
 * are restricted to MANAGER / MAINTENANCE roles (enforced in Task 7).</p>
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rooms", description = "Room management endpoints")
public class RoomController {

    private final RoomService roomService;

    /**
     * Returns all rooms.
     */
    @GetMapping
    @Operation(summary = "List All Rooms", description = "Get all rooms")
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.findAllRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * Finds rooms available for the given date range and optional type.
     */
    @GetMapping("/available")
    @Operation(summary = "Available Rooms",
            description = "Find rooms available for a date range and optional room type")
    public ResponseEntity<List<Room>> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) RoomType type) {
        List<Room> rooms = roomService.findAvailableRooms(checkIn, checkOut, type);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Searches rooms with advanced multi-criteria filters.
     */
    @GetMapping("/search")
    @Operation(summary = "Search Rooms",
            description = "Advanced room search with filters for type, price, capacity, floor")
    public ResponseEntity<List<Room>> searchRooms(
            @RequestParam(required = false) RoomType type,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer floor,
            @RequestParam(defaultValue = "true") boolean availableOnly,
            @RequestParam(defaultValue = "ratePerNight") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        RoomSearchCriteria criteria = RoomSearchCriteria.builder()
                .roomType(type)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minCapacity(minCapacity)
                .floorNumber(floor)
                .availableOnly(availableOnly)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        log.info("Searching rooms with criteria: {}", criteria);
        List<Room> rooms = roomService.searchRooms(criteria);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Returns a room by its UUID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Room", description = "Get a room by its ID")
    public ResponseEntity<Room> getRoomById(@PathVariable String id) {
        Room room = roomService.findById(id);
        return ResponseEntity.ok(room);
    }

    /**
     * Creates a new room (MANAGER only).
     */
    @PostMapping
    @Operation(summary = "Add Room", description = "Create a new room (manager only)")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        log.info("Creating room: {}", room.getRoomNumber());
        Room created = roomService.createRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing room (MANAGER / MAINTENANCE).
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Room",
            description = "Update room details (manager/maintenance only)")
    public ResponseEntity<Room> updateRoom(
            @PathVariable String id,
            @Valid @RequestBody Room room) {
        log.info("Updating room: {}", id);
        Room updated = roomService.updateRoom(id, room);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a room (MANAGER only).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Room", description = "Delete a room (manager only)")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        log.info("Deleting room: {}", id);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
