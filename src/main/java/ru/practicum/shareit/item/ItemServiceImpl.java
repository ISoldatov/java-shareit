package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.UpdItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.ValidationUtil;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;

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
    public Item add(NewItemDto newItemDto, int ownerId) {
        log.info("ItemService: add({},{})", newItemDto, ownerId);
        ValidationUtil.checkNotFound(userRepository.get(ownerId), String.valueOf(ownerId));
        return itemRepository.save(ItemMapper.toItem(newItemDto, ownerId));
    }

    @Override
    public Item update(UpdItemDto updItemDto, int itemId, int ownerId) {
        log.info("ItemService: update({},{}, {})", updItemDto, itemId, ownerId);
        Item myItem = get(itemId);
        if (ownerId != myItem.getOwnerId()) {
            throw new NotFoundException("Пользователь не является владельцем.");
        }
        if (updItemDto.getName() != null) {
            myItem.setName(updItemDto.getName());
        }
        if (updItemDto.getDescription() != null) {
            myItem.setDescription(updItemDto.getDescription());
        }
        if (updItemDto.getAvailable() != null) {
            myItem.setAvailable(updItemDto.getAvailable());
        }
        return itemRepository.update(myItem);
    }

    @Override
    public Item get(int itemId) {
        log.info("ItemService: get({})", itemId);
        return ValidationUtil.checkNotFoundWithId(itemRepository.get(itemId), itemId, "item");
    }

    @Override
    public List<Item> getOwnerItems(int ownerId) {
        log.info("ItemService: getOwnerItems({})", ownerId);
        ValidationUtil.checkNotFound(userRepository.get(ownerId), String.valueOf(ownerId));
        return itemRepository.getOwnerItems(ownerId);
    }

    @Override
    public List<Item> find(String text) {
        log.info("ItemService: find({})", text);
        return itemRepository.find(text);
    }
}
