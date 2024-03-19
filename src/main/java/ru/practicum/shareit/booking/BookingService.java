package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto add(NewBookingDto newBookingDto, int bookingId);

    BookingDto update(int bookingId, boolean approved, int ownerId);

    BookingDto get(int bookingId, int ownerId);

    List<BookingDto> getAllUserBookings(String state, int userId);

    List<BookingDto> getAllOwnerBookings(String state, int ownerId);

    BookingItemDto getLastItemBooking(int itemId, int ownerId, LocalDateTime currentTime);

    BookingItemDto getNextItemBooking(int itemId, int ownerId, LocalDateTime currentTime);
}
