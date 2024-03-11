package ru.practicum.shareit.item.model;

import lombok.*;

@Data
@Builder
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private final Integer ownerId;
    private Integer requestId;
}
