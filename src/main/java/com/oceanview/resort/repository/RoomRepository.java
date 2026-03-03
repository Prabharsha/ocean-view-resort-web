package com.oceanview.resort.repository;

import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for {@link Room} entity.
 * Provides standard CRUD operations and custom query methods for rooms.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    /**
     * Finds all rooms ordered by creation date descending (newest first).
     */
    List<Room> findAllByOrderByCreatedAtDesc();

    /**
     * Finds a room by its unique room number.
     */
    Optional<Room> findByRoomNumber(String roomNumber);

    /**
     * Finds all rooms of a given type.
     */
    List<Room> findByRoomType(RoomType roomType);

    /**
     * Finds all currently available rooms.
     */
    List<Room> findByIsAvailableTrue();

    /**
     * Finds rooms that are available (no overlapping non-cancelled reservations)
     * for the given date range, optionally filtered by room type.
     */
    @Query("SELECT r FROM Room r WHERE r.isAvailable = true " +
           "AND (:roomType IS NULL OR r.roomType = :roomType) " +
           "AND r.id NOT IN (" +
           "  SELECT res.room.id FROM Reservation res " +
           "  WHERE res.status <> com.oceanview.resort.model.enums.ReservationStatus.CANCELLED " +
           "  AND res.checkInDate < :checkOut " +
           "  AND res.checkOutDate > :checkIn" +
           ")")
    List<Room> findAvailableRooms(@Param("checkIn") LocalDate checkIn,
                                  @Param("checkOut") LocalDate checkOut,
                                  @Param("roomType") RoomType roomType);

    /**
     * Advanced multi-criteria room search with optional filters.
     * All filter parameters are optional (pass null to skip).
     *
     * @param roomType     optional room type filter
     * @param minPrice     optional minimum rate per night
     * @param maxPrice     optional maximum rate per night
     * @param minCapacity  optional minimum guest capacity
     * @param floorNumber  optional floor number filter
     * @param availableOnly if true, only available rooms are returned
     * @return list of matching rooms ordered by rate per night ascending
     */
    @Query("SELECT r FROM Room r WHERE " +
           "(:roomType IS NULL OR r.roomType = :roomType) " +
           "AND (:minPrice IS NULL OR r.ratePerNight >= :minPrice) " +
           "AND (:maxPrice IS NULL OR r.ratePerNight <= :maxPrice) " +
           "AND (:minCapacity IS NULL OR r.capacity >= :minCapacity) " +
           "AND (:floorNumber IS NULL OR r.floorNumber = :floorNumber) " +
           "AND (:availableOnly = false OR r.isAvailable = true) " +
           "ORDER BY r.ratePerNight ASC")
    List<Room> searchRooms(@Param("roomType") RoomType roomType,
                           @Param("minPrice") BigDecimal minPrice,
                           @Param("maxPrice") BigDecimal maxPrice,
                           @Param("minCapacity") Integer minCapacity,
                           @Param("floorNumber") Integer floorNumber,
                           @Param("availableOnly") boolean availableOnly);

    /**
     * Checks if a room number already exists.
     */
    boolean existsByRoomNumber(String roomNumber);

    /**
     * Counts rooms by availability status.
     */
    long countByIsAvailableTrue();
}
