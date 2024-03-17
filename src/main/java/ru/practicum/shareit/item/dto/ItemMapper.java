package ru.practicum.shareit.item.dto;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public final class ItemMapper {

    public static Item mapToItem(ItemDto itemDto, User owner, ItemRequest request) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(true)
                .owner(owner)
                .request(request)
                .build();
    }

    public static ItemDto mapToItemDto(Item item, BookingItemDto lastBooking, BookingItemDto nextBooking, List<CommentDto> commentsDto) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentsDto)
                .build();
    }
}
