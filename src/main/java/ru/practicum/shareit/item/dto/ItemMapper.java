package ru.practicum.shareit.item.dto;


import ru.practicum.shareit.item.model.Item;

public final class ItemMapper {

    public static Item mapToItem(ItemDto itemDto, int ownerId) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(true)
                .ownerId(ownerId)
                .requestId(null)
                .build();
    }

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
