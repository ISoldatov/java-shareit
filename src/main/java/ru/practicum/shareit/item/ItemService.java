package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(ItemDto itemDto, int ownerId);

    ItemDto update(ItemDto itemDto, int itemId, int ownerId);

    ItemDto get(int itemId);

    List<ItemDto> getOwnerItems(int ownerId);

    List<ItemDto> find(String text);
}
