package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.mapToItem;
import static ru.practicum.shareit.item.dto.ItemMapper.mapToItemDto;
import static ru.practicum.shareit.util.ValidationUtil.checkNotFound;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final BookingService bookingService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(BookingService bookingService, ItemRepository itemRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.bookingService = bookingService;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto add(ItemDto itemDto, int ownerId) {
        log.info("ItemService: add({},{})", itemDto, ownerId);
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Владелец c id=%d не найден.", ownerId)));
        return mapToItemDto(itemRepository.save(mapToItem(itemDto, owner, null)), null, null, List.of());
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
        return getItemDtoWithBookingAndComments(itemRepository.save(itemInBase), ownerId);
    }

    @Override
    public ItemDto get(int itemId, int ownerId) {
        log.info("ItemService: get({})", itemId);
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден.", ownerId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item c id=%d не найден.", itemId)));
        return getItemDtoWithBookingAndComments(item, ownerId);
    }

    @Override
    public List<ItemDto> getOwnerItems(int ownerId) {
        log.info("ItemService: getOwnerItems({})", ownerId);
        checkNotFound(userRepository.getReferenceById(ownerId), String.valueOf(ownerId));
        List<Item> items = new ArrayList<>(itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId));
        return items.stream()
                .map(item -> getItemDtoWithBookingAndComments(item, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> find(String text) {
        log.info("ItemService: find({})", text);
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemRepository.findByText(text).stream()
                    .map(item -> getItemDtoWithBookingAndComments(item, item.getOwner().getId()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto addComment(int itemId, int authorId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ValidationException(String.format("Item c id=%d не найден.", itemId)));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден.", authorId)));
        bookingService.getAllUserBookings("PAST", authorId).stream()
                .filter(b -> b.getItem().getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new ValidationException("Автор не брал вещь в аренду."));
        Comment comment = CommentMapper.mapToComment(commentDto, item, author, LocalDateTime.now());
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));

    }

    private ItemDto getItemDtoWithBookingAndComments(Item item, int ownerId) {
        List<CommentDto> commentsDto = commentRepository.findAllByItemIdOrderByIdDesc(item.getId()).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
        if (item.getOwner().getId() != ownerId) {
            return mapToItemDto(item, null, null, commentsDto);
        }
        LocalDateTime currentTime = LocalDateTime.now();
        BookingItemDto lastBooking = bookingService.getLastItemBooking(item.getId(), ownerId, currentTime);
        BookingItemDto nextBooking = bookingService.getNextItemBooking(item.getId(), ownerId, currentTime);
        return mapToItemDto(item, lastBooking, nextBooking, commentsDto == null ? List.of() : commentsDto);
    }
}
