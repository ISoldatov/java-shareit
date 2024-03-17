package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.UnsupportedStateException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.*;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto add(NewBookingDto newBookingDto, int bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден.", bookerId)));
        Item item = itemRepository.findById(newBookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь c id=%d не найден.", newBookingDto.getItemId())));
        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("Владелец не может взять в аренду свою вещь.");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для аренды.");
        }
        if (newBookingDto.getStart().isAfter(newBookingDto.getEnd()) ||
                newBookingDto.getStart().isEqual(newBookingDto.getEnd())) {
            throw new ValidationException("Дата начала аренды должна быть до даты ее окончания.");
        }
        Booking booking = BookingMapper.mapToBooking(newBookingDto, booker, item);
        return mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(int bookingId, boolean approved, int ownerId) {
        Booking bookingInBase = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Аренда c id=%d не найдена.", bookingId)));
        if (bookingInBase.getItem().getOwner().getId() != ownerId) {
            throw new NotFoundException(String.format("Вещь не пренадлежит пользователю c id=%d.", ownerId));
        }
        if (bookingInBase.getStatus() != BookingStatus.WAITING && approved) {
            throw new ValidationException(String.format("По заказу уже принято решение %s.", bookingInBase.getStatus().toString()));
        }
        bookingInBase.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapToBookingDto(bookingRepository.save(bookingInBase));
    }

    @Override
    public BookingDto get(int bookingId, int ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден.", ownerId)));
        Booking bookingInBase = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Аренда c id=%d не найдена.", bookingId)));
        if (bookingInBase.getItem().getOwner().getId() != ownerId && bookingInBase.getBooker().getId() != ownerId) {
            throw new NotFoundException(String.format("Нет прав для просмотра заказа c id=%d.", bookingId));
        }
        return mapToBookingDto(bookingInBase);
    }

    @Override
    public List<BookingDto> getAllUserBookings(String state, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден.", userId)));
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        try {
            BookingState bookingState = BookingState.valueOf(state);
            switch (bookingState) {
                case ALL:
                    bookings = bookingRepository.findAllByBookerIdOrderByIdDesc(userId);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByBookerIdAndStatusOrderByIdDesc(userId, BookingStatus.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByBookerIdAndStatusOrderByIdDesc(userId, BookingStatus.REJECTED);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByIdDesc(userId, currentTime);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(userId, currentTime, currentTime);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByIdDesc(userId, currentTime);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllOwnerBookings(String state, int ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден.", ownerId)));
        List<Booking> bookings = new ArrayList<>();
        try {
            BookingState bookingState = BookingState.valueOf(state);
            switch (bookingState) {
                case ALL:
                    bookings = bookingRepository.getAllBookingsOwnerItems(ownerId);
                    break;
                case PAST:
                    bookings = bookingRepository.getPastBookingsItemsOwner(ownerId, LocalDateTime.now());
                    break;
                case CURRENT:
                    bookings = bookingRepository.getCurrentBookingsItemsOwner(ownerId, LocalDateTime.now());
                    break;
                case FUTURE:
                    bookings = bookingRepository.getFutureBookingsItemsOwner(ownerId, LocalDateTime.now());
                    break;
                case WAITING:
                    bookings = bookingRepository.getBookingsOwnerItemsByState(ownerId, BookingStatus.WAITING.toString());
                    break;
                case REJECTED:
                    bookings = bookingRepository.getBookingsOwnerItemsByState(ownerId, BookingStatus.REJECTED.toString());
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingItemDto getLastItemBooking(int itemId, int ownerId, LocalDateTime currentTime) {
        Booking booking = bookingRepository.getLastItemBooking(itemId, currentTime);
        return booking != null ? mapToBookingItemDto(booking) : null;
    }

    @Override
    public BookingItemDto getNextItemBooking(int itemId, int ownerId, LocalDateTime currentTime) {
        Booking booking = bookingRepository.getNextItemBooking(itemId, currentTime);
        return booking != null ? mapToBookingItemDto(booking) : null;
    }
}
