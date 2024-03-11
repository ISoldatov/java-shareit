package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import static ru.practicum.shareit.item.dto.ItemMapper.*;
import static ru.practicum.shareit.util.ValidationUtil.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto add(ItemDto itemDto, int ownerId) {
        log.info("ItemService: add({},{})", itemDto, ownerId);
        checkNotFound(userRepository.getReferenceById(ownerId), String.valueOf(ownerId));
        return mapToItemDto(itemRepository.save(mapToItem(itemDto, null, null)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, int itemId, int ownerId) {
        log.info("ItemService: update({},{}, {})", itemDto, itemId, ownerId);
        Item itemInBase = itemRepository.get(itemId);
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
        return mapToItemDto(itemRepository.update(itemInBase));
    }

    @Override
    public ItemDto get(int itemId) {
        log.info("ItemService: get({})", itemId);
        return mapToItemDto(checkNotFoundWithId(itemRepository.get(itemId), itemId, "item"));
    }

    @Override
    public List<ItemDto> getOwnerItems(int ownerId) {
        log.info("ItemService: getOwnerItems({})", ownerId);
        checkNotFound(userRepository.getReferenceById(ownerId), String.valueOf(ownerId));
        return itemRepository.getOwnerItems(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> find(String text) {
        log.info("ItemService: find({})", text);
        return itemRepository.find(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
