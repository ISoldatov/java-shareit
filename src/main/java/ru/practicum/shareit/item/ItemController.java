package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.UpdItemDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.*;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto add(@Valid @RequestBody NewItemDto newItemDto, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("ItemController: POST /items, itemDto={}, X-Sharer-User-Id={}", newItemDto, ownerId);
        return toItemDto(itemService.add(newItemDto, ownerId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Valid @RequestBody UpdItemDto updItemDto,
                          @PathVariable int itemId,
                          @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("ItemController: PATCH /items/{}, itemDto={}, X-Sharer-User-Id={}", itemId, updItemDto, ownerId);
        return toItemDto(itemService.update(updItemDto, itemId, ownerId));
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable int itemId) {
        log.info("ItemController: GET /items/{}", itemId);
        return toItemDto(itemService.get(itemId));
    }

    @GetMapping
    public List<ItemDto> getOwnersItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("ItemController: GET /items, X-Sharer-User-Id={}", ownerId);
        return itemService.getOwnerItems(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam String text) {
        log.info("ItemController: GET /items/search?text={}", text);
        return itemService.find(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
