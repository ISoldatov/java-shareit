package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByIdDesc(int bookerId);

    List<Booking> findAllByBookerIdAndStatusOrderByIdDesc(int bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByIdDesc(int bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(int bookerId,
                                                                         LocalDateTime dateTime1,
                                                                         LocalDateTime dateTime2);

    List<Booking> findAllByBookerIdAndStartAfterOrderByIdDesc(int bookerId, LocalDateTime dateTime);

    @Query(value = "SELECT b.* " +
            "         FROM bookings b " +
            "        INNER JOIN items i ON i.id = b.item_id AND i.owner_id =?1 " +
            "        ORDER BY b.id DESC;",
            nativeQuery = true)
    List<Booking> getAllBookingsOwnerItems(int ownerId);

    @Query(value = "SELECT b.* " +
            "         FROM bookings b " +
            "        INNER JOIN items i ON i.id = b.item_id AND i.owner_id =?1 " +
            "        WHERE b.status =?2" +
            "        ORDER BY b.id DESC;",
            nativeQuery = true)
    List<Booking> getBookingsOwnerItemsByState(int ownerId, String state);

    @Query(value = "SELECT b.* " +
            "         FROM bookings b " +
            "        INNER JOIN items i ON i.id = b.item_id AND i.owner_id =?1 " +
            "        WHERE b.start_date > CURRENT_TIMESTAMP" +
            "        ORDER BY b.id DESC;",
            nativeQuery = true)
    List<Booking> getFutureBookingsItemsOwner(int ownerId);

    @Query(value = "SELECT b.*" +
            "         FROM bookings b" +
            "        INNER JOIN items i ON i.id = b.item_id AND i.id=?1 " +
            "        WHERE b.status !='REJECTED' " +
            "          AND b.start_date < CURRENT_TIMESTAMP " +
            "        ORDER BY b.id ASC " +
            "        LIMIT 1;",
            nativeQuery = true)
    Booking getLastItemBooking(int itemId, int ownerId);

    @Query(value = "SELECT b.*" +
            "         FROM bookings b" +
            "        INNER JOIN items i ON i.id = b.item_id AND i.id=?1 " +
            "        WHERE b.status !='REJECTED' " +
            "          AND b.start_date > CURRENT_TIMESTAMP " +
            "        ORDER BY b.id DESC " +
            "        LIMIT 1;",
            nativeQuery = true)
    Booking getNextItemBooking(int itemId, int ownerId);



}
