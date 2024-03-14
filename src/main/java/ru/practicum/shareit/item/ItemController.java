package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;


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
    public ItemDto add(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("ItemController: POST /items, itemDto={}, X-Sharer-User-Id={}", itemDto, ownerId);
        return itemService.add(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable int itemId,
                          @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("ItemController: PATCH /items/{}, itemDto={}, X-Sharer-User-Id={}", itemId, itemDto, ownerId);
        return itemService.update(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int ownerId ) {
        log.info("ItemController: GET /items/{}, X-Sharer-User-Id={}", itemId, ownerId);
        return itemService.get(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getOwnersItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("ItemController: GET /items, X-Sharer-User-Id={}", ownerId);
        return itemService.getOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam String text) {
        log.info("ItemController: GET /items/search?text={}", text);
        return itemService.find(text);
    }
}
