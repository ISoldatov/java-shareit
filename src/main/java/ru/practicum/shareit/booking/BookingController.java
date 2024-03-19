package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private BookingService bookingService;

    @Autowired
    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto add(@Valid @RequestBody NewBookingDto newBookingDto, @RequestHeader("X-Sharer-User-Id") int bookerId) {
        log.info("BookingController: POST /bookings, NewBookingDto={}, X-Sharer-User-Id={}", newBookingDto, bookerId);
        return bookingService.add(newBookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable int bookingId, @RequestParam boolean approved,
                             @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("BookingController: PATCH /bookings/{}?approved={}, X-Sharer-User-Id={}", bookingId, approved, ownerId);
        return bookingService.update(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("BookingController: GET /bookings/{}, X-Sharer-User-Id={}", bookingId, ownerId);
        return bookingService.get(bookingId, ownerId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                               @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("BookingController: GET /bookings?state={}, X-Sharer-User-Id={}", state, userId);
        return bookingService.getAllUserBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("BookingController: GET /bookings/owner?state={}, X-Sharer-User-Id={}", state, ownerId);
        return bookingService.getAllOwnerBookings(state, ownerId);
    }
}
