package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.RoomSearchCriteria;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.RoomType;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.service.interfaces.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of {@link RoomService}.
 *
 * <p>Handles room CRUD operations, availability checks, and search filtering.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    /** {@inheritDoc} */
    @Override
    public List<Room> findAllRooms() {
        log.debug("Finding all rooms");
        return roomRepository.findAllByOrderByCreatedAtDesc();
    }

    /** {@inheritDoc} */
    @Override
    public Room findById(String id) {
        log.debug("Finding room by id: {}", id);
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + id));
    }

    /** {@inheritDoc} */
    @Override
    public Room findByRoomNumber(String roomNumber) {
        log.debug("Finding room by number: {}", roomNumber);
        return roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Room not found with number: " + roomNumber));
    }

    /** {@inheritDoc} */
    @Override
    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        log.debug("Finding available rooms from {} to {}, type: {}", checkIn, checkOut, type);
        return roomRepository.findAvailableRooms(checkIn, checkOut, type);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Room createRoom(Room room) {
        log.info("Creating room: {}", room.getRoomNumber());

        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new IllegalStateException(
                    "Room with number " + room.getRoomNumber() + " already exists");
        }

        Room saved = roomRepository.save(room);
        log.info("Room created: {} ({})", saved.getRoomNumber(), saved.getId());
        return saved;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Room updateRoom(String id, Room room) {
        log.info("Updating room: {}", id);

        Room existing = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + id));

        existing.setRoomNumber(room.getRoomNumber());
        existing.setRoomType(room.getRoomType());
        existing.setFloorNumber(room.getFloorNumber());
        existing.setCapacity(room.getCapacity());
        existing.setRatePerNight(room.getRatePerNight());
        existing.setAvailable(room.isAvailable());
        existing.setDescription(room.getDescription());
        existing.setAmenities(room.getAmenities());

        Room saved = roomRepository.save(existing);
        log.info("Room updated: {}", saved.getRoomNumber());
        return saved;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void deleteRoom(String id) {
        log.info("Deleting room: {}", id);

        if (!roomRepository.existsById(id)) {
            throw new IllegalArgumentException("Room not found: " + id);
        }

        roomRepository.deleteById(id);
        log.info("Room deleted: {}", id);
    }

    /** {@inheritDoc} */
    @Override
    public List<Room> searchRooms(RoomSearchCriteria criteria) {
        log.debug("Searching rooms with criteria: {}", criteria);
        return roomRepository.searchRooms(
                criteria.getRoomType(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getMinCapacity(),
                criteria.getFloorNumber(),
                criteria.isAvailableOnly()
        );
    }
}
