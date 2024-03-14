package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Builder
public final class BookingMapper {

    public static Booking mapToBooking(NewBookingDto newBookingDto, User booker, Item item) {
        return Booking.builder()
                .start(newBookingDto.start)
                .end(newBookingDto.end)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }


}
