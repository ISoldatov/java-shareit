package ru.practicum.shareit.item.dto;


import ru.practicum.shareit.item.model.Item;

public final class ItemMapper {

    public static Item toItem(NewItemDto newItemDto, int ownerId) {
        return Item.builder()
                .id(null)
                .name(newItemDto.getName())
                .description(newItemDto.getDescription())
                .available(true)
                .ownerId(ownerId)
                .requestId(null)
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
