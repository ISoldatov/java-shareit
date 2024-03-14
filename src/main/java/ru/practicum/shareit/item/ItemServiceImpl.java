package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import static ru.practicum.shareit.item.dto.ItemMapper.*;
import static ru.practicum.shareit.util.ValidationUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingService bookingService) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingService = bookingService;
    }

    @Override
    public ItemDto add(ItemDto itemDto, int ownerId) {
        log.info("ItemService: add({},{})", itemDto, ownerId);
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Владелец c id=%d не найден.", ownerId)));
        return mapToItemDto(itemRepository.save(mapToItem(itemDto, owner, null)), null, null);
    }

    @Override
    public ItemDto update(ItemDto itemDto, int itemId, int ownerId) {
        log.info("ItemService: update({},{}, {})", itemDto, itemId, ownerId);
        Item itemInBase = itemRepository.findById(itemId)
                .orElseThrow(() -> new ValidationException(String.format("Item c id=%d не найден.", itemId)));
        if (ownerId != itemInBase.getOwner().getId()) {
            throw new NotFoundException("Пользователь не является владельцем.");
        }
        if (itemDto.getName() != null) {
            itemInBase.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemInBase.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemInBase.setAvailable(itemDto.getAvailable());
        }
        return mapToItemDto(itemRepository.save(itemInBase), null, null);
    }

    @Override
    public ItemDto get(int itemId, int ownerId) {
        log.info("ItemService: get({})", itemId);
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден.", ownerId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item c id=%d не найден.", itemId)));
        if (item.getOwner().getId() != ownerId) {
            return mapToItemDto(item, null, null);
        }
        BookingItemDto lastBooking = bookingService.getLastItemBooking(itemId, ownerId);
        BookingItemDto nextBooking = bookingService.getNextItemBooking(itemId, ownerId);
        return mapToItemDto(item, lastBooking, nextBooking);
    }

    @Override
    public List<ItemDto> getOwnerItems(int ownerId) {
        log.info("ItemService: getOwnerItems({})", ownerId);
        checkNotFound(userRepository.getReferenceById(ownerId), String.valueOf(ownerId));
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId).stream()
                .collect(Collectors.toList());
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item i: items) {
            BookingItemDto lastBooking = bookingService.getLastItemBooking(i.getId(), ownerId);
            BookingItemDto nextBooking = bookingService.getNextItemBooking(i.getId(), ownerId);
            ItemDto itemDto = mapToItemDto(i, lastBooking, nextBooking);
            itemsDto.add(itemDto);
        }
        return itemsDto;

//        return items.stream().map(item -> {
//                    BookingDto lastBooking = bookingService.getLastItemBooking(item.getId(), ownerId);
//                    BookingDto nextBooking = bookingService.getNextItemBooking(item.getId(), ownerId);
//                    return mapToItemDto(item, lastBooking, nextBooking);
//                })
//                .collect(Collectors.toList());

//        List<Item> items= itemRepository.findAllByOwnerId(ownerId).stream()
//                .map(i -> mapToItemDto(i, null, null))
//                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> find(String text) {
        log.info("ItemService: find({})", text);
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemRepository.findByText(text).stream()
                    .map(i -> mapToItemDto(i, null, null))
                    .collect(Collectors.toList());
        }
    }
}
