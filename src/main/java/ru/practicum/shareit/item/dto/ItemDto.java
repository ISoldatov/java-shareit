package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class ItemDto {

    @Min(value = 1, message = "Id должно быть положительным.")
    private Integer id;

    @NotBlank(message = "Название предмета не может быть пустым.")
    private final String name;

    @NotBlank(message = "Описание предмета не может быть пустым.")
    @Size(max = 1000, message = "Длина имени должна быть от до 1000 символов.")
    private final String description;

    @NotNull
    private final Boolean available;

    private BookingItemDto lastBooking;

    private BookingItemDto nextBooking;

    private List<CommentDto> comments;
}
