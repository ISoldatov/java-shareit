package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item add(NewItemDto newItemDto, int ownerId);

    Item update(UpdItemDto updItemDto, int itemId, int ownerId);

    Item get(int itemId);

    List<Item> getOwnerItems(int ownerId);

    List<Item> find(String text);
}
