package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private final Integer id;
    private final String name;
    private final String description;
    private final Boolean available;
}
